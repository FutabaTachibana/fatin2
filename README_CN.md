# Fatin2
| [English](README.md) | [中文](README_CN.md) |

一款基于 [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket) 的轻量聊天机器人框架与插件平台，可作为 **WebSocket 客户端** 对接兼容 **OneBot** 的上游实现，接收事件并分发给内置处理器或第三方插件。

## 目录
- [特性](#特性)
- [系统要求与依赖](#系统要求与依赖)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [核心概念](#核心概念)
- [开发插件（最小示例）](#开发插件最小示例)
- [常见问题](#常见问题)
- [其它](#其它)

## 特性
- **高性能异步处理** - 基于线程池的异步事件处理机制
- **插件化架构** - 支持热加载/热重载插件，无需重启，支持使用 Java 或 Kotlin 开发插件
- **完善的事件系统** - 灵活的事件监听器，支持优先级和取消机制
- **多种消息类型支持** - 支持私聊、群聊等多种消息类型
- **抽象化消息模型** - 使用 [Gson](https://github.com/google/gson) 进行消息解析与封装，简化消息处理
- **命令处理** - 内置命令解析与处理功能，支持 SHELL 风格命令与注解式命令监听（见 Demo 插件）
- **会话/对话式交互** - 通过 `@Coroutines` + `event.wait(...)` 提供“等待下一条消息”的会话式 API（底层使用虚拟线程，便于写同步风格代码）
- **跟踪发出的消息** - 支持通过回调或 `Future` 跟踪发送结果，并进行撤回/回复等后续操作
- **配置** - 使用 YAML 配置应用

## 系统要求与依赖
- 操作系统：跨平台（Windows、Linux、macOS 等）
- Java：21 或更高版本
- 构建工具：Gradle 8+（推荐直接使用项目自带的 [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)）
- 上游：支持 WebSocket 的 OneBot 兼容实现（本项目作为客户端连接上游）

核心依赖（部分）：
- [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket) - WebSocket 客户端
- [Gson](https://github.com/google/gson) - JSON 解析
- [SnakeYAML](https://bitbucket.org/snakeyaml/snakeyaml) - YAML 配置
- [SLF4J](https://www.slf4j.org/) + [Logback](https://logback.qos.ch/) - 日志
- [Gradle Shadow](https://github.com/johnrengelman/shadow) - 打包 Shadow JAR

## 快速开始

### 对于使用者
直接下载最新发布的 **Shadow JAR**（包含所有依赖），然后运行即可。

```shell
# Linux / macOS / Windows
java -jar fatin2-<version>-shadow.jar
```

### 构建 fatin2 主程序

Linux / macOS（bash）：
```bash
# 构建项目
./gradlew build

# 构建标准 JAR（不含依赖）
./gradlew jar

# 构建 Shadow JAR（包含所有依赖）
./gradlew shadowJar

# 构建所有类型的 JAR
./gradlew buildAll
```

Windows（PowerShell）：
```powershell
# 构建项目
.\gradlew.bat build

# 构建标准 JAR（不含依赖）
.\gradlew.bat jar

# 构建 Shadow JAR（包含所有依赖）
.\gradlew.bat shadowJar

# 构建所有类型的 JAR
.\gradlew.bat buildAll
```

### 运行
推荐从 `run/` 目录运行（该目录包含运行时配置、插件与日志目录）：
- `run/config.yml`
- `run/plugins/`
- `run/logs/`

方式一：直接运行 Shadow JAR（以实际版本号为准）
```bash
# 进入 run 目录后运行（建议）
java -jar ..\build\libs\fatin2-<version>-shadow.jar
```

方式二：使用 Gradle 任务（自动将工作目录设置为 `run/`）
```bash
# Linux/macOS
./gradlew runBot

# Windows
.\gradlew.bat runBot
```

> 程序默认读取 `config.yml`；也可以传入参数指定配置文件路径：`java -jar ... <configPath>`。

### IDE
推荐使用 IntelliJ IDEA 构建项目。

在 IntelliJ IDEA 中打开项目后，等待 Gradle 同步完成即可运行和调试。

## 配置说明
默认示例配置位于 `src/main/resources/config.yml`，运行时建议使用 `run/config.yml`。

```yaml
# WebSocket 连接地址
websocket_url: ws://localhost:3001
# Token (如果没有请设为 "")
access_token: ""
# 命令前缀
command_prefix: "/"
# 调试模式
debug: false
plugin:
  # 如果没有特别指定，插件目录相对于工作目录
  directory: plugins
  # 监视插件目录变化并自动热重载
  auto_reload: true
  # 是否启用内置功能
  integrated:
    # 权限管理，如果使用其它权限插件需要设置为 false
    permission: true
    # 自动生成 help 命令
    help_generator: true
```

字段说明（常用）：
- `websocket_url`：上游 OneBot WebSocket 地址（本项目会 **主动连接** 该地址）
- `access_token`：鉴权 token；不需要鉴权可设为 `""`
- `command_prefix`：命令前缀，影响 `@OnCommand` 的触发（Demo 中例如 `/echo`）
- `debug`：是否开启调试日志（内部会设置 `log.level=DEBUG/INFO`）
- `plugin.directory`：插件目录（相对工作目录；通常配合 `run/` 使用）
- `plugin.auto_reload`：监视插件目录变化并自动热重载

## 核心概念

### 抽象化消息
框架将接收到的 OneBot 消息抽象为多种消息类型，并且提供丰富的接口进行处理，插件无需关心底层消息格式。

### 事件系统
框架的核心基于事件驱动。所有的消息、通知都会被转换为事件，插件通过监听事件来响应。

所有的消息响应事件默认异步触发，插件一般不需要手动管理线程。

插件可以通过对事件处理方法使用 `@Coroutines` 注解来启用会话式 API，从而更方便地编写“等待回复”的交互逻辑。

进一步阅读（源码）：
- 程序入口：`src/main/java/org/f14a/fatin2/Main.java`
- Demo 插件监听器：`src/main/java/org/f14a/demoplugin/EventListener.java`

### 命令支持
Fatin2 内置命令处理器，提供命令解析、分词、权限管理等功能。

Demo 插件中包含若干命令示例：
- `/echo`：回显消息
- `/guess`：猜数字（会话等待示例）
- `/sendandrecall`：发送后延时撤回

### 插件系统
插件是功能的最小单元，每个插件：
- 独立的 JAR 文件
- 独立的类加载器
- 支持热加载/卸载
- 自动注册事件监听器

### 消息处理流程
```
WebSocket 客户端接收消息（来自 OneBot 上游）
    ↓
消息解析器解析消息
    ↓
创建对应的 Event
    ↓
EventBus 分发事件
    ↓
按优先级调用监听器
    ↓
监听器处理事件
```

## 开发插件（最小示例）

项目内置了一个 Demo 插件（`org.f14a.demoplugin`），可作为最小示例参考。

### 1) 构建并输出插件到 run/plugins
```bash
# Linux/macOS
./gradlew buildPlugin

# Windows
.\gradlew.bat buildPlugin
```
构建产物会输出到：`run/plugins/demoplugin-<version>.jar`

### 2) 关键注解与能力速览（来自 Demo）
- `@EventHandler`：监听原始消息事件（如包含关键词回复）
- `@OnCommand(...)`：监听命令（如 `echo/guess`）
- `@Coroutines`：启用会话式 API（如 `event.wait(...)` 等待下一条消息）

进一步阅读（源码）：
- Demo 插件入口：`src/main/java/org/f14a/demoplugin/Main.java`
- Demo 插件监听器：`src/main/java/org/f14a/demoplugin/EventListener.java`

## 常见问题

### 机器人无法连接 / 一直在 Connecting
- 检查 `websocket_url` 是否可达、端口是否正确
- 如果上游需要鉴权，确认 `access_token` 是否与上游一致

### 插件未加载 / 热重载不生效
- 确认插件 JAR 位于 `plugin.directory` 指定目录（默认 `run/plugins/`）
- 确认 `plugin.auto_reload=true`
- 建议从 `run/` 作为工作目录启动（`./gradlew runBot` / `runPlugin` 已自动设置）

## 其它

### 贡献
欢迎提交 issue 或 pull request 贡献代码。

### 许可证
本项目采用 **[MIT](LICENSE.txt)** 许可证。

