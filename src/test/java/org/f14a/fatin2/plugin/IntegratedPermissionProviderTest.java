package org.f14a.fatin2.plugin;

import org.f14a.fatin2.plugin.integrated.IntegratedPermissionProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntegratedPermissionProviderTest {
    @Test
    void integratedPermissionProvider_handleYaml() {
        IntegratedPermissionProvider permissionProvider = new IntegratedPermissionProvider();
        // Just ensure no exceptions are thrown during loading.
        permissionProvider.onLoad();
    }
}
