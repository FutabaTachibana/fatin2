package org.f14a.fatin2.plugin;

public interface Fatin2Plugin {
    // Call when the plugin is loaded
    void onLoad();
    // Call when the plugin is enabled
    void onEnable();
    // Call when the plugin is disabled
    void onDisable();
    String getName();
    String getDisplayName();
    default String getVersion() {
        return "1.0.0";
    }
    default String getAuthor() {
        return "Anonymous";
    }
    default String getDescription() {
        return "No description provided.";
    }
}
