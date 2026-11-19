/*
 * fydroid-rt lmkd — PSI-driven low memory killer for the ARC++ container.
 *
 * This used to be a pure stub: it spoke the ActivityManager protocol, threw
 * every command away and never killed anything.  The stated reasons were that
 * "the standard AOSP lmkd cannot write /proc/<pid>/oom_score_adj due to user
 * namespace restrictions" and that "Chrome OS handles memory pressure via its
 * own tab discard and OOM killer mechanisms".  Neither holds:
 *
 *  - The container runs with a full capability set in its own PID namespace;
 *    writing another process's oom_score_adj and kill(2) both succeed.
 *  - The Chrome OS discarder only reclaims *Chrome tabs*.  Nothing on the
 *    system ever shed Android memory, so the container accumulated cached apps
 *    without bound.  On a 3.5 GB board that ended in 2.1 GB pushed into zram
 *    and memory PSI "full avg10" at 34% — every Android app crawling while
 *    Chrome, which has its own discarder, stayed responsive.  On an otherwise
 *    identical 7.8 GB board pressure never builds, which is why the missing
 *    reclaim went unnoticed.
 *
 * So this daemon now does the job stock lmkd would:
 *
 *  - tracks the ActivityManager's per-process oom_score_adj (LMK_PROCPRIO) and
 *    reflects it into /proc/<pid>/oom_score_adj, which additionally steers the
 *    *host* kernel OOM killer towards Android cached apps rather than picking a
 *    victim blind;
 *  - arms PSI memory stall triggers on /proc/pressure/memory and, when the
 *    system stalls on memory, kills the least important tracked process;
 *  - reports kills back to the ActivityManager (LMK_GETKILLCNT, and the
 *    LMK_PROCKILL async event for LMK_SUBSCRIBE clients).
 *
 * A stall on its own is not evidence of an app-memory shortage — a large file
 * copy stalls tasks on page cache just as effectively — so kills are gated on a
 * genuine shortage.  The gate is two-part, and both parts matter:
 *
 *   - MemAvailable must actually be low.  A hard floor (max(6%, 200 MB)) kills
 *     on its own; above that, but under mem_avail_rate_gate_pct of MemTotal,
 *     the board counts as short enough for paging rates to mean anything.
 *   - and something must actually be paging: swap-in, file refaults, or a
 *     complete stall with the working set already displaced into swap.
 *
 * The first version of this daemon made those alternatives rather than
 * conjuncts — any swap-in at all was enough, whatever MemAvailable said — and
 * on device that killed 44 processes in 19.5 h without MemAvailable ever dropping
 * below 1.62 GB of 3.5 GB.  The thresholds below carry the measurements.
 *
 * Everything is tunable at runtime through the usual ro.lmk.* properties, so a
 * board that wants different aggressiveness does not need a new binary.
 */

#include <arpa/inet.h>
#include <errno.h>
#include <fcntl.h>
#include <dirent.h>
#include <limits.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/epoll.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <unistd.h>

#include <map>
#include <unordered_map>

#define LOG_TAG "arc-lowmemorykiller"

#include <cutils/properties.h>
#include <cutils/sockets.h>
#include <lmkd.h>
#include <log/log.h>
#include <psi/psi.h>

#define MAX_DATA_CONN 3

/* Not exported by lmkd.h; AOSP's lmkd.cpp defines these privately as well. */
#define OOM_SCORE_ADJ_MIN (-1000)
#define OOM_SCORE_ADJ_MAX 1000

static void update_props();

enum vmpressure_level {
    VMPRESS_LEVEL_LOW,
    VMPRESS_LEVEL_MEDIUM,
    VMPRESS_LEVEL_CRITICAL,
    VMPRESS_LEVEL_COUNT,
};

static const char* const level_name[VMPRESS_LEVEL_COUNT] = {"low", "medium", "critical"};

/*
 * epoll tags.  A non-negative tag is an index into data_sock[]; the singletons
 * below are negative so the two spaces cannot collide.
 */
#define TAG_CTRL_SOCK (-1)
#define TAG_PSI(level) (-2 - (level))
#define TAG_TO_PSI_LEVEL(tag) (-2 - (tag))

#define MAX_EPOLL_EVENTS (1 + MAX_DATA_CONN + VMPRESS_LEVEL_COUNT)

/* Stall thresholds, matching AOSP lmkd's PSI monitor configuration. */
static const struct {
    enum psi_stall_type stall_type;
    int threshold_ms;
} psi_thresholds[VMPRESS_LEVEL_COUNT] = {
    {PSI_SOME, 70},  /* 70ms of partial stall per second  */
    {PSI_SOME, 100}, /* 100ms of partial stall per second */
    {PSI_FULL, 70},  /* 70ms of complete stall per second */
};

#define PSI_WINDOW_SIZE_MS 1000

/*
 * Least-important-first: at pressure level N we only consider processes whose
 * oom_score_adj is at or above this floor.  Even the critical floor stays above
 * everything the ActivityManager considers live — foreground (0), visible
 * (100), perceptible (200), services (500), the launcher/home (600) and the
 * active IME — because losing one of those is user-visible in a way that losing
 * a cached app is not.  Defaults are the AOSP adj bucket boundaries:
 * CACHED_APP_MIN_ADJ, SERVICE_B_ADJ, PREVIOUS_APP_ADJ.
 */
static int level_oomadj[VMPRESS_LEVEL_COUNT] = {900, 800, 700};

/* Never kill anything the ActivityManager still considers perceptible. */
#define MIN_KILLABLE_OOMADJ 300

/*
 * Gate: how much swap-in traffic counts as "the working set no longer fits".
 *
 * These were originally set to 100 pages/s (400 kB/s) on the assumption that
 * ordinary operation is "essentially zero".  Measured on device (3.5 GB amd64)
 * that assumption is wrong by two orders of magnitude.  Sampling /proc/vmstat
 * every 5 s while simply cold-launching four apps, with 2.0 GB MemAvailable and
 * the board under no distress whatsoever:
 *
 *     swapin  peak    114 pages/s      (idle: 0-6)
 *     refault peak   6782 pages/s      (idle: 0-41), with 1950 / 1073 / 455
 *                                      pages/s in other samples
 *
 * The ARC rootfs is squashfs and Chrome OS always keeps zram populated, so
 * every cold code page read back is a refault and every touched swapped page is
 * a swap-in.  At 100 pages/s both gates are open essentially whenever the user
 * does anything, which is why this daemon killed 44 processes in 19.5 h with
 * MemAvailable never below 1.62 GB.
 *
 * So the thresholds now sit above measured normal-operation peaks.  They are a
 * backstop, not the discriminator: mem_avail_rate_gate_pct below is what
 * actually decides whether a rate means anything.
 */
static int swapin_thresh_pages_per_sec = 500;

/* ...or the file working set being re-read as fast as it is evicted. */
static int refault_thresh_pages_per_sec = 8000;

/*
 * A paging *rate* only means "the working set no longer fits" when there is
 * nothing left to give.  Above this fraction of MemTotal still available, a
 * stall is something else — a cold page cache, a large file copy, an app
 * starting -- and killing an app neither fixes it nor is justified by it.
 * Below it, the board is genuinely short and the rates above become evidence.
 *
 * Shares its default with swap_path_avail_pct, which expresses the same
 * "not comfortable" line for the complete-stall signal; both stay separately
 * tunable because a board may want to reach for one before the other.
 */
static int mem_avail_rate_gate_pct = 33;

/* ...or MemAvailable below max(pct of MemTotal, floor). */
static int mem_avail_floor_pct = 6;
static int mem_avail_floor_min_kb = 200 * 1024;

/*
 * ...or, on a complete stall, this much of RAM already displaced into swap while
 * MemAvailable is itself under swap_path_avail_pct.  See should_kill() for why a
 * stall with no paging in flight still counts, and why it needs the second half.
 */
static int swap_used_pct = 10;
static int swap_path_avail_pct = 33;

/* Pace kills so a single stall burst cannot empty the process table. */
static int kill_cooldown_ms = 300;
static int max_kills_per_event = 2;

static bool debug_enabled = false;

struct proc_record {
    uid_t uid;
    int oomadj;
    enum proc_type ptype;
};

static int epollfd = -1;
static int ctrl_sock = -1;
static int data_sock[MAX_DATA_CONN];
static int data_sock_cnt;
/* Bitmask of async_event_type each connection subscribed to. */
static unsigned int data_sock_subs[MAX_DATA_CONN];
static int psi_fd[VMPRESS_LEVEL_COUNT];

static std::unordered_map<pid_t, proc_record> procs;
/*
 * pids we have just SIGKILLed.  A task does not leave /proc the instant it is
 * signalled -- it lingers while it tears down, then as a zombie until its parent
 * reaps it -- so without this the next pass in the same pressure burst picks the
 * same corpse again and spends the kill budget on a process that frees nothing.
 * Only the /proc search actually needs this (the tracked table drops a victim on
 * the spot), but it is cheap and keeps both paths honest.
 */
static std::unordered_map<pid_t, int64_t> recently_killed;
#define KILL_GRACE_MS 10000
/* oom_score_adj -> number of processes we killed at that adj, for GETKILLCNT. */
static std::map<int, int> killcnt;

static long page_size_kb;

static int64_t now_ms() {
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return (int64_t)ts.tv_sec * 1000 + ts.tv_nsec / 1000000;
}

/* ------------------------------------------------------------------ */
/* /proc helpers                                                       */
/* ------------------------------------------------------------------ */

/* A snapshot of every signal the kill gate reasons about. */
struct mem_state {
    long total_kb;
    long avail_kb;
    long swap_used_kb;
    unsigned long long swapin_rate;  /* pages/s faulted back in from swap */
    unsigned long long refault_rate; /* pages/s of file working set re-read */
};

/* Reads the fields we need out of /proc/meminfo in one pass. */
static bool read_meminfo(struct mem_state* m) {
    FILE* f = fopen("/proc/meminfo", "re");
    if (!f) {
        return false;
    }

    char line[256];
    long total = -1, avail = -1, swap_total = -1, swap_free = -1;
    while (fgets(line, sizeof(line), f)) {
        long val;
        if (total < 0 && sscanf(line, "MemTotal: %ld kB", &val) == 1) {
            total = val;
        } else if (avail < 0 && sscanf(line, "MemAvailable: %ld kB", &val) == 1) {
            avail = val;
        } else if (swap_total < 0 && sscanf(line, "SwapTotal: %ld kB", &val) == 1) {
            swap_total = val;
        } else if (swap_free < 0 && sscanf(line, "SwapFree: %ld kB", &val) == 1) {
            swap_free = val;
        }
    }
    fclose(f);

    if (total < 0 || avail < 0) {
        return false;
    }
    m->total_kb = total;
    m->avail_kb = avail;
    /* A board with swap disabled simply never trips the swap-based signals. */
    m->swap_used_kb = (swap_total > 0 && swap_free >= 0) ? swap_total - swap_free : 0;
    return true;
}

/*
 * Cumulative counters behind the two "we are actually paging" signals.  The
 * refault counter was split into _file/_anon in 5.9; fall back to the older
 * combined name so this still works on the arm64 boards' kernels.
 */
static bool read_vmstat(unsigned long long* pswpin, unsigned long long* refault_file) {
    FILE* f = fopen("/proc/vmstat", "re");
    if (!f) {
        return false;
    }

    char line[128];
    bool got_swap = false, got_refault = false;
    *pswpin = 0;
    *refault_file = 0;
    while (fgets(line, sizeof(line), f)) {
        unsigned long long val;
        if (!got_swap && sscanf(line, "pswpin %llu", &val) == 1) {
            *pswpin = val;
            got_swap = true;
        } else if (sscanf(line, "workingset_refault_file %llu", &val) == 1) {
            *refault_file = val;
            got_refault = true;
        } else if (!got_refault && sscanf(line, "workingset_refault %llu", &val) == 1) {
            *refault_file = val;
            got_refault = true;
        }
    }
    fclose(f);
    return got_swap || got_refault;
}

/*
 * Fills in the rate fields by differencing against the previous sample.  The
 * first call after startup cannot produce a rate, so it reports zero and the
 * gate falls through to the level-based signals.
 */
static void sample_rates(struct mem_state* m) {
    static unsigned long long last_pswpin, last_refault;
    static int64_t last_ms;

    m->swapin_rate = 0;
    m->refault_rate = 0;

    unsigned long long pswpin, refault;
    if (!read_vmstat(&pswpin, &refault)) {
        return;
    }

    int64_t now = now_ms();
    if (last_ms != 0 && now > last_ms) {
        unsigned long long elapsed_ms = (unsigned long long)(now - last_ms);
        if (pswpin >= last_pswpin) {
            m->swapin_rate = (pswpin - last_pswpin) * 1000 / elapsed_ms;
        }
        if (refault >= last_refault) {
            m->refault_rate = (refault - last_refault) * 1000 / elapsed_ms;
        }
    }

    last_pswpin = pswpin;
    last_refault = refault;
    last_ms = now;
}

static long proc_rss_kb(pid_t pid) {
    char path[64];
    snprintf(path, sizeof(path), "/proc/%d/statm", pid);
    FILE* f = fopen(path, "re");
    if (!f) {
        return -1;
    }
    long total_pages, rss_pages;
    int n = fscanf(f, "%ld %ld", &total_pages, &rss_pages);
    fclose(f);
    if (n != 2) {
        return -1;
    }
    return rss_pages * page_size_kb;
}

/* Best-effort process name for logging; falls back to the pid. */
static void proc_name(pid_t pid, char* buf, size_t len) {
    char path[64];
    snprintf(path, sizeof(path), "/proc/%d/cmdline", pid);
    int fd = open(path, O_RDONLY | O_CLOEXEC);
    if (fd >= 0) {
        ssize_t rd = TEMP_FAILURE_RETRY(read(fd, buf, len - 1));
        close(fd);
        if (rd > 0) {
            /* cmdline is NUL separated; the first token is the process name. */
            buf[rd] = '\0';
            if (buf[0] != '\0') {
                return;
            }
        }
    }
    snprintf(buf, len, "pid %d", pid);
}

static bool proc_uid(pid_t pid, uid_t* uid) {
    char path[64];
    snprintf(path, sizeof(path), "/proc/%d/status", pid);
    FILE* f = fopen(path, "re");
    if (!f) {
        return false;
    }
    char line[128];
    bool found = false;
    while (fgets(line, sizeof(line), f)) {
        unsigned int real_uid;
        if (sscanf(line, "Uid: %u", &real_uid) == 1) {
            *uid = (uid_t)real_uid;
            found = true;
            break;
        }
    }
    fclose(f);
    return found;
}

/* Returns the process's oom_score_adj, or INT_MIN if it cannot be read. */
static int proc_oomadj(pid_t pid) {
    char path[64];
    snprintf(path, sizeof(path), "/proc/%d/oom_score_adj", pid);
    FILE* f = fopen(path, "re");
    if (!f) {
        return INT_MIN;
    }
    int adj;
    int n = fscanf(f, "%d", &adj);
    fclose(f);
    return n == 1 ? adj : INT_MIN;
}

static bool proc_alive(pid_t pid) {
    char path[64];
    snprintf(path, sizeof(path), "/proc/%d", pid);
    return access(path, F_OK) == 0;
}

/*
 * Mirror the ActivityManager's adj into the kernel.  Besides keeping /proc
 * honest for anyone debugging, this is what lets the *host* OOM killer pick an
 * Android cached app instead of an arbitrary victim when the board runs out
 * from under us.
 *
 * Negative targets are skipped rather than attempted, because they cannot
 * succeed here.  The kernel refuses to take oom_score_adj below a process's
 * inherited oom_score_adj_min -- 0 for everything zygote forks -- unless the
 * caller holds CAP_SYS_RESOURCE *in the initial user namespace*.  The ARC
 * container lives in a nested one (uid_map 0 -> 655360), so no process inside
 * it can ever qualify, root with a full capability set included; a privileged
 * shell fails at this identically.  It costs us nothing: only the persistent
 * processes the framework wants pinned at a negative adj are affected, they
 * stay at 0 while cached apps still rise to 900+, so the ordering the host OOM
 * killer actually uses to choose a victim is preserved.
 */
static void write_oomadj(pid_t pid, int oomadj) {
    if (oomadj < 0) {
        return;
    }

    char path[64];
    char value[16];
    snprintf(path, sizeof(path), "/proc/%d/oom_score_adj", pid);
    int fd = open(path, O_WRONLY | O_CLOEXEC);
    if (fd < 0) {
        return;
    }
    int len = snprintf(value, sizeof(value), "%d", oomadj);
    if (TEMP_FAILURE_RETRY(write(fd, value, len)) < 0) {
        static bool warned = false;
        if (!warned) {
            warned = true;
            ALOGW("Cannot write oom_score_adj for pid %d: %s; kill decisions still "
                  "use the ActivityManager's values",
                  pid, strerror(errno));
        }
    }
    close(fd);
}

/* ------------------------------------------------------------------ */
/* Kill path                                                           */
/* ------------------------------------------------------------------ */

/*
 * Fire-and-forget: this is sent with MSG_DONTWAIT because the reader is the
 * ActivityManager, and the moment we most need to keep reclaiming is exactly
 * the moment it is most likely to be stalled on memory itself.  A blocking
 * write here would park the only thread that can free anything, on a socket
 * that only drains once memory frees up.  Losing a notification is cheap --
 * the framework still reaps the process through the usual child-death path --
 * so an unreadable socket is dropped rather than waited on.
 */
static void notify_kill(pid_t pid, uid_t uid) {
    LMKD_CTRL_PACKET packet;
    size_t len = lmkd_pack_set_prockills(packet, pid, uid);
    for (int i = 0; i < MAX_DATA_CONN; i++) {
        if (data_sock[i] < 0 || !(data_sock_subs[i] & (1u << LMK_ASYNC_EVENT_KILL))) {
            continue;
        }
        if (TEMP_FAILURE_RETRY(send(data_sock[i], packet, len, MSG_DONTWAIT)) < 0 &&
            errno != EAGAIN && errno != EWOULDBLOCK) {
            ALOGW("Failed to report kill on fd %d: %s", data_sock[i], strerror(errno));
        }
    }
}

/* True while a pid is still inside its post-SIGKILL grace window. */
static bool killed_recently(pid_t pid, int64_t now) {
    auto it = recently_killed.find(pid);
    if (it == recently_killed.end()) {
        return false;
    }
    if (now - it->second >= KILL_GRACE_MS) {
        recently_killed.erase(it);
        return false;
    }
    return true;
}

/*
 * Fallback victim search, straight from /proc.
 *
 * The tracked table can be empty through no fault of ours.  When lmkd restarts,
 * the ActivityManager's reconnect handler sends LMK_PROCPURGE and then does
 * *not* re-register anything -- it only pushes a process's adj when that adj next
 * changes -- so a restarted daemon is blind to everything already running, and
 * would stay blind until ARC itself restarts.  That is precisely when a board is
 * least able to afford no reclaim.
 *
 * Reading /proc back is sound rather than a guess: oom_score_adj is maintained
 * on this platform independently of lmkd, verified with the old do-nothing stub
 * running, where the values were still correct and current.  The adj floor keeps
 * this safe -- only cached apps are ever eligible, so seeing processes the
 * framework never registered cannot put anything important at risk.
 */
static pid_t scan_proc_victim(int min_oomadj, int* victim_adj, long* victim_rss_kb, uid_t* uid) {
    DIR* d = opendir("/proc");
    if (!d) {
        return -1;
    }

    pid_t self = getpid();
    int64_t now = now_ms();
    pid_t victim = -1;
    int best_adj = -1;
    long best_rss = -1;

    struct dirent* ent;
    while ((ent = readdir(d)) != nullptr) {
        char* end;
        long val = strtol(ent->d_name, &end, 10);
        if (*end != '\0' || val <= 1 || val > INT_MAX) {
            continue;
        }
        pid_t pid = (pid_t)val;
        if (pid == self) {
            continue;
        }

        int adj = proc_oomadj(pid);
        if (adj == INT_MIN || adj < min_oomadj || adj < MIN_KILLABLE_OOMADJ || adj < best_adj) {
            continue;
        }
        if (killed_recently(pid, now)) {
            continue;
        }

        /*
         * Nothing resident means nothing to reclaim: a kernel thread, or a task
         * already on its way out.  Killing it would free no memory and would
         * consume a slot that a real victim needs.
         */
        long rss = proc_rss_kb(pid);
        if (rss <= 0) {
            continue;
        }
        if (adj == best_adj && rss <= best_rss) {
            continue;
        }
        victim = pid;
        best_adj = adj;
        best_rss = rss;
    }
    closedir(d);

    if (victim > 0 && !proc_uid(victim, uid)) {
        *uid = 0;
    }
    *victim_adj = best_adj;
    *victim_rss_kb = best_rss;
    return victim;
}

/*
 * Pick the least important process at or above min_oomadj, breaking ties on
 * resident size so each kill frees the most.  The ActivityManager's own view
 * wins when we have one; /proc covers us when we do not.
 */
static pid_t find_victim(int min_oomadj, int* victim_adj, long* victim_rss_kb, uid_t* victim_uid) {
    int64_t now = now_ms();
    pid_t victim = -1;
    int best_adj = -1;
    long best_rss = -1;

    for (auto it = procs.begin(); it != procs.end();) {
        pid_t pid = it->first;
        if (!proc_alive(pid)) {
            it = procs.erase(it);
            continue;
        }

        int adj = it->second.oomadj;
        uid_t uid = it->second.uid;
        ++it;

        if (adj < min_oomadj || adj < MIN_KILLABLE_OOMADJ || adj < best_adj) {
            continue;
        }
        if (killed_recently(pid, now)) {
            continue;
        }

        long rss = proc_rss_kb(pid);
        if (rss <= 0) {
            continue;
        }
        if (adj == best_adj && rss <= best_rss) {
            continue;
        }
        victim = pid;
        best_adj = adj;
        best_rss = rss;
        *victim_uid = uid;
    }

    if (victim < 0) {
        return scan_proc_victim(min_oomadj, victim_adj, victim_rss_kb, victim_uid);
    }

    *victim_adj = best_adj;
    *victim_rss_kb = best_rss;
    return victim;
}

static bool kill_one(enum vmpressure_level level, long mem_avail_kb, const char* reason) {
    int adj;
    long rss_kb;
    uid_t uid = 0;
    pid_t pid = find_victim(level_oomadj[level], &adj, &rss_kb, &uid);
    if (pid < 0) {
        return false;
    }

    char name[128];
    proc_name(pid, name, sizeof(name));

    if (kill(pid, SIGKILL) != 0) {
        ALOGW("Failed to kill %s (pid %d): %s", name, pid, strerror(errno));
        procs.erase(pid);
        return false;
    }

    ALOGI("Killed %s (pid %d, adj %d, %ldkB) on %s memory pressure (%s); MemAvailable %ldkB",
          name, pid, adj, rss_kb, level_name[level], reason, mem_avail_kb);

    killcnt[adj]++;
    recently_killed[pid] = now_ms();
    procs.erase(pid);
    notify_kill(pid, uid);
    return true;
}

/*
 * Decide whether a stall is something killing an app can actually fix.
 *
 * A PSI stall on its own is not evidence of an app-memory shortage: a large file
 * copy stalls tasks on page cache exactly as hard, and no kill would help it.
 * So every signal below has to point at the *anonymous* working set specifically.
 */
static bool should_kill(enum vmpressure_level level, const struct mem_state* m,
                        const char** reason) {
    long floor_kb = m->total_kb / 100 * mem_avail_floor_pct;
    if (floor_kb < mem_avail_floor_min_kb) {
        floor_kb = mem_avail_floor_min_kb;
    }
    if (m->avail_kb < floor_kb) {
        *reason = "MemAvailable below floor";
        return true;
    }

    /*
     * Everything from here down is a paging *rate*, and a rate is not a
     * shortage.  Measured on device, cold-launching four apps with 2.0 GB of
     * 3.5 GB still available produces refault bursts to 6782 pages/s and
     * swap-in to 114 pages/s — the page cache filling and zram being read
     * back, which is what a healthy Chrome OS board does all day.  Killing an
     * app there frees memory nobody needed and costs a restart, whose own cold
     * start faults the same pages straight back in and re-opens the gate: the
     * kill bursts observed on this board (14 in 14 s) were that loop.
     *
     * So require the board to be genuinely short first.  Below this line the
     * rates become evidence of a working set that no longer fits; above it they
     * are just the machine working.
     */
    if (m->avail_kb >= m->total_kb / 100 * mem_avail_rate_gate_pct) {
        return false;
    }

    if (m->swapin_rate >= (unsigned long long)swapin_thresh_pages_per_sec) {
        *reason = "swapping in";
        return true;
    }

    if (m->refault_rate >= (unsigned long long)refault_thresh_pages_per_sec) {
        *reason = "file working set thrashing";
        return true;
    }

    /*
     * Nothing is paging this instant, but a *complete* stall means no task made
     * progress at all -- and when a large part of the anonymous working set has
     * already been displaced into swap, that stall is the price of those pages,
     * not page-cache churn.
     *
     * This is the steady state a small board settles into, and it is why the
     * three signals above are not enough on their own.  Measured on the 3.5 GB
     * board this daemon was written for: 1.9 GB parked in zram -- 54% of RAM --
     * while MemAvailable still read a healthy 1.1 GB, because compressed pages
     * do not count as unavailable, and with swap-in at 1.5 pages/s because the
     * bursts happen when the user touches an app, not while sampling. Requiring
     * a burst to be caught in the act would mean never reclaiming on exactly the
     * boards that need it.
     *
     * MemAvailable still has to be genuinely low, though, which is the second
     * half of the condition and not redundant: swap stays *occupied* long after a
     * burst is over, because pages are only faulted back when something touches
     * them.  Without this a decaying stall would keep killing on a board that had
     * already recovered -- observed doing exactly that, killing two apps with
     * 6 GB free.  The floor above is a hard "out of memory" line; this is the
     * looser "not comfortable, and paying for swap" line.
     */
    if (level == VMPRESS_LEVEL_CRITICAL &&
        m->swap_used_kb > m->total_kb / 100 * swap_used_pct &&
        m->avail_kb < m->total_kb / 100 * swap_path_avail_pct) {
        *reason = "complete stall with the working set in swap";
        return true;
    }

    return false;
}

static void mp_event(enum vmpressure_level level) {
    static int64_t last_kill_ms;

    struct mem_state m;
    if (!read_meminfo(&m)) {
        ALOGW("Cannot read /proc/meminfo: %s", strerror(errno));
        return;
    }
    sample_rates(&m);

    const char* reason = "";
    if (!should_kill(level, &m, &reason)) {
        if (debug_enabled) {
            ALOGD("%s stall ignored: MemAvailable %ldkB, swap used %ldkB, swap-in %llu/s, "
                  "refault %llu/s",
                  level_name[level], m.avail_kb, m.swap_used_kb, m.swapin_rate, m.refault_rate);
        }
        return;
    }

    int64_t now = now_ms();
    if (now - last_kill_ms < kill_cooldown_ms) {
        return;
    }

    for (int i = 0; i < max_kills_per_event; i++) {
        if (!kill_one(level, m.avail_kb, reason)) {
            if (i == 0 && debug_enabled) {
                ALOGD("%s stall (%s) but nothing killable at adj >= %d", level_name[level], reason,
                      level_oomadj[level]);
            }
            break;
        }
        last_kill_ms = now_ms();
    }
}

/* ------------------------------------------------------------------ */
/* ActivityManager control socket                                      */
/* ------------------------------------------------------------------ */

static void ctrl_data_close(int idx) {
    ALOGI("Closing Activity Manager data connection fd=%d", data_sock[idx]);
    if (epoll_ctl(epollfd, EPOLL_CTL_DEL, data_sock[idx], nullptr) < 0) {
        ALOGW("epoll_ctl to deregister data connection socket failed: %s", strerror(errno));
    }
    close(data_sock[idx]);
    data_sock[idx] = -1;
    data_sock_subs[idx] = 0;
    data_sock_cnt--;
}

static void ctrl_data_handler(int idx, uint32_t events) {
    if (events & (EPOLLERR | EPOLLHUP)) {
        ctrl_data_close(idx);
        return;
    }

    LMKD_CTRL_PACKET packet;
    struct iovec iov = {packet, CTRL_PACKET_MAX_SIZE};
    char cmsg_buf[CMSG_SPACE(sizeof(struct ucred))];
    struct msghdr hdr = {};
    hdr.msg_iov = &iov;
    hdr.msg_iovlen = 1;
    hdr.msg_control = cmsg_buf;
    hdr.msg_controllen = sizeof(cmsg_buf);

    ssize_t len = TEMP_FAILURE_RETRY(recvmsg(data_sock[idx], &hdr, 0));
    if (len <= 0) {
        if (len == 0) {
            ALOGE("Got EOF on control data socket");
        } else {
            ALOGE("control data socket read failed; %s", strerror(errno));
        }
        ctrl_data_close(idx);
        return;
    }

    if (len < (ssize_t)sizeof(int)) {
        return;
    }

    enum lmk_cmd cmd = lmkd_pack_get_cmd(packet);
    int field_count = (int)(len / sizeof(int)) - 1;

    switch (cmd) {
    case LMK_PROCPRIO: {
        struct lmk_procprio params;
        lmkd_pack_get_procprio(packet, field_count, &params);
        if (params.oomadj < OOM_SCORE_ADJ_MIN || params.oomadj > OOM_SCORE_ADJ_MAX) {
            ALOGE("Invalid PROCPRIO oomadj argument %d", params.oomadj);
            break;
        }
        procs[params.pid] = {params.uid, params.oomadj, params.ptype};
        write_oomadj(params.pid, params.oomadj);
        break;
    }
    case LMK_PROCREMOVE: {
        struct lmk_procremove params;
        lmkd_pack_get_procremove(packet, &params);
        procs.erase(params.pid);
        break;
    }
    case LMK_PROCPURGE:
        procs.clear();
        break;
    case LMK_SUBSCRIBE: {
        struct lmk_subscribe params;
        lmkd_pack_get_subscribe(packet, &params);
        if (params.evt_type < LMK_ASYNC_EVENT_FIRST || params.evt_type >= LMK_ASYNC_EVENT_COUNT) {
            ALOGE("Invalid subscription event type %d", params.evt_type);
            break;
        }
        data_sock_subs[idx] |= 1u << params.evt_type;
        break;
    }
    case LMK_TARGET:
        /*
         * minfree/adj thresholds from the framework.  We drive reclaim off PSI
         * rather than free-page watermarks, so there is nothing to apply — but
         * the framework still expects the command to be accepted.
         */
        break;
    case LMK_GETKILLCNT: {
        struct lmk_getkillcnt params;
        lmkd_pack_get_getkillcnt(packet, &params);
        int cnt = 0;
        for (const auto& entry : killcnt) {
            if (entry.first >= params.min_oomadj && entry.first <= params.max_oomadj) {
                cnt += entry.second;
            }
        }
        LMKD_CTRL_PACKET reply;
        size_t rlen = lmkd_pack_set_getkillcnt_repl(reply, cnt);
        TEMP_FAILURE_RETRY(write(data_sock[idx], reply, rlen));
        break;
    }
    case LMK_UPDATE_PROPS: {
        update_props();
        LMKD_CTRL_PACKET reply;
        size_t rlen = lmkd_pack_set_update_props_repl(reply, 0);
        TEMP_FAILURE_RETRY(write(data_sock[idx], reply, rlen));
        break;
    }
    default:
        ALOGW("Received unknown command code %d", cmd);
        break;
    }
}

static void ctrl_connect_handler() {
    int fd = accept(ctrl_sock, nullptr, nullptr);
    if (fd < 0) {
        ALOGE("lmkd control socket accept failed: %s", strerror(errno));
        return;
    }

    /* Find a free slot */
    int idx = -1;
    for (int i = 0; i < MAX_DATA_CONN; i++) {
        if (data_sock[i] < 0) {
            idx = i;
            break;
        }
    }
    if (idx < 0) {
        ALOGE("Max data connections reached, rejecting");
        close(fd);
        return;
    }

    data_sock[idx] = fd;
    data_sock_subs[idx] = 0;
    data_sock_cnt++;
    ALOGI("ActivityManager connected. fd=%d", fd);

    struct epoll_event ev;
    ev.events = EPOLLIN;
    ev.data.ptr = reinterpret_cast<void*>(static_cast<intptr_t>(idx));
    if (epoll_ctl(epollfd, EPOLL_CTL_ADD, fd, &ev) < 0) {
        ALOGE("epoll_ctl for data connection socket failed: %s", strerror(errno));
        ctrl_data_close(idx);
    }
}

/* ------------------------------------------------------------------ */
/* Setup                                                               */
/* ------------------------------------------------------------------ */

/*
 * Every knob's default is the value its declaration above already carries, so
 * the declaration stays the single source of truth.  Repeating the literal here
 * is how a retune silently does nothing: the initialiser changes, this line
 * keeps overwriting it with the old number at startup.
 */
static void update_props() {
    level_oomadj[VMPRESS_LEVEL_LOW] =
            property_get_int32("ro.lmk.low", level_oomadj[VMPRESS_LEVEL_LOW]);
    level_oomadj[VMPRESS_LEVEL_MEDIUM] =
            property_get_int32("ro.lmk.medium", level_oomadj[VMPRESS_LEVEL_MEDIUM]);
    level_oomadj[VMPRESS_LEVEL_CRITICAL] =
            property_get_int32("ro.lmk.critical", level_oomadj[VMPRESS_LEVEL_CRITICAL]);
    swapin_thresh_pages_per_sec =
            property_get_int32("ro.lmk.swapin_threshold", swapin_thresh_pages_per_sec);
    refault_thresh_pages_per_sec =
            property_get_int32("ro.lmk.refault_threshold", refault_thresh_pages_per_sec);
    swap_used_pct = property_get_int32("ro.lmk.swap_used_pct", swap_used_pct);
    swap_path_avail_pct = property_get_int32("ro.lmk.swap_path_avail_pct", swap_path_avail_pct);
    mem_avail_rate_gate_pct =
            property_get_int32("ro.lmk.mem_avail_rate_gate_pct", mem_avail_rate_gate_pct);
    mem_avail_floor_pct = property_get_int32("ro.lmk.mem_avail_floor_pct", mem_avail_floor_pct);
    mem_avail_floor_min_kb =
            property_get_int32("ro.lmk.mem_avail_floor_kb", mem_avail_floor_min_kb);
    kill_cooldown_ms = property_get_int32("ro.lmk.kill_cooldown_ms", kill_cooldown_ms);
    max_kills_per_event =
            property_get_int32("ro.lmk.max_kills_per_event", max_kills_per_event);
    debug_enabled = property_get_bool("ro.lmk.debug", false);
}

static bool init_psi_monitors() {
    for (int level = 0; level < VMPRESS_LEVEL_COUNT; level++) {
        psi_fd[level] = init_psi_monitor(psi_thresholds[level].stall_type,
                                         psi_thresholds[level].threshold_ms * 1000,
                                         PSI_WINDOW_SIZE_MS * 1000);
        if (psi_fd[level] < 0) {
            ALOGE("Failed to create %s PSI monitor: %s", level_name[level], strerror(errno));
            return false;
        }

        void* tag = reinterpret_cast<void*>(static_cast<intptr_t>(TAG_PSI(level)));
        if (register_psi_monitor(epollfd, psi_fd[level], tag) < 0) {
            ALOGE("Failed to register %s PSI monitor: %s", level_name[level], strerror(errno));
            return false;
        }
    }
    return true;
}

int main() {
    for (int i = 0; i < MAX_DATA_CONN; i++) {
        data_sock[i] = -1;
    }
    for (int i = 0; i < VMPRESS_LEVEL_COUNT; i++) {
        psi_fd[i] = -1;
    }

    page_size_kb = sysconf(_SC_PAGESIZE) / 1024;
    update_props();

    epollfd = epoll_create(MAX_EPOLL_EVENTS);
    if (epollfd < 0) {
        ALOGE("epoll_create failed: %s", strerror(errno));
        return 1;
    }

    ctrl_sock = android_get_control_socket("lmkd");
    if (ctrl_sock < 0) {
        ALOGE("get lmkd control socket failed: %s", strerror(errno));
        return 1;
    }

    if (listen(ctrl_sock, MAX_DATA_CONN) < 0) {
        ALOGE("lmkd control socket listen failed: %s", strerror(errno));
        return 1;
    }

    struct epoll_event ev;
    ev.events = EPOLLIN;
    ev.data.ptr = reinterpret_cast<void*>(static_cast<intptr_t>(TAG_CTRL_SOCK));
    if (epoll_ctl(epollfd, EPOLL_CTL_ADD, ctrl_sock, &ev) < 0) {
        ALOGE("epoll_ctl for lmkd control socket failed: %s", strerror(errno));
        return 1;
    }

    /*
     * Losing the PSI monitors is not fatal: without them we degrade to exactly
     * the old stub behaviour (protocol served, no reclaim) rather than leaving
     * the ActivityManager with no lmkd at all, which would wedge process
     * management entirely.
     */
    bool have_psi = init_psi_monitors();
    ALOGI("fydroid-rt lmkd started; memory pressure reclaim %s (kill floors %d/%d/%d)",
          have_psi ? "enabled" : "DISABLED", level_oomadj[VMPRESS_LEVEL_LOW],
          level_oomadj[VMPRESS_LEVEL_MEDIUM], level_oomadj[VMPRESS_LEVEL_CRITICAL]);

    struct epoll_event events[MAX_EPOLL_EVENTS];
    for (;;) {
        int nev = epoll_wait(epollfd, events, MAX_EPOLL_EVENTS, -1);
        if (nev < 0) {
            if (errno == EINTR) continue;
            ALOGE("epoll_wait failed: %s", strerror(errno));
            continue;
        }

        for (int i = 0; i < nev; i++) {
            intptr_t tag = reinterpret_cast<intptr_t>(events[i].data.ptr);
            if (tag == TAG_CTRL_SOCK) {
                ctrl_connect_handler();
            } else if (tag <= TAG_PSI(0)) {
                mp_event((enum vmpressure_level)TAG_TO_PSI_LEVEL(tag));
            } else {
                ctrl_data_handler(static_cast<int>(tag), events[i].events);
            }
        }
    }

    return 0;
}
