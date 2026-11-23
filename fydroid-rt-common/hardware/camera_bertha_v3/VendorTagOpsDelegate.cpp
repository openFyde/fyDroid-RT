/*
 * Copyright (C) 2026 Fyde Innovations
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "VendorTagOpsDelegate.h"
#include "MojoCallWaiter.h"

#include <base/bind.h>
#include <base/logging.h>
#include <base/synchronization/waitable_event.h>

namespace fydroid { namespace camera {

// C thunks for vendor_tag_ops_t — file-scope singletons.
static VendorTagOpsDelegate* g_vendor_delegate = nullptr;

static int vtag_get_tag_count(const vendor_tag_ops_t*) {
    return g_vendor_delegate ? g_vendor_delegate->GetTagCount() : 0;
}
static void vtag_get_all_tags(const vendor_tag_ops_t*, uint32_t* tag_array) {
    if (g_vendor_delegate) g_vendor_delegate->GetAllTags(tag_array);
}
static const char* vtag_get_section_name(const vendor_tag_ops_t*, uint32_t tag) {
    return g_vendor_delegate ? g_vendor_delegate->GetSectionName(tag) : nullptr;
}
static const char* vtag_get_tag_name(const vendor_tag_ops_t*, uint32_t tag) {
    return g_vendor_delegate ? g_vendor_delegate->GetTagName(tag) : nullptr;
}
static int vtag_get_tag_type(const vendor_tag_ops_t*, uint32_t tag) {
    return g_vendor_delegate ? g_vendor_delegate->GetTagType(tag) : -1;
}

// ---------------------------------------------------------------------------

VendorTagOpsDelegate::VendorTagOpsDelegate(
        arc::MojoProcessSupport* mojo_support,
        mojo::InterfacePtr<cros::mojom::VendorTagOps> ops)
        : mojo_support_(mojo_support), ops_(std::move(ops)) {
    g_vendor_delegate = this;
    Prefetch();
}

void VendorTagOpsDelegate::Prefetch() {
    auto runner = mojo_support_->getThread()->getTaskRunner();

    // --- GetTagCount ---
    int32_t count = 0;
    {
        auto pending = MakePendingReply<int32_t>();
        runner->PostTask(FROM_HERE,
                base::BindOnce([](mojo::InterfacePtr<cros::mojom::VendorTagOps>* ops,
                                  std::shared_ptr<PendingReply<int32_t>> p) {
                    (*ops)->GetTagCount(base::BindRepeating(
                            [](std::shared_ptr<PendingReply<int32_t>> p, int32_t n) {
                                p->value = n;
                                p->event.Signal();
                            }, p));
                }, &ops_, pending));
        if (!AwaitReply(pending, pipe_, runner, "VendorTagOps::GetTagCount")) {
            return;
        }
        count = pending->value;
    }

    if (count <= 0) {
        LOG(INFO) << "VendorTagOpsDelegate: no vendor tags";
        return;
    }

    // --- GetAllTags ---
    {
        auto pending = MakePendingReply<std::vector<uint32_t>>();
        runner->PostTask(FROM_HERE,
                base::BindOnce([](mojo::InterfacePtr<cros::mojom::VendorTagOps>* ops,
                                  std::shared_ptr<PendingReply<std::vector<uint32_t>>> p) {
                    (*ops)->GetAllTags(base::BindRepeating(
                            [](std::shared_ptr<PendingReply<std::vector<uint32_t>>> p,
                               const std::vector<uint32_t>& tags) {
                                p->value = tags;
                                p->event.Signal();
                            }, p));
                }, &ops_, pending));
        if (!AwaitReply(pending, pipe_, runner, "VendorTagOps::GetAllTags")) {
            return;
        }
        tags_ = std::move(pending->value);
    }

    // --- Fetch section / name / type for each tag ---
    //
    // Three round-trips per tag, so this is where a wedged pipe would hurt
    // most. PipeHealth makes only the first timeout cost real time; every
    // later call returns immediately and we abandon the prefetch, leaving the
    // tags we did manage to read.
    for (uint32_t tag : tags_) {
        if (!pipe_->ok()) {
            LOG(ERROR) << "VendorTagOpsDelegate: pipe died after "
                       << tag_info_.size() << "/" << tags_.size() << " tags";
            break;
        }
        TagInfo info;

        // GetSectionName
        {
            auto pending = MakePendingReply<std::string>();
            runner->PostTask(FROM_HERE,
                    base::BindOnce([](mojo::InterfacePtr<cros::mojom::VendorTagOps>* ops,
                                      uint32_t t,
                                      std::shared_ptr<PendingReply<std::string>> p) {
                        (*ops)->GetSectionName(t, base::BindRepeating(
                                [](std::shared_ptr<PendingReply<std::string>> p,
                                   const base::Optional<std::string>& s) {
                                    if (s) p->value = *s;
                                    p->event.Signal();
                                }, p));
                    }, &ops_, tag, pending));
            if (!AwaitReply(pending, pipe_, runner, "VendorTagOps::GetSectionName")) {
                break;
            }
            info.section = std::move(pending->value);
        }

        // GetTagName
        {
            auto pending = MakePendingReply<std::string>();
            runner->PostTask(FROM_HERE,
                    base::BindOnce([](mojo::InterfacePtr<cros::mojom::VendorTagOps>* ops,
                                      uint32_t t,
                                      std::shared_ptr<PendingReply<std::string>> p) {
                        (*ops)->GetTagName(t, base::BindRepeating(
                                [](std::shared_ptr<PendingReply<std::string>> p,
                                   const base::Optional<std::string>& n) {
                                    if (n) p->value = *n;
                                    p->event.Signal();
                                }, p));
                    }, &ops_, tag, pending));
            if (!AwaitReply(pending, pipe_, runner, "VendorTagOps::GetTagName")) {
                break;
            }
            info.name = std::move(pending->value);
        }

        // GetTagType
        {
            auto pending = MakePendingReply<int32_t>();
            pending->value = -1;
            runner->PostTask(FROM_HERE,
                    base::BindOnce([](mojo::InterfacePtr<cros::mojom::VendorTagOps>* ops,
                                      uint32_t t,
                                      std::shared_ptr<PendingReply<int32_t>> p) {
                        (*ops)->GetTagType(t, base::BindRepeating(
                                [](std::shared_ptr<PendingReply<int32_t>> p, int32_t tp) {
                                    p->value = tp;
                                    p->event.Signal();
                                }, p));
                    }, &ops_, tag, pending));
            if (!AwaitReply(pending, pipe_, runner, "VendorTagOps::GetTagType")) {
                break;
            }
            info.type = pending->value;
        }

        tag_info_[tag] = std::move(info);
    }

    // Keep the advertised tag list and the descriptions consistent. If the
    // prefetch was cut short, tags_ still lists tags we have no name or type
    // for, and the framework's VendorTagDescriptor would read back nullptr for
    // them. Publish only the tags we fully resolved.
    if (tag_info_.size() != tags_.size()) {
        std::vector<uint32_t> resolved;
        resolved.reserve(tag_info_.size());
        for (const auto& [tag, info] : tag_info_) {
            resolved.push_back(tag);
        }
        LOG(ERROR) << "VendorTagOpsDelegate: publishing " << resolved.size()
                   << " of " << tags_.size() << " vendor tags (prefetch cut short)";
        tags_ = std::move(resolved);
    }

    LOG(INFO) << "VendorTagOpsDelegate: prefetched " << tags_.size()
              << " vendor tags";
}

void VendorTagOpsDelegate::FillVendorTagOps(vendor_tag_ops_t* vtops) {
    vtops->get_tag_count = vtag_get_tag_count;
    vtops->get_all_tags = vtag_get_all_tags;
    vtops->get_section_name = vtag_get_section_name;
    vtops->get_tag_name = vtag_get_tag_name;
    vtops->get_tag_type = vtag_get_tag_type;
}

int VendorTagOpsDelegate::GetTagCount() {
    return static_cast<int>(tags_.size());
}

void VendorTagOpsDelegate::GetAllTags(uint32_t* tag_array) {
    for (size_t i = 0; i < tags_.size(); i++) {
        tag_array[i] = tags_[i];
    }
}

const char* VendorTagOpsDelegate::GetSectionName(uint32_t tag) {
    auto it = tag_info_.find(tag);
    return it != tag_info_.end() ? it->second.section.c_str() : nullptr;
}

const char* VendorTagOpsDelegate::GetTagName(uint32_t tag) {
    auto it = tag_info_.find(tag);
    return it != tag_info_.end() ? it->second.name.c_str() : nullptr;
}

int VendorTagOpsDelegate::GetTagType(uint32_t tag) {
    auto it = tag_info_.find(tag);
    return it != tag_info_.end() ? it->second.type : -1;
}

}} // namespace fydroid::camera
