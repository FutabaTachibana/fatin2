package org.f14a.fatin2.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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
        List<Map<String, Object>> all = ConfigManager.getGlobalWrapper().getAll();
        assertNotNull(all);
        assertNotEquals(0, all.size());
    }
}
