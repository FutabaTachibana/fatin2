package org.f14a.fatin2.config;

import com.google.gson.JsonArray;
import org.junit.jupiter.api.Test;

public class ConfigManagerTest {
    @Test
    void load_initGlobal() {
        JsonArray json = ConfigManager.getGlobalJson();
        System.out.println(json);
    }
}
