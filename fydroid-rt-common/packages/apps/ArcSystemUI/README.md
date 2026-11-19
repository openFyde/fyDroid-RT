# ArcSystemUI (AOSP 13)

FydeOS SystemUI variant for ARC++ on AOSP 13. Thin overlay over `SystemUI-core`
that hands host chrome (status bar, recents, lock screen, notification shade) to
Chrome OS. See `device/fydeos/docs/arcsystemui-architecture.md` (what/why,
module-by-module) and `arcsystemui-rebase-plan.md` (rewrite execution plan).

AOSP 11 reference fork: `../ArcSystemUI-aosp11/` (reference-only, not built).

## Status: builds (`m ArcSystemUI` green)

Implemented for AOSP 13:
- `Android.bp`, `res/values/config.xml` (entry point → `ArcSystemUIInitializer`),
  `AndroidManifest.xml` (seeded from stock-13; the AOSP 11 ARC deltas were only
  `exported`/`configChanges` fixes that stock-13 already has).
- Entry + Dagger graph, mirroring the stock `Tv*` variant:
  - `ArcSystemUIInitializer` → `DaggerArcGlobalRootComponent`
  - `dagger/ArcGlobalRootComponent` (reuses stock `WMComponent`)
  - `dagger/ArcSysUIComponent` (swaps in the two ARC modules below)
- ARC behavior (the actual fork):
  - `dagger/ArcSystemUIModule` — copy of `ReferenceSystemUIModule` that
    **omits `StartCentralSurfacesModule`** (Chrome OS owns the status bar, so
    `CentralSurfacesImpl` is never started) and fixes `leak_report_email`.
  - `dagger/ArcSystemUICoreStartableModule` — trimmed `CoreStartable` set; starts
    only non-chrome services (the AOSP 13 home of ARC's old service-list trim).

### The AOSP 13 simplification

In AOSP 13 every `CentralSurfaces` (ex-`StatusBar`) consumer injects
`Optional<CentralSurfaces>` (via `SystemUIModule @BindsOptionalOf`), and
`registerCentralSurfaces` is only ever called from `CentralSurfacesImpl.start()`.
So ARC just **doesn't start `CentralSurfacesImpl`** and everything degrades
gracefully — which makes the AOSP 11 null-`StatusBar` provider and the no-op
`ArcStatusBarKeyguardViewManager` / `ArcShadeController` subclasses **unnecessary
for the build**, and likely at runtime too.

### Deferred to on-device bring-up (plan §5)

Runtime-only behavior, unverifiable until boot — add **only if** Chrome OS conflicts:
- `ArcKeyguardViewMediator` (disable Android lock screen) + `ArcKeyguardModule`.
- No-op `ArcShadeController` / `ArcStatusBarKeyguardViewManager` (likely unneeded;
  see above).
- Further `CoreStartable` trim tuning; ARC binders
  (`ArcDefault{Component,Service,BroadcastReceiver}Binder`).
The AOSP 11 implementations of all of these are in `../ArcSystemUI-aosp11/`.
