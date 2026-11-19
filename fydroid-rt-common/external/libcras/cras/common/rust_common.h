// Shim for cras/common/rust_common.h — type definitions only (no Rust functions).
// The actual functions (fralog, etc.) are server-side and not called by the client library.
#ifndef CRAS_COMMON_RUST_COMMON_H_
#define CRAS_COMMON_RUST_COMMON_H_

#include <stdarg.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>
#include <stdlib.h>

enum CRAS_FRA_SIGNAL {
  PeripheralsUsbSoundCard = 0,
  USBAudioConfigureFailed,
  USBAudioListOutputNodeFailed,
  USBAudioStartFailed,
  USBAudioSoftwareVolumeAbnormalRange,
  USBAudioSoftwareVolumeAbnormalSteps,
  USBAudioUCMNoJack,
  USBAudioUCMWrongJack,
  USBAudioResumeFailed,
  ActiveOutputDevice,
  ActiveInputDevice,
  AudioThreadEvent,
  ALSAUCMCaptureChannelMapExceedsNumChannels,
  SecondaryHciDeviceChanged,
};

enum CrasProcessorEffect {
  NoEffects,
  Negate,
  NoiseCancellation,
  StyleTransfer,
  Beamforming,
  GenerateEcho,
  SpeakerPlugin,
  HeadphonePlugin,
  Overridden,
};

struct cras_fra_kv_t {
  const char *key;
  const char *value;
};

typedef uint64_t CRAS_STREAM_ACTIVE_AP_EFFECT;
#define CRAS_STREAM_ACTIVE_AP_EFFECT_ECHO_CANCELLATION (uint64_t)(1 << 0)
#define CRAS_STREAM_ACTIVE_AP_EFFECT_NOISE_SUPPRESSION (uint64_t)(1 << 1)
#define CRAS_STREAM_ACTIVE_AP_EFFECT_VOICE_ACTIVITY_DETECTION (uint64_t)(1 << 2)
#define CRAS_STREAM_ACTIVE_AP_EFFECT_NEGATE (uint64_t)(1 << 3)
#define CRAS_STREAM_ACTIVE_AP_EFFECT_NOISE_CANCELLATION (uint64_t)(1 << 4)
#define CRAS_STREAM_ACTIVE_AP_EFFECT_STYLE_TRANSFER (uint64_t)(1 << 5)
#define CRAS_STREAM_ACTIVE_AP_EFFECT_BEAMFORMING (uint64_t)(1 << 6)
#define CRAS_STREAM_ACTIVE_AP_EFFECT_GENERATE_ECHO (uint64_t)(1 << 7)
#define CRAS_STREAM_ACTIVE_AP_EFFECT_SPEAKER_PLUGIN (uint64_t)(1 << 8)
#define CRAS_STREAM_ACTIVE_AP_EFFECT_HEADPHONE_PLUGIN (uint64_t)(1 << 9)
#define CRAS_STREAM_ACTIVE_AP_EFFECT_PROCESSOR_OVERRIDDEN (uint64_t)(1 << 10)

typedef uint32_t EFFECT_TYPE;
#define EFFECT_TYPE_NONE (uint32_t)0
#define EFFECT_TYPE_NOISE_CANCELLATION (uint32_t)(1 << 0)
#define EFFECT_TYPE_HFP_MIC_SR (uint32_t)(1 << 1)
#define EFFECT_TYPE_STYLE_TRANSFER (uint32_t)(1 << 2)
#define EFFECT_TYPE_BEAMFORMING (uint32_t)(1 << 3)

typedef uint32_t CRAS_NC_PROVIDER;
#define CRAS_NC_PROVIDER_NONE (uint32_t)0
#define CRAS_NC_PROVIDER_DSP (uint32_t)(1 << 0)
#define CRAS_NC_PROVIDER_AP (uint32_t)(1 << 1)
#define CRAS_NC_PROVIDER_AST (uint32_t)(1 << 2)
#define CRAS_NC_PROVIDER_BF (uint32_t)(1 << 3)

struct CrasEffectUIAppearance {
  EFFECT_TYPE toggle_type;
  EFFECT_TYPE effect_mode_options;
  bool show_effect_fallback_message;
};

#endif  // CRAS_COMMON_RUST_COMMON_H_
