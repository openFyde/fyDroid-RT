package org.chromium.arc.mojom;

import org.chromium.mojo.bindings.Interface;

public interface BackupSettingsInstance extends Interface {
    public static final Interface.Manager<BackupSettingsInstance, Proxy> MANAGER = BackupSettingsInstance_Internal.MANAGER;

    public interface Proxy extends BackupSettingsInstance, Interface.Proxy {
    }

    void setBackupEnabled(boolean z, boolean z2);
}
