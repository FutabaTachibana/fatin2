# Fatin2

一款基于 WebSocket 的轻量聊天机器人框架与插件平台。

## 简介
fatin2 是一个通过 WebSocket 连接上游（例如 OneBot 客户端或自定义服务）接收消息并分发给内置处理器或第三方插件处理的框架。目标是提供简单的消息分发、插件扩展能力与可配置的运行方式，便于快速开发机器人功能。

## 主要功能
- WebSocket 客户端：基于 `org.java-websocket` 实现。
- 消息分发：Fatin2 将解析后的消息分发到不同的 Handler。
- 插件机制：支持将功能打包为插件 JAR 并放入插件目录。
- YAML 配置：通过 `config.yml` 或自定义路径加载运行配置。

## 系统要求与依赖
- JDK：Java17 及以上。
- 构建：Gradle（建议使用仓库自带 Gradle Wrapper：`./gradlew` / `gradlew.bat`）。仓库 wrapper 指定的 Gradle 版本：8.8（见 `gradle/wrapper/gradle-wrapper.properties`）。
- 主要依赖：
  - org.java-websocket:Java-WebSocket:1.6.0
  - com.google.code.gson:gson:2.13.2
  - org.yaml:snakeyaml:2.5
  - org.slf4j:slf4j-api:2.0.9
  - ch.qos.logback:logback-classic:1.5.21

## 快速开始（构建与运行）
1. 

## 配置说明
默认示例配置位于 `config.yml`：

示例（resources/config.yml）:
```yaml
- websocket_url: ws://localhost:3001
- access_token: ""            # 如需要，可填入 token
- debug: false
- plugin:
    directory: ./plugins
    auto_reload: true
```

常见配置项说明：
- websocket_url：要连接的 WebSocket 地址
- access_token：若服务使用 Bearer 授权，请填入 token（请勿把真实 token 提交到仓库）
- debug：是否启用 debug 日志
- plugin.directory：插件目录（默认 `./plugins`）
- plugin.auto_reload：是否自动重载插件

## 插件与扩展
- 插件目录：由配置读取（默认 `./plugins`）。
- 加载方式：`PluginLoader` 会扫描目录下的 `.jar` 文件，读取 JAR 的 `MANIFEST.MF` 中的 `Main-Class`，通过 ClassLoader 加载并实例化该类；该类必须实现 `Fatin2Plugin` 接口（接口定义见 `src/main/java/org/f14a/fatin2/plugin/Fatin2Plugin.java`）。
- 插件生命周期：`onLoad()` -> `onEnable()`；禁用时调用 `onDisable()`。
- 打包要求：插件 JAR 的 MANIFEST 中需指定 `Main-Class`，并将实现类打包入 JAR。

已知限制 / TODO（插件相关）:
- `PluginLoader` 中有注释 TODO：自动重载 (`auto_reload`) 未完善，且缺少重复加载检测（见 `PluginLoader.java` 注释）。
- 当前仓库未包含示例插件工程或插件打包示例，建议补充一个最小示例以便开发者参考。

## 开发与贡献指南
- 本地构建：
  ./gradlew clean build
  或：
  ./gradlew clean fatJar

- 运行（开发）：
  ./gradlew runBot --args="path/to/config.yml"

- 代码风格与测试：
  - 仓库当前未包含自动化测试（`test/` 目录为空），建议新增单元测试以覆盖关键模块（`ConfigLoader`、`PluginLoader`、`MessageDispatcher`）。
  - TODO：可加入代码质量工具（Spotless / Checkstyle / PMD）并在 CI 中运行。

- 提交流程：Fork -> 新分支 -> 提交 -> 发起 PR；PR 需说明变更目的并尽量包含相关测试。

## 已知问题与 TODO
- 插件自动重载与重复加载检测未实现（见 `PluginLoader.java` 注释）。
- 根 `config.yml` 含疑似真实 `access_token`（请替换为示例并发布安全说明）。
- `DefaultConfig` 与 resources config 中的 websocket 地址不一致，需核对并统一默认值。
- 无优雅的进程退出/信号处理（`Main` 使用 `Thread.currentThread().join()` 阻塞，未实现 SIGTERM 等优雅停机逻辑）。
- 缺少 LICENSE 文件（请在仓库根添加许可证并在 README 中声明）。
- 缺少示例插件工程与自动化测试用例，建议优先补充以提升可用性与稳定性。

## 许可证与联系
- MIT
- GitHub Issues

## 附录：常用命令（示例）
- 构建（生成 fat jar）：
  ./gradlew clean fatJar
- 开发运行：
  ./gradlew runBot --args="config.yml"
- 直接运行 jar：
  java -jar build/libs/fatin2-2.0.0-all.jar config.yml  # 请以实际构建产物为准

## 下一步建议
- 请确认并告知目标 Java 版本（默认建议 Java 11）。
- 是否允许我替换仓库根 `config.yml` 中的敏感 `access_token` 为占位符 `"<YOUR_TOKEN_HERE>"` 并在仓库中添加安全提示？
- 是否要我为插件开发添加一个最小示例工程并把它加入 `examples/` 或 `plugins/` 目录？

## 问题追踪（简短的变更清单）
- DONE: 阅读仓库并生成 README 草稿（含 TODO 与已知问题）。
- TODO: 等待你确认 Java 版本、许可证与是否替换敏感配置；我可根据确认继续迭代 README 或执行代码/文件修改（例如覆盖 `config.yml`、添加 LICENSE、添加示例插件）。

----

