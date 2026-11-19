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
