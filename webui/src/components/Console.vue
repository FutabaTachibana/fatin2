<template>
  <div class="content-wrapper">
    <div class="content-header">
      <h1>实时日志</h1>
      <div class="header-controls">
        <el-button circle @click="toggleTheme" :icon="isDark ? Moon : Sunny" />
      </div>
    </div>

    <div class="console-container" :class="isDark ? 'theme-dark' : 'theme-light'">
      <div class="console-box" ref="consoleRef">
        <div v-for="(log, index) in logs" :key="index" class="log-line">
          {{ log }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, onUnmounted, nextTick} from 'vue'
import { Moon, Sunny } from '@element-plus/icons-vue'

const logs = ref([])
const consoleRef = ref(null)
const isDark = ref(true)
let debugTimer = null
let ws = null

const toggleTheme = () => {
  isDark.value = !isDark.value
}

const addLog = (message) => {
  logs.value.push(message)
  if (logs.value.length > 1000) logs.value.shift()

  // 确保 DOM 更新后滚动到底部
  nextTick(() => {
    if (consoleRef.value) {
      consoleRef.value.scrollTop = consoleRef.value.scrollHeight
    }
  })
}

onMounted(() => {
  // 自动检测系统浅色模式
  if (window.matchMedia && window.matchMedia('(prefers-color-scheme: light)').matches) {
    isDark.value = false
  }

  // --- 调试模式：生成假日志 (生产环境请注释掉此块) ---
  // --- Debug Mode: Generate fake logs (Comment out in production) ---
  addLog(`[INFO] Console initialized. Waiting for logs...`)

  debugTimer = setInterval(() => {
    const levels = ['[INFO]', '[WARN]', '[DEBUG]', '[ERROR]']
    const level = levels[Math.floor(Math.random() * levels.length)]
    const time = new Date().toLocaleTimeString()
    // 模拟日志内容
    addLog(`${time} ${level} [System] Processing event #${Math.floor(Math.random() * 9000) + 1000} from client.`)
  }, 1500)
  // -----------------------------------------------------

  // --- 生产环境：WebSocket 连接 (生产环境请取消注释并替换上面的调试代码) ---
  // --- Production: WebSocket Connection (Uncomment and replace debug code above) ---
  /*
  // 替换为实际的 WebSocket 地址，例如 `ws://${location.host}/ws/logs`
  ws = new WebSocket(`ws://${location.host}/ws/logs`)

  ws.onopen = () => {
    addLog('[INFO] Connected to log server.')
  }

  ws.onmessage = (event) => {
    addLog(event.data)
  }

  ws.onclose = () => {
    addLog('[WARN] Connection lost.')
  }
  */
})

onUnmounted(() => {
  if (debugTimer) clearInterval(debugTimer)
  if (ws) ws.close()
})
</script>

<style scoped>
.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.console-container {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  border: 1px solid;
  /* 确保容器本身不产生滚动条 */
  display: flex;
  flex-direction: column;
}

.console-box {
  /* 调整高度适配窗口: 100vh - (Header高度 + Padding + 底部留白) */
  /* 增加减去的像素值以确保底部不被遮挡 (约 180px - 200px) */
  height: calc(100vh - 250px);
  overflow-y: auto;
  padding: 16px;
  font-family: 'JetBrains Mono', 'Fira Code', Consolas, monospace;
  font-size: 13px;
  line-height: 1.5;
  scroll-behavior: smooth;
}

/* Dark Theme */
.theme-dark {
  border-color: #4c4d4f;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.theme-dark .console-box {
  background: #1e1e1e;
  color: #d4d4d4;
}

.theme-dark .log-line {
  border-bottom: 1px solid #2d2d2d;
}

/* Light Theme */
.theme-light {
  border-color: #dcdfe6;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.theme-light .console-box {
  background: #ffffff;
  color: #303133;
}

.theme-light .log-line {
  border-bottom: 1px solid #ebeef5;
}

.log-line {
  padding: 2px 0;
  white-space: pre-wrap;
  word-break: break-all;
}

.log-line:last-child {
  border-bottom: none;
}
</style>