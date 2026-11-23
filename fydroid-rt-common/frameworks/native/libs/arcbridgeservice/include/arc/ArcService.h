/*
 * Copyright (C) 2024 Fyde Innovations
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

// ArcService — abstract base class for ARC bridge service consumers.
//
// Uses virtual inheritance from android::RefBase so that ready() is at
// vtable slot 0 in ArcService's own vtable (RefBase vtable goes to the
// virtual base sub-object).
#pragma once

#include <utils/RefBase.h>
#include <mojo/public/cpp/bindings/interface_ptr.h>
#include <arc/arc_bridge.mojom.h>

namespace arc {

class ArcService : public virtual android::RefBase {
public:
    virtual void ready(mojo::InterfacePtr<arc::mojom::ArcBridgeHost>* host) = 0;
    virtual void versionMismatch(unsigned int version) = 0;

protected:
    ~ArcService() override = default;
};

}  // namespace arc
