package org.f14a.fatin2.config;


/**
* This class is used to store configuration settings for the application.
*/
public class Config {
    private String webSocketUrl;
    private String accessToken;
    private String commandPrefix;
    private boolean debug;
    private String pluginDirectory;
    private boolean pluginAutoReload;
    private boolean enablePermission;
    private boolean enableHelp;
    // Getters and Setters
    public String getWebSocketUrl() {
        return this.webSocketUrl;
    }
    public void setWebSocketUrl(String webSocketUrl) {
        this.webSocketUrl = webSocketUrl;
    }
    public String getAccessToken() {
        return this.accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getCommandPrefix() {
        return commandPrefix;
    }
    public void setCommandPrefix(String commandPrefix) {
        this.commandPrefix = commandPrefix;
    }
    public boolean isDebug() {
        return this.debug;
    }
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    public String getPluginDirectory() {
        return this.pluginDirectory;
    }
    public void setPluginDirectory(String pluginDirectory) {
        this.pluginDirectory = pluginDirectory;
    }
    public boolean isPluginAutoReload() {
        return this.pluginAutoReload;
    }
    public void setPluginAutoReload(boolean pluginAutoReload) {
        this.pluginAutoReload = pluginAutoReload;
    }
    public boolean isEnablePermission() {
        return enablePermission;
    }
    public void setEnablePermission(boolean enablePermission) {
        this.enablePermission = enablePermission;
    }
    public boolean isEnableHelp() {
        return enableHelp;
    }
    public void setEnableHelp(boolean enableHelp) {
        this.enableHelp = enableHelp;
    }

    private static Config instance;
    public static Config getConfig(){
        return Config.instance;
    }

    public Config() {
        if(Config.instance == null) {
            Config.instance = this;
        }
        else {
            throw new RuntimeException("Config instance already exists!");
        }
    }
}
