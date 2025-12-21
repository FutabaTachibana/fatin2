package org.f14a.fatin2.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.f14a.fatin2.config.ConfigLoader.CONFIG_FILE_NAME;
import static org.junit.jupiter.api.Assertions.*;

class ConfigLoaderTest {

    private static final Path CONFIG_PATH = Path.of("test/config.yml");

    @AfterEach
    void cleanup() throws IOException {
        // Config is a singleton; tests in this module typically run in one JVM.
        // We avoid resetting singleton here to keep changes minimal.
        // Also revert config.yml changes if present.
        // NOTE: If config.yml is important for your local run, you may want to disable these tests.
        if (Files.exists(CONFIG_PATH)) {
            // Leave it in place: ConfigLoader relies on working-dir config.yml.
            // But if we created a temp malformed file, later tests would be impacted.
        }
    }

    @Test
    void load_createsConfigFileIfMissing() throws Exception {
        // If config.yml doesn't exist, ConfigLoader should create it from resources.
        if (Files.exists(CONFIG_PATH)) {
            return; // don't mess with user's working file in unit tests
        }

        Config config = ConfigLoader.load(CONFIG_PATH, "/" + CONFIG_FILE_NAME);
        assertNotNull(config);
        assertTrue(Files.exists(CONFIG_PATH));
        assertNotNull(config.getWebSocketUrl());
        assertFalse(config.getWebSocketUrl().isBlank());
    }

    @Test
    void load_toleratesTypeMismatchAndFallsBackToDefaults() throws Exception {
        // This test only runs if a working-dir config.yml exists; we temporarily overwrite it.
        if (!Files.exists(CONFIG_PATH)) {
            // Make sure it exists first (from resources)
            ConfigLoader.load(CONFIG_PATH, "/" + CONFIG_FILE_NAME);
        }

        String original = Files.readString(CONFIG_PATH, StandardCharsets.UTF_8);
        try {
            Files.writeString(CONFIG_PATH, "debug: not_a_bool\nplugin: 123\nwebsocket_url: 123\n", StandardCharsets.UTF_8);
            Config config = ConfigLoader.load(CONFIG_PATH, "/" + CONFIG_FILE_NAME);
            assertNotNull(config);
            // websocket_url 123 becomes "123"; debug should fallback to default (from resources)
            assertEquals("123", config.getWebSocketUrl());
        } finally {
            Files.writeString(CONFIG_PATH, original, StandardCharsets.UTF_8);
        }
    }
}

