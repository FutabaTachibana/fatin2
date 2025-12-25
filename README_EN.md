# Fatin2
| [English](README.md) | [中文](README_CN.md) |

A lightweight chatbot framework and plugin platform built on top of [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket). Fatin2 acts as a **WebSocket client** that connects to **OneBot-compatible** upstream implementations, receives events, and dispatches them to built-in handlers or third-party plugins.

## Table of contents
- [Features](#features)
- [Requirements & dependencies](#requirements--dependencies)
- [Quickstart](#quickstart)
- [Configuration](#configuration)
- [Core concepts](#core-concepts)
- [Plugin development (minimal example)](#plugin-development-minimal-example)
- [Troubleshooting](#troubleshooting)
- [Other](#other)

## Features
- **High-performance async handling** - event processing based on a thread pool
- **Plugin architecture** - hot-load / hot-reload plugins without restarting; plugins can be written in Java or Kotlin
- **Event system** - flexible event listeners with priority and cancellation
- **Multiple message types** - private / group messages and more
- **Abstracted message model** - JSON parsing and modeling via [Gson](https://github.com/google/gson)
- **Command framework** - shell-style parsing + annotation-based command handlers (see the demo plugin)
- **Session-like interactions** - `@Coroutines` + `event.wait(...)` for “wait for the next message” flows (implemented with virtual threads, so you can write sync-style code)
- **Track outgoing messages** - callback / future-based APIs enable follow-up actions like reply/recall
- **YAML configuration** - configured via YAML

## Requirements & dependencies
- OS: Windows / Linux / macOS
- Java: 21+
- Build tool: Gradle 8+ (recommended: use the bundled [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html))
- Upstream: a OneBot-compatible implementation that provides a WebSocket endpoint (Fatin2 connects to it)

Key dependencies (partial):
- [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket) - WebSocket client
- [Gson](https://github.com/google/gson) - JSON parsing
- [SnakeYAML](https://bitbucket.org/snakeyaml/snakeyaml) - YAML parsing
- [SLF4J](https://www.slf4j.org/) + [Logback](https://logback.qos.ch/) - logging
- [Gradle Shadow](https://github.com/johnrengelman/shadow) - building a shadow (fat) JAR

## Quickstart

### For users
Download the latest released **shadow JAR** (with all dependencies included) and run it directly.

```shell
# Linux / macOS / Windows
java -jar fatin2-<version>-shadow.jar
```

### Build

Linux / macOS (bash):
```bash
./gradlew build
./gradlew jar
./gradlew shadowJar
./gradlew buildAll
```

Windows (PowerShell):
```powershell
.\gradlew.bat build
.\gradlew.bat jar
.\gradlew.bat shadowJar
.\gradlew.bat buildAll
```

### Run
It’s recommended to run the bot from the `run/` directory, which contains runtime files:
- `run/config.yml`
- `run/plugins/`
- `run/logs/`

Option 1: run the shadow JAR (replace `<version>` with the actual one)
```bash
java -jar ..\build\libs\fatin2-<version>-shadow.jar
```

Option 2: run via Gradle (sets working directory to `run/` automatically)
```bash
# Linux/macOS
./gradlew runBot

# Windows
.\gradlew.bat runBot
```

> By default, Fatin2 reads `config.yml`. You can also pass a config path as the first argument: `java -jar ... <configPath>`.

## Configuration
The default example config is at `src/main/resources/config.yml`. For actual runs, use `run/config.yml`.

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
  # directory without dot or slash(backslash in Windows) means relative path to working directory
  directory: plugins
  # watch plugin directory for changes and reload automatically
  auto_reload: true
  # whether to load plugins bundled with the bot
  integrated:
    # integrated permission manager, set false if you want to use other permission manager
    permission: true
    # it will generate help of command by its description(if exists)
    help_generator: true
```

Common fields:
- `websocket_url`: the upstream OneBot WebSocket URL (**Fatin2 actively connects** to this endpoint)
- `access_token`: auth token; set to `""` if no token is needed
- `command_prefix`: command prefix used by command handlers (demo uses `/echo`, etc.)
- `debug`: enables debug logs internally (sets `log.level=DEBUG/INFO`)
- `plugin.directory`: plugin directory (relative to working dir; usually under `run/`)
- `plugin.auto_reload`: watch the plugin directory and reload on changes

## Core concepts

### Message abstraction
Incoming OneBot messages are abstracted into message types with a richer API, so plugins don’t need to deal with raw payloads.

### Event system
Fatin2 is event-driven. All messages/notifications are converted into events and dispatched to listeners.

By default, handlers run asynchronously. If you annotate your handler with `@Coroutines`, you can use the session-like API (e.g. `event.wait(...)`) for interactive flows.

Further reading (source):
- Entry point: `src/main/java/org/f14a/fatin2/Main.java`
- Demo listener: `src/main/java/org/f14a/demoplugin/EventListener.java`

### Commands
Fatin2 includes a command processor (parsing, arguments, permissions, etc.).

The demo plugin provides examples:
- `/echo`: echo back the message
- `/guess`: guess-the-number game (with `event.wait(...)`)
- `/sendandrecall`: send a message and recall it later

### Plugin system
A plugin is the smallest unit of functionality:
- packaged as an independent JAR
- loaded with an isolated classloader
- supports hot load/unload
- listeners are registered automatically

### Event flow
```
WebSocket client receives an event (from upstream OneBot)
    ↓
Payload is parsed
    ↓
An Event object is created
    ↓
EventBus dispatches the event
    ↓
Listeners are invoked by priority
    ↓
Your handler runs
```

## Plugin development (minimal example)
This repository ships with a demo plugin (`org.f14a.demoplugin`) as the minimal reference.

### 1) Build the plugin into run/plugins
```bash
# Linux/macOS
./gradlew buildPlugin

# Windows
.\gradlew.bat buildPlugin
```
The output JAR will be written to: `run/plugins/demoplugin-<version>.jar`.

### 2) Useful annotations & APIs (from the demo)
- `@EventHandler`: handle raw message events
- `@OnCommand(...)`: handle commands (e.g. `echo`, `guess`)
- `@Coroutines`: enable a session-like API (e.g. `event.wait(...)`)

Further reading (source):
- Demo plugin entry: `src/main/java/org/f14a/demoplugin/Main.java`
- Demo listener: `src/main/java/org/f14a/demoplugin/EventListener.java`

## Troubleshooting

### Stuck at “Connecting” / can’t connect
- Verify `websocket_url` is reachable and the port is correct
- If the upstream requires auth, make sure `access_token` matches

### Plugins not loaded / hot reload not working
- Make sure the plugin JAR is under `plugin.directory` (default: `run/plugins/`)
- Make sure `plugin.auto_reload=true`
- Run with `run/` as the working directory (Gradle tasks `runBot` / `runPlugin` already do this)

## Other

### Contributing
Issues and pull requests are welcome.

### License
Licensed under the **[MIT](LICENSE.txt)** license.
