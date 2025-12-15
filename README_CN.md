# Fatin2
一款基于 Java-WebSocket 的轻量聊天机器人框架与插件平台。

## 简介
Fatin2 是一个通过 WebSocket 连接上游（例如 Onebot 客户端）接收消息并分发给内置处理器或第三方插件处理的框架。目标是提供简单的消息分发、插件扩展能力与可配置的运行方式，便于快速开发机器人功能。

## 特性
- **高性能异步处理** - 基于线程池的异步事件处理机制
- **插件化架构** - 支持热加载/热重载插件，无需重启
- **完善的事件系统** - 灵活的事件监听器，支持优先级和取消机制
- **配置** -使用 YAML 配置应用。

## 系统要求与依赖
- Java 17 或更高版本
- Gradle 8.0+（或使用项目自带的 Gradle Wrapper）
- 支持的 Onebot 客户端。

## 快速开始（构建与运行）
### 使用 Gradle 构建
使用 `./gradlew shadowJar` 构建项目。

输出文件:  build/libs/fatin2-2.0.0.jar

### 运行
使用 `java -jar build/libs/fatin2-2.0.0.jar` 运行项目。

### IDE
推荐使用 IntelliJ IDEA 构建项目。

## 配置说明
默认示例配置位于 `config.yml`：

```yaml
# NapCat WebSocket 连接地址
websocket_url: ws://localhost:3001

# 访问令牌（如果 NapCat 配置了 token）
access_token: ""

# 日志配置
log: 
  # 日志级别:  TRACE, DEBUG, INFO, WARN, ERROR
  level: INFO
  # 日志文件路径
  path: ./logs

# 插件配置
plugin:
  # 插件目录
  directory: ./plugins
  # 自动重载插件
  auto_reload: true
```

## 核心概念
### 事件系统
框架的核心基于事件驱动。所有的消息、通知都会被转换为事件，插件通过监听事件来响应。

所有的消息响应事件都是异步触发，插件无须考虑异步执行逻辑。

### 插件系统
插件是功能的最小单元，每个插件：

- 独立的 JAR 文件
- 独立的类加载器
- 支持热加载/卸载
- 自动注册事件监听器

```
WebSocket 消息接收
    ↓
OneBotMessage 解析
    ↓
创建对应的 Event
    ↓
EventBus 分发事件
    ↓
按优先级调用监听器
    ↓
返回事件结果（TODO）
```
