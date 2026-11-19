/*
 * fydroid_gpu_import_probe — does a shared buffer survive the GPU EGL picked?
 *
 * On the dual-GPU iris boards a buffer can be allocated in one place (minigbm
 * on i915, or a dma-heap) and rendered by another GPU (nouveau via zink/NVK),
 * and only layouts every driver agrees on survive the trip. A failed import is
 * not the only failure mode: Mesa's Android platform retries a rejected
 * gralloc modifier as DRM_FORMAT_MOD_INVALID, which zink on NVK treats as
 * LINEAR, so a tiled i915 buffer can import "successfully" and render as
 * garbage. This probe therefore checks pixels, not return codes.
 *
 *   render: the EGL GPU draws a pattern into the buffer through an EGLImage
 *           FBO; the CPU reads it back through the allocator's own mapping.
 *   sample: the CPU writes the pattern through that mapping; the EGL GPU
 *           samples it with texelFetch and glReadPixels it back.
 *
 * Usage: fydroid_gpu_import_probe [--source gralloc|heap] [--usage U] [--size WxH]
 *   --source gralloc  AHardwareBuffer, imported as EGL_NATIVE_BUFFER_ANDROID
 *                     (what SurfaceFlinger and apps do). Default.
 *   --source heap     /dev/dma_heap/system, PRIME-imported into every render
 *                     node and imported as EGL_LINUX_DMA_BUF_EXT with an
 *                     explicit LINEAR modifier (what Chrome does with an
 *                     exported ARC buffer). --usage is ignored.
 *   --usage app       GPU_FRAMEBUFFER|GPU_SAMPLED_IMAGE|COMPOSER_OVERLAY (default)
 *   --usage texture   GPU_SAMPLED_IMAGE
 *   --usage linear    app + CPU_READ_RARELY (minigbm forces LINEAR: positive control)
 *   --usage export    app + the ARC composer-export bit TaskRenderSurface sets
 *                     on the buffers Chrome imports (1ULL << 55)
 *
 * The EGL GPU follows drm.gpu.vendor_name (Mesa platform_android) and zink's
 * Vulkan driver follows ro.hardware.vulkan, so to aim at nouveau without
 * touching the rest of the container:
 *   setprop drm.gpu.vendor_name nouveau
 *   unshare -m sh -c 'mount --bind /vendor/lib64/hw/vulkan.nouveau.so \
 *       /vendor/lib64/hw/vulkan.intel.so && ./fydroid_gpu_import_probe'
 *   setprop drm.gpu.vendor_name ""
 *
 * Exit status: 0 when both directions match pixel for pixel.
 */

#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <GLES3/gl3.h>
// gl2ext.h relies on the types gl3.h defines, so it has to come second.
#include <GLES2/gl2ext.h>
#include <android/hardware_buffer.h>
#include <cutils/properties.h>
#include <drm/drm.h>
#include <fcntl.h>
#include <hardware/gralloc.h>
#include <linux/dma-buf.h>
#include <linux/dma-heap.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <unistd.h>
#include <vndk/hardware_buffer.h>

#include <cerrno>
#include <cinttypes>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <functional>
#include <memory>

namespace {

// Same bit as kUsageArcComposerExport in surfaceflinger/TaskRenderSurface.cpp.
constexpr uint64_t kUsageArcComposerExport = 1ULL << 55;

// minigbm gralloc0 perform op and its out struct (cros_gralloc/gralloc0).
constexpr int kCrosGrallocDrmGetBufferInfo = 4;
struct CrosGralloc0BufferInfo {
    uint32_t drm_fourcc;
    int num_fds;
    int fds[4];
    uint64_t modifier;
    int offset[4];
    int stride[4];
};

struct Rgba {
    uint8_t r, g, b, a;
};

uint32_t gWidth = 1920;
uint32_t gHeight = 1080;

// Quadrants plus a small odd-sized block at an odd offset, so a tile-layout
// mismatch shows up as displaced pixels rather than a uniform colour.
Rgba expected(uint32_t x, uint32_t y) {
    uint32_t bx = gWidth / 3 + 5, by = gHeight / 3 + 3;
    if (x >= bx && x < bx + 37 && y >= by && y < by + 19) return {255, 0, 255, 255};
    bool right = x >= gWidth / 2, top = y >= gHeight / 2;
    if (!right && !top) return {255, 0, 0, 255};
    if (right && !top) return {0, 255, 0, 255};
    if (!right && top) return {0, 0, 255, 255};
    return {255, 255, 255, 255};
}

// A buffer under test: how to wrap it as an EGLImage, and how the CPU reaches
// its pixels (tightly packed RGBA rows, strideBytes apart).
struct Target {
    std::function<EGLImageKHR(EGLDisplay)> createImage;
    std::function<uint8_t *(bool forWrite)> map;
    std::function<void()> unmap;
    uint32_t strideBytes = 0;
};

void clearRect(int x, int y, int w, int h, Rgba c) {
    glScissor(x, y, w, h);
    glClearColor(c.r / 255.0f, c.g / 255.0f, c.b / 255.0f, c.a / 255.0f);
    glClear(GL_COLOR_BUFFER_BIT);
}

void drawPatternWithClears() {
    int w = gWidth, h = gHeight;
    glEnable(GL_SCISSOR_TEST);
    clearRect(0, 0, w / 2, h / 2, expected(0, 0));
    clearRect(w / 2, 0, w - w / 2, h / 2, expected(w - 1, 0));
    clearRect(0, h / 2, w / 2, h - h / 2, expected(0, h - 1));
    clearRect(w / 2, h / 2, w - w / 2, h - h / 2, expected(w - 1, h - 1));
    clearRect(w / 3 + 5, h / 3 + 3, 37, 19, expected(w / 3 + 5, h / 3 + 3));
    glDisable(GL_SCISSOR_TEST);
}

// Compares rows of tightly packed RGBA pixels; returns the mismatch count.
uint64_t compare(const char *what, const uint8_t *base, uint32_t strideBytes) {
    uint64_t bad = 0;
    for (uint32_t y = 0; y < gHeight; y++) {
        const uint8_t *row = base + (uint64_t)y * strideBytes;
        for (uint32_t x = 0; x < gWidth; x++) {
            const uint8_t *p = row + x * 4;
            Rgba e = expected(x, y);
            if (p[0] != e.r || p[1] != e.g || p[2] != e.b || p[3] != e.a) {
                if (bad < 5)
                    printf("  %s mismatch at (%u,%u): got %02x%02x%02x%02x want %02x%02x%02x%02x\n",
                           what, x, y, p[0], p[1], p[2], p[3], e.r, e.g, e.b, e.a);
                bad++;
            }
        }
    }
    return bad;
}

GLuint compile(GLenum type, const char *src) {
    GLuint s = glCreateShader(type);
    glShaderSource(s, 1, &src, nullptr);
    glCompileShader(s);
    GLint ok = 0;
    glGetShaderiv(s, GL_COMPILE_STATUS, &ok);
    if (!ok) {
        char log[1024] = {};
        glGetShaderInfoLog(s, sizeof(log), nullptr, log);
        printf("shader compile failed: %s\n", log);
    }
    return s;
}

// Wraps an EGLImage as a GL texture on the current context.
bool textureFromImage(EGLImageKHR image, GLuint *outTex) {
    GLuint tex = 0;
    glGenTextures(1, &tex);
    glBindTexture(GL_TEXTURE_2D, tex);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glEGLImageTargetTexture2DOES(GL_TEXTURE_2D, (GLeglImageOES)image);
    GLenum err = glGetError();
    if (err != GL_NO_ERROR) {
        printf("  glEGLImageTargetTexture2DOES failed: 0x%x\n", err);
        glDeleteTextures(1, &tex);
        return false;
    }
    *outTex = tex;
    return true;
}

bool renderTest(EGLDisplay dpy, Target &t) {
    printf("[render] GPU draws into the buffer, CPU reads it back\n");
    EGLImageKHR image = t.createImage(dpy);
    GLuint tex;
    if (image == EGL_NO_IMAGE_KHR) return false;
    if (!textureFromImage(image, &tex)) {
        eglDestroyImageKHR(dpy, image);
        return false;
    }

    GLuint fbo = 0;
    glGenFramebuffers(1, &fbo);
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, tex, 0);
    GLenum status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
    bool ok = status == GL_FRAMEBUFFER_COMPLETE;
    if (!ok) {
        printf("  FBO incomplete: 0x%x\n", status);
    } else {
        glViewport(0, 0, gWidth, gHeight);
        drawPatternWithClears();
        glFinish();
    }
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    glDeleteFramebuffers(1, &fbo);
    glDeleteTextures(1, &tex);
    eglDestroyImageKHR(dpy, image);
    if (!ok) return false;

    uint8_t *base = t.map(false);
    if (!base) return false;
    uint64_t bad = compare("render", base, t.strideBytes);
    t.unmap();
    printf("  %s: %" PRIu64 " of %" PRIu64 " pixels wrong\n", bad ? "FAIL" : "PASS", bad,
           (uint64_t)gWidth * gHeight);
    return bad == 0;
}

bool sampleTest(EGLDisplay dpy, Target &t) {
    printf("[sample] CPU writes the buffer, GPU samples it\n");
    uint8_t *base = t.map(true);
    if (!base) return false;
    for (uint32_t y = 0; y < gHeight; y++) {
        uint8_t *row = base + (uint64_t)y * t.strideBytes;
        for (uint32_t x = 0; x < gWidth; x++) {
            Rgba e = expected(x, y);
            row[x * 4] = e.r;
            row[x * 4 + 1] = e.g;
            row[x * 4 + 2] = e.b;
            row[x * 4 + 3] = e.a;
        }
    }
    t.unmap();

    EGLImageKHR image = t.createImage(dpy);
    GLuint src;
    if (image == EGL_NO_IMAGE_KHR) return false;
    if (!textureFromImage(image, &src)) {
        eglDestroyImageKHR(dpy, image);
        return false;
    }

    GLuint dst = 0, fbo = 0;
    glGenTextures(1, &dst);
    glBindTexture(GL_TEXTURE_2D, dst);
    glTexStorage2D(GL_TEXTURE_2D, 1, GL_RGBA8, gWidth, gHeight);
    glGenFramebuffers(1, &fbo);
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, dst, 0);

    static const char *kVs = "#version 300 es\n"
                             "void main() {\n"
                             "  vec2 p = vec2(float((gl_VertexID & 1) << 2), float((gl_VertexID & 2) << 1));\n"
                             "  gl_Position = vec4(p - 1.0, 0.0, 1.0);\n"
                             "}\n";
    static const char *kFs = "#version 300 es\n"
                             "precision highp float;\n"
                             "uniform highp sampler2D t;\n"
                             "out vec4 o;\n"
                             "void main() { o = texelFetch(t, ivec2(gl_FragCoord.xy), 0); }\n";
    GLuint prog = glCreateProgram();
    glAttachShader(prog, compile(GL_VERTEX_SHADER, kVs));
    glAttachShader(prog, compile(GL_FRAGMENT_SHADER, kFs));
    glLinkProgram(prog);
    glUseProgram(prog);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, src);
    glUniform1i(glGetUniformLocation(prog, "t"), 0);
    glViewport(0, 0, gWidth, gHeight);
    glDrawArrays(GL_TRIANGLES, 0, 3);

    uint8_t *pixels = static_cast<uint8_t *>(malloc((uint64_t)gWidth * gHeight * 4));
    glReadPixels(0, 0, gWidth, gHeight, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
    uint64_t bad = compare("sample", pixels, gWidth * 4);
    free(pixels);

    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    glDeleteFramebuffers(1, &fbo);
    glDeleteTextures(1, &dst);
    glDeleteTextures(1, &src);
    glDeleteProgram(prog);
    eglDestroyImageKHR(dpy, image);
    printf("  %s: %" PRIu64 " of %" PRIu64 " pixels wrong\n", bad ? "FAIL" : "PASS", bad,
           (uint64_t)gWidth * gHeight);
    return bad == 0;
}

void printGrallocModifier(AHardwareBuffer *ahb) {
    const hw_module_t *mod = nullptr;
    if (hw_get_module(GRALLOC_HARDWARE_MODULE_ID, &mod) != 0 || !mod) {
        printf("gralloc module: unavailable\n");
        return;
    }
    const gralloc_module_t *gr = reinterpret_cast<const gralloc_module_t *>(mod);
    CrosGralloc0BufferInfo info{};
    if (!gr->perform ||
        gr->perform(gr, kCrosGrallocDrmGetBufferInfo, AHardwareBuffer_getNativeHandle(ahb), &info)) {
        printf("gralloc buffer info: perform op failed (module \"%s\")\n", mod->name);
        return;
    }
    char fourcc[5] = {};
    memcpy(fourcc, &info.drm_fourcc, 4);
    printf("gralloc buffer: fourcc %s modifier 0x%016" PRIx64 " planes %d stride %d offset %d\n",
           fourcc, info.modifier, info.num_fds, info.stride[0], info.offset[0]);
}

bool setupGralloc(uint64_t usage, const char *usageName, Target &t, AHardwareBuffer **out) {
    AHardwareBuffer_Desc desc = {};
    desc.width = gWidth;
    desc.height = gHeight;
    desc.layers = 1;
    desc.format = AHARDWAREBUFFER_FORMAT_R8G8B8A8_UNORM;
    desc.usage = usage;
    AHardwareBuffer *ahb = nullptr;
    int err = AHardwareBuffer_allocate(&desc, &ahb);
    if (err || !ahb) {
        printf("AHardwareBuffer_allocate(%s %ux%u usage 0x%" PRIx64 ") failed: %d\n", usageName,
               gWidth, gHeight, usage, err);
        return false;
    }
    AHardwareBuffer_describe(ahb, &desc);
    printf("source: gralloc %s %ux%u usage 0x%" PRIx64 " pixel stride %u\n", usageName, desc.width,
           desc.height, (uint64_t)desc.usage, desc.stride);
    printGrallocModifier(ahb);

    t.strideBytes = desc.stride * 4;
    t.createImage = [ahb](EGLDisplay dpy) {
        EGLint attrs[] = {EGL_IMAGE_PRESERVED_KHR, EGL_TRUE, EGL_NONE};
        EGLImageKHR image = eglCreateImageKHR(dpy, EGL_NO_CONTEXT, EGL_NATIVE_BUFFER_ANDROID,
                                              eglGetNativeClientBufferANDROID(ahb), attrs);
        if (image == EGL_NO_IMAGE_KHR)
            printf("  eglCreateImageKHR(EGL_NATIVE_BUFFER_ANDROID) failed: 0x%x\n", eglGetError());
        return image;
    };
    t.map = [ahb](bool forWrite) -> uint8_t * {
        void *addr = nullptr;
        uint64_t u = forWrite ? AHARDWAREBUFFER_USAGE_CPU_WRITE_RARELY : AHARDWAREBUFFER_USAGE_CPU_READ_RARELY;
        if (AHardwareBuffer_lock(ahb, u, -1, nullptr, &addr) || !addr) {
            printf("  AHardwareBuffer_lock(%s) failed\n", forWrite ? "write" : "read");
            return nullptr;
        }
        return static_cast<uint8_t *>(addr);
    };
    t.unmap = [ahb]() { AHardwareBuffer_unlock(ahb, nullptr); };
    *out = ahb;
    return true;
}

// Kernel-level PRIME import into every render node, independent of EGL: shows
// which drivers accept this dma-buf at all before any userspace driver runs.
void primeImportIntoRenderNodes(int dmabuf) {
    for (int minor = 128; minor < 136; minor++) {
        char path[32];
        snprintf(path, sizeof(path), "/dev/dri/renderD%d", minor);
        int fd = open(path, O_RDWR | O_CLOEXEC);
        if (fd < 0) continue;
        char name[32] = {};
        struct drm_version ver = {};
        ver.name = name;
        ver.name_len = sizeof(name) - 1;
        ioctl(fd, DRM_IOCTL_VERSION, &ver);
        struct drm_prime_handle prime = {};
        prime.fd = dmabuf;
        int ret = ioctl(fd, DRM_IOCTL_PRIME_FD_TO_HANDLE, &prime);
        printf("PRIME import into %s (%s): %s\n", path, name, ret ? strerror(errno) : "ok");
        if (!ret) {
            struct drm_gem_close close_req = {};
            close_req.handle = prime.handle;
            ioctl(fd, DRM_IOCTL_GEM_CLOSE, &close_req);
        }
        close(fd);
    }
}

bool setupHeap(Target &t, int *outDmabuf, uint8_t **outAddr, size_t *outSize) {
    int heap = open("/dev/dma_heap/system", O_RDONLY | O_CLOEXEC);
    if (heap < 0) {
        printf("open /dev/dma_heap/system failed: %s\n", strerror(errno));
        return false;
    }
    // NVK only accepts LINEAR pitches that are multiples of 32 bytes (nil
    // descriptor.rs); anything else aborts the process inside NVK. Use the
    // layout minigbm's nouveau backend produces.
    const uint32_t pitch = (gWidth * 4 + 31) & ~31U;
    size_t size = ((uint64_t)pitch * gHeight + 4095) & ~4095ULL;
    struct dma_heap_allocation_data alloc = {};
    alloc.len = size;
    alloc.fd_flags = O_RDWR | O_CLOEXEC;
    int ret = ioctl(heap, DMA_HEAP_IOCTL_ALLOC, &alloc);
    close(heap);
    if (ret) {
        printf("DMA_HEAP_IOCTL_ALLOC(%zu) failed: %s\n", size, strerror(errno));
        return false;
    }
    int dmabuf = alloc.fd;
    void *addr = mmap(nullptr, size, PROT_READ | PROT_WRITE, MAP_SHARED, dmabuf, 0);
    if (addr == MAP_FAILED) {
        printf("mmap of heap dma-buf failed: %s\n", strerror(errno));
        close(dmabuf);
        return false;
    }
    printf("source: /dev/dma_heap/system %ux%u ABGR8888 LINEAR, pitch %u, %zu bytes\n", gWidth,
           gHeight, pitch, size);
    primeImportIntoRenderNodes(dmabuf);

    t.strideBytes = pitch;
    t.createImage = [dmabuf, pitch](EGLDisplay dpy) {
        EGLint attrs[] = {
                EGL_WIDTH, (EGLint)gWidth,
                EGL_HEIGHT, (EGLint)gHeight,
                EGL_LINUX_DRM_FOURCC_EXT, 0x34324241 /* DRM_FORMAT_ABGR8888, 'AB24' */,
                EGL_DMA_BUF_PLANE0_FD_EXT, dmabuf,
                EGL_DMA_BUF_PLANE0_OFFSET_EXT, 0,
                EGL_DMA_BUF_PLANE0_PITCH_EXT, (EGLint)pitch,
                EGL_DMA_BUF_PLANE0_MODIFIER_LO_EXT, 0 /* DRM_FORMAT_MOD_LINEAR */,
                EGL_DMA_BUF_PLANE0_MODIFIER_HI_EXT, 0,
                EGL_NONE,
        };
        EGLImageKHR image = eglCreateImageKHR(dpy, EGL_NO_CONTEXT, EGL_LINUX_DMA_BUF_EXT, nullptr, attrs);
        if (image == EGL_NO_IMAGE_KHR)
            printf("  eglCreateImageKHR(EGL_LINUX_DMA_BUF_EXT, LINEAR) failed: 0x%x\n", eglGetError());
        return image;
    };
    // The CPU view is the heap's own mmap, bracketed with DMA_BUF_IOCTL_SYNC so
    // the importing device's caches are flushed the way a real client would.
    auto syncFlags = std::make_shared<uint64_t>(0);
    t.map = [dmabuf, addr, syncFlags](bool forWrite) -> uint8_t * {
        *syncFlags = forWrite ? DMA_BUF_SYNC_WRITE : DMA_BUF_SYNC_READ;
        struct dma_buf_sync sync = {DMA_BUF_SYNC_START | *syncFlags};
        ioctl(dmabuf, DMA_BUF_IOCTL_SYNC, &sync);
        return static_cast<uint8_t *>(addr);
    };
    t.unmap = [dmabuf, syncFlags]() {
        struct dma_buf_sync sync = {DMA_BUF_SYNC_END | *syncFlags};
        ioctl(dmabuf, DMA_BUF_IOCTL_SYNC, &sync);
    };
    *outDmabuf = dmabuf;
    *outAddr = static_cast<uint8_t *>(addr);
    *outSize = size;
    return true;
}

}  // namespace

int main(int argc, char **argv) {
    uint64_t usage = AHARDWAREBUFFER_USAGE_GPU_FRAMEBUFFER | AHARDWAREBUFFER_USAGE_GPU_SAMPLED_IMAGE |
                     AHARDWAREBUFFER_USAGE_COMPOSER_OVERLAY;
    const char *usageName = "app";
    bool heapSource = false;
    for (int i = 1; i < argc; i++) {
        if (!strcmp(argv[i], "--usage") && i + 1 < argc) {
            usageName = argv[++i];
            if (!strcmp(usageName, "texture"))
                usage = AHARDWAREBUFFER_USAGE_GPU_SAMPLED_IMAGE;
            else if (!strcmp(usageName, "linear"))
                usage |= AHARDWAREBUFFER_USAGE_CPU_READ_RARELY;
            else if (!strcmp(usageName, "export"))
                usage |= kUsageArcComposerExport;
            else if (strcmp(usageName, "app")) {
                fprintf(stderr, "unknown --usage %s\n", usageName);
                return 2;
            }
        } else if (!strcmp(argv[i], "--source") && i + 1 < argc) {
            const char *s = argv[++i];
            if (!strcmp(s, "heap"))
                heapSource = true;
            else if (strcmp(s, "gralloc")) {
                fprintf(stderr, "unknown --source %s\n", s);
                return 2;
            }
        } else if (!strcmp(argv[i], "--size") && i + 1 < argc) {
            if (sscanf(argv[++i], "%ux%u", &gWidth, &gHeight) != 2 || !gWidth || !gHeight) {
                fprintf(stderr, "bad --size %s\n", argv[i]);
                return 2;
            }
        } else {
            fprintf(stderr,
                    "usage: %s [--source gralloc|heap] [--usage app|texture|linear|export] [--size WxH]\n",
                    argv[0]);
            return 2;
        }
    }

    char vendor[PROPERTY_VALUE_MAX], vulkan[PROPERTY_VALUE_MAX];
    property_get("drm.gpu.vendor_name", vendor, "");
    property_get("ro.hardware.vulkan", vulkan, "");
    printf("drm.gpu.vendor_name=\"%s\" ro.hardware.vulkan=\"%s\"\n", vendor, vulkan);

    EGLDisplay dpy = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (!eglInitialize(dpy, nullptr, nullptr)) {
        printf("eglInitialize failed: 0x%x\n", eglGetError());
        return 1;
    }
    EGLint cfgAttrs[] = {EGL_RENDERABLE_TYPE, EGL_OPENGL_ES3_BIT, EGL_SURFACE_TYPE, EGL_PBUFFER_BIT,
                         EGL_NONE};
    EGLConfig cfg;
    EGLint n = 0;
    if (!eglChooseConfig(dpy, cfgAttrs, &cfg, 1, &n) || n < 1) {
        printf("eglChooseConfig found no GLES3 config: 0x%x\n", eglGetError());
        return 1;
    }
    EGLint ctxAttrs[] = {EGL_CONTEXT_CLIENT_VERSION, 3, EGL_NONE};
    EGLContext ctx = eglCreateContext(dpy, cfg, EGL_NO_CONTEXT, ctxAttrs);
    EGLint pbAttrs[] = {EGL_WIDTH, 1, EGL_HEIGHT, 1, EGL_NONE};
    EGLSurface pb = eglCreatePbufferSurface(dpy, cfg, pbAttrs);
    if (ctx == EGL_NO_CONTEXT || !eglMakeCurrent(dpy, pb, pb, ctx)) {
        printf("context setup failed: 0x%x\n", eglGetError());
        return 1;
    }
    printf("GL_RENDERER: %s\nGL_VERSION: %s\n", glGetString(GL_RENDERER), glGetString(GL_VERSION));

    Target target;
    AHardwareBuffer *ahb = nullptr;
    int dmabuf = -1;
    uint8_t *heapAddr = nullptr;
    size_t heapSize = 0;
    bool ready = heapSource ? setupHeap(target, &dmabuf, &heapAddr, &heapSize)
                            : setupGralloc(usage, usageName, target, &ahb);
    if (!ready) return 1;

    bool renderOk = renderTest(dpy, target);
    bool sampleOk = sampleTest(dpy, target);
    printf("RESULT: render %s, sample %s\n", renderOk ? "PASS" : "FAIL", sampleOk ? "PASS" : "FAIL");

    if (ahb) AHardwareBuffer_release(ahb);
    if (heapAddr) munmap(heapAddr, heapSize);
    if (dmabuf >= 0) close(dmabuf);
    eglMakeCurrent(dpy, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    eglDestroySurface(dpy, pb);
    eglDestroyContext(dpy, ctx);
    eglTerminate(dpy);
    return renderOk && sampleOk ? 0 : 1;
}
