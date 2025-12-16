package org.f14a.fatin2.config;


/*
* This class is used to store configuration settings for the application.
* */
public class Config {
    private String webSocketUrl;
    private String accessToken;
    private String commandPrefix;
    private boolean debug;
    private String pluginDirectory;
    private boolean pluginAutoReload;
    // Getters and Setters
    public String getWebSocketUrl() {
        return this.webSocketUrl;
    }
    public void setWebSocketUrl(Object webSocketUrl) {
        this.webSocketUrl = webSocketUrl.toString();
    }
    public String getAccessToken() {
        return this.accessToken;
    }
    public void setAccessToken(Object accessToken) {
        this.accessToken = accessToken.toString();
    }
    public String getCommandPrefix() {
        return commandPrefix;
    }
    public void setCommandPrefix(Object commandPrefix) {
        this.commandPrefix = commandPrefix.toString();
    }
    public boolean isDebug() {
        return this.debug;
    }
    public void setDebug(Object debug) {
        if (debug instanceof Boolean) {
            this.debug = (Boolean) debug;
        } else {
            this.debug = Boolean.parseBoolean(debug.toString());
        }
    }
    public String getPluginDirectory() {
        return this.pluginDirectory;
    }
    public void setPluginDirectory(Object pluginDirectory) {
        this.pluginDirectory = pluginDirectory.toString();
    }
    public boolean isPluginAutoReload() {
        return this.pluginAutoReload;
    }
    public void setPluginAutoReload(Object pluginAutoReload) {
        if (pluginAutoReload instanceof Boolean) {
            this.pluginAutoReload = (Boolean) pluginAutoReload;
        } else {
            this.pluginAutoReload = Boolean.parseBoolean(pluginAutoReload.toString());
        }
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
