package io.fydeos.arc.policymanager;

/** Default runtime-permission disposition from the ArcPolicy "defaultPermissionPolicy" field. */
enum PermissionPolicy {
    PERMISSION_POLICY_UNSPECIFIED, PROMPT, GRANT, DENY;

    int toInt() {
        return ordinal();
    }

    static PermissionPolicy byName(String input) {
        if (input == null) {
            return null;
        }
        for (PermissionPolicy p : values()) {
            if (p.name().equalsIgnoreCase(input)) {
                return p;
            }
        }
        return null;
    }

    static PermissionPolicy byIndex(int index) {
        PermissionPolicy[] values = values();
        return (index >= 0 && index < values.length) ? values[index] : PERMISSION_POLICY_UNSPECIFIED;
    }
}
