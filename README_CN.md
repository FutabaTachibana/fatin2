# Fatin2
| [English](README.md) | [中文](README_CN.md) |

一款基于 Java-WebSocket 的轻量聊天机器人框架与插件平台。

## 简介
Fatin2 是一个通过 WebSocket 连接上游（例如 Onebot 客户端）接收消息并分发给内置处理器或第三方插件处理的框架。目标是提供简单的消息分发、插件扩展能力与可配置的运行方式，便于快速开发机器人功能。

## 特性
- **高性能异步处理** - 基于线程池的异步事件处理机制
- **插件化架构** - 支持热加载/热重载插件，无需重启，支持使用 Java 或 Kotlin 开发插件
- **完善的事件系统** - 灵活的事件监听器，支持优先级和取消机制
- **多种消息类型支持** - 支持私聊、群聊等多种消息类型
- **命令处理** - 内置命令解析与处理功能
- **会话管理** - 通过虚拟线程池模拟协程进行用户会话状态管理
- **跟踪发出的消息** - 通过回调函数跟踪和管理机器人发送的消息
- **配置** -使用 YAML 配置应用。

## 系统要求与依赖
- 操作系统：跨平台（Windows、Linux、macOS等）
- Java 21 或更高版本
- Gradle 8.0+（或使用项目自带的 Gradle Wrapper）
- 支持的 Onebot 客户端。

## 快速开始
### 构建 fatin2 主程序
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

### 运行
使用 `java -jar build/libs/fatin2-2.0.0-shadow.jar` 运行项目。

### IDE
推荐使用 IntelliJ IDEA 构建项目。

在 IntelliJ IDEA 中打开项目后，等待 Gradle 同步完成即可运行和调试。

## 配置说明
默认示例配置位于 `config.yml`：

```yaml
# WebSocket address
websocket_url: ws://localhost:3001
# Token (set "" if no token is required)
access_token: ""
# Command prefix for bot commands
command_prefix: "/"
# Debug mode
debug: false
plugin:
  # directory without dot or slash(backslash in Windows) means relative path to working directory.
  directory: plugins
  # watch plugin directory for changes and reload automatically
  auto_reload: true
```

## 核心概念
### 抽象化消息
框架将接收到的 OneBot 消息抽象为多种消息类型，并且提供丰富的接口进行处理，插件无需关系底层消息格式。

### 事件系统
框架的核心基于事件驱动。所有的消息、通知都会被转换为事件，插件通过监听事件来响应。

所有的消息响应事件都是异步触发，插件无须考虑异步执行逻辑。

插件可以通过对事件使用 `@Coroutine` 注解来启用虚拟线程支持，从而简化异步代码编写。

### 插件系统
插件是功能的最小单元，每个插件：

- 独立的 JAR 文件
- 独立的类加载器
- 支持热加载/卸载
- 自动注册事件监听器

### 消息处理流程
```
WebSocket 客户端接受消息
    ↓
消息解析器解析消息
    ↓
创建对应的 Event
    ↓
EventBus 分发事件
    ↓
按优先级调用监听器
```

## 其它
### 贡献
欢迎提交 issue 或 pull request 贡献代码。

### 许可证
本项目采用 **MIT** 许可证。

### AI说明
该项目部分内容由 AI 协助生成，但本人已对所有生成内容进行了审核与修改，确保代码质量与可用性。

项目中以下内容完全由 AI 生成: 
- README.md
- build.gradle
- logback.xml

项目中以下内容部分由 AI 生成:
- README_CN.md
- 部分代码及注释
