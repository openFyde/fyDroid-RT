#pragma once

#include <hardware/camera_common.h>
#include <map>
#include <string>
#include <vector>
#include <memory>

#include <base/synchronization/waitable_event.h>
#include <arc/MojoProcessSupport.h>
#include <camera/mojo/camera_common.mojom.h>

#include "PipeHealth.h"

namespace fydroid { namespace camera {

// Wraps cros::mojom::VendorTagOpsProxy.
//
// Pre-fetches the full vendor tag list at construction time so that the
// per-tag query C callbacks (vendor_tag_ops_t function pointers) are
// synchronous and cheap.
class VendorTagOpsDelegate {
 public:
  VendorTagOpsDelegate(arc::MojoProcessSupport* mojo_support,
                        mojo::InterfacePtr<cros::mojom::VendorTagOps> ops);
  ~VendorTagOpsDelegate() = default;

  // Populate a vendor_tag_ops_t for the camera module.
  void FillVendorTagOps(vendor_tag_ops_t* vtops);

  // vendor_tag_ops_t implementations.
  int GetTagCount();
  void GetAllTags(uint32_t* tag_array);
  const char* GetSectionName(uint32_t tag);
  const char* GetTagName(uint32_t tag);
  int GetTagType(uint32_t tag);

 private:
  struct TagInfo {
    std::string section;
    std::string name;
    int type = -1;
  };

  void Prefetch();

  arc::MojoProcessSupport* mojo_support_;
  mojo::InterfacePtr<cros::mojom::VendorTagOps> ops_;
  std::shared_ptr<PipeHealth> pipe_ = std::make_shared<PipeHealth>();

  std::vector<uint32_t> tags_;
  std::map<uint32_t, TagInfo> tag_info_;
};

}} // namespace fydroid::camera
