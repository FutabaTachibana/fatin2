package org.f14a.fatin2.config;

import com.google.gson.JsonArray;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigManagerTest {
    @BeforeAll
    static void beforeAll() {
        Path path = Path.of("config.yml");
        if (Files.exists(path)) {
            path.toFile().delete();
        }
    }
    @Test
    void load_initGlobal() {
        JsonArray json = ConfigManager.getGlobalConfig();
        assertNotNull(json);
        assertNotEquals(0, json.size());
    }
}
