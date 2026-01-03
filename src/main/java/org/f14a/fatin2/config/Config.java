package org.f14a.fatin2.config;

import lombok.Getter;
import lombok.Setter;

/**
* Fatin2 的配置类，包含所有可配置的选项。
*/
@Setter
@Getter
public class Config {
    @ConfigItem(order = 0, label = "WebSocket 服务器地址",
            type = "string", defaultValue = "ws://localhost:3001", 
            description = "WebSocket 服务器的地址，通常是 NapCat 或其他 Onebot v11 兼容客户端的地址。")
    private String websocketUrl;
    @ConfigItem(order = 1, label = "WebSocket Token", type = "password", defaultValue = "",
            description = "连接 WebSocket 服务器时使用的 Token，没有则留空。")
    private String accessToken;
    @ConfigItem(order = 2, label = "命令前缀", type = "string", defaultValue = "/",
            description = "在私聊或群聊中触发命令时使用的前缀，例如 \"/help\"。")
    private String commandPrefix;
    @ConfigItem(order = 3, label = "调试模式", type = "boolean", defaultValue = "false",
            description = "启用后会输出更多调试信息，适合排查问题时使用。")
    private boolean debug;
    @ConfigItem(order = 4, label = "插件目录", type = "string", defaultValue = "plugins",
            description = "存放插件的目录路径。")
    private String pluginDirectory;
    @ConfigItem(order = 5, label = "自动重载插件", type = "boolean", defaultValue = "true",
            description = "启用后，修改插件文件后会自动重载插件，无需重启 Fatin2。")
    private boolean pluginAutoReload;
    @ConfigItem(order = 6, label = "启用内置权限插件", type = "boolean", defaultValue = "true",
            description = "启用后，插件可以使用内置权限插件来控制命令的访问权限，如果你使用了第三方的权限插件，请关闭此选项。")
    private boolean integratedPermission;
    @ConfigItem(order = 7, label = "启用内置帮助插件", type = "boolean", defaultValue = "true",
            description = "启用后，Fatin2 会提供内置的帮助命令，自动生成 \"help\" 指令的内容。")
    private boolean integratedHelp;
    @ConfigItem(order = 8, label = "WebUI 端口", type = "integer", defaultValue = "8080",
            description = "WebUI 服务器监听的端口。")
    private int webUIPort;
    @ConfigItem(order = 9, label = "WebUI Token", type = "password", defaultValue = "114514",
            description = "WebUI 登录所需的 Token。")
    private String webUIToken;
}
