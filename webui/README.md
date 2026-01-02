# Fatin2 WebUI
这个是 Fatin2 的 Web 用户界面 (WebUI)，用于通过浏览器与 Fatin2 进行交互和管理。

## 项目简介
基于 Vue 3 和 Element Plus 构建，提供直观的用户界面，方便用户配置和监控 Fatin2 的运行状态。

### 依赖
- axios
- element-plus
- vue-router

### 调试
```bash
bun run dev
```

## 界面

- [ ] 首页 - 显示 Fatin2 的状态和基本信息
- [ ] 配置页面 - 允许用户查看和修改 Fatin2 的配置
- [ ] 日志页面 - 显示 Fatin2 的运行日志
- [ ] 插件管理界面 - 管理和配置 Fatin2 的插件

## API
WebUI 通过 RESTful API 与 Fatin2 后端通信。确保后端 API 可用并正确配置。

### 需要实现的的 API 端点

### 基础
- [ ] GET /api/status - 获取 Fatin2 的当前状态

返回一个结构体，包含以下字段：
```json
{
  "uptime": "string",
  "version": "string",
  "config_version": "string",
  "bot_nickname": "string",
  "bot_id": "number",
  "active_connections": "number",
  "cpu_usage": "number",
  "memory_usage": "number"
}
```

### 连接
- [ ] GET /api/ws/clients - 获取当前连接的 WebSocket 客户端列表
- [ ] GET /api/ws/logs - 实时日志流


WebSocket 客户端:
```json
{
  "id": "string",
  "address": "string",
  "status": "string"
}
```

### 设置
- [ ] GET /api/settings - 获取当前设置结构和值
- [ ] POST /api/settings - 更新配置

表单结构: 
```json
[
  {
    "order": 0,
    "label": "WebSocket 服务器地址",
    "type": "string",
    "defaultValue": "ws://localhost:3001",
    "description": "WebSocket 服务器的地址，通常是 NapCat 或其他 Onebot v11 兼容客户端的地址。",
    "enable": false,
    "constraints": "",
    "options": [],
    "value": "ws://localhost:3001"
  }
]
```

### 插件
- [ ] GET /api/plugins - 返回插件列表
- [ ] POST /api/plugins/{id}/toggle - 开关插件
- [ ] GET /api/plugins/{id}/config - 获取插件配置结构和值
- [ ] POST /api/plugins/{id}/config - 更新插件配置

插件列表: 
```json
[
    {
        "id": "string",
        "name": "string",
        "enabled": "boolean",
        "can_hot_reload": "boolean",
        "version": "string",
        "author": "string",
        "description": "string",
        "has_config": "boolean"
    }
]
```

### 鉴权
- [ ] POST /api/auth/login - 用户登录