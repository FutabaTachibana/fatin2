package org.f14a.fatin2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogbackVersionSmokeTest {
    @Test
    void logbackClassicIsOnClasspath() {
        Package p = ch.qos.logback.classic.LoggerContext.class.getPackage();
        assertNotNull(p);
        // Not asserting exact version; just make it visible in test output when needed.
        System.out.println("logback-classic version=" + p.getImplementationVersion());
    }
}

