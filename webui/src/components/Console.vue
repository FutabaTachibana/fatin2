<template>
  <div class="content-wrapper">
    <div class="console-box" ref="consoleRef">
      <div v-for="(log, index) in logs" :key="index" class="log-line">
        {{ log }}
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'

const logs = ref([])
const consoleRef = ref(null)

onMounted(() => {
  // 注意：开发环境可能需要写死 localhost:7070
  // 生产环境直接用 location.host
  const ws = new WebSocket(`ws://${location.host}/ws/logs`)

  ws.onmessage = (event) => {
    logs.value.push(event.data)
    // 自动滚动到底部
    if (logs.value.length > 500) logs.value.shift() // 限制行数
    setTimeout(() => {
      if (consoleRef.value) consoleRef.value.scrollTop = consoleRef.value.scrollHeight
    }, 0)
  }
})
</script>

<style scoped>
.console-box {
  background: #1e1e1e;
  color: #fff;
  height: 80vh;
  overflow-y: auto;
  padding: 10px;
  font-family: monospace;
}

.log-line {
  margin-bottom: 4px;
  border-bottom: 1px solid #333;
}
</style>