package org.f14a.fatin2.plugin;

import org.f14a.fatin2.event.EventBus;
import org.f14a.fatin2.plugin.integrated.IntegratedHelpGenerator;
import org.f14a.fatin2.plugin.integrated.IntegratedPermissionProvider;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PluginBasicsTest {

    @Test
    void integratedPlugins_haveValidNames() {
        Fatin2Plugin help = new IntegratedHelpGenerator();
        assertNotNull(help.getName());
        assertFalse(help.getName().isBlank());
        assertTrue(help.getName().matches("^[a-zA-Z0-9_-]+$"));

        Fatin2Plugin perm = new IntegratedPermissionProvider(Set.of(), Set.of());
        assertNotNull(perm.getName());
        assertFalse(perm.getName().isBlank());
        assertTrue(perm.getName().matches("^[a-zA-Z0-9_-]+$"));
    }

    @Test
    void pluginWrapper_disableAlwaysUnregisters_evenIfNotEnabled() {
        // Ensure EventBus singleton exists.
        new EventBus();

        Fatin2Plugin p = new Fatin2Plugin() {
            @Override public void onLoad() {}
            @Override public void onEnable() {}
            @Override public void onDisable() { throw new RuntimeException("boom"); }
            @Override public String getName() { return "test"; }
            @Override public String getDisplayName() { return "Test"; }
        };

        PluginWrapper wrapper = new PluginWrapper(p, null, "Integrated");

        // Even if onDisable throws, disable() shouldn't propagate and should clear enabled.
        wrapper.enable();
        assertTrue(wrapper.isEnabled());
        assertDoesNotThrow(wrapper::disable);
        assertFalse(wrapper.isEnabled());

        // Calling disable again should be safe.
        assertDoesNotThrow(wrapper::disable);
    }

    @Test
    void pluginLoader_createWrapperFromJar_handlesNonFileGracefully() {
        // PluginManager singleton is normally initialized at runtime; for this unit test we just
        // verify PluginLoader doesn't crash for invalid input.
        assertDoesNotThrow(() -> assertNull(PluginLoader.createWrapperFromJar(null)));
        assertDoesNotThrow(() -> assertNull(PluginLoader.createWrapperFromJar(new File("not-exist.jar"))));
    }
}
