<script setup>
import {ref, onMounted} from 'vue'
// import request from '../api/request'
import {ElMessage} from 'element-plus'
import ConfigForm from './ConfigForm.vue'

// 配置数据
const settingsConfig = ref([])
const loading = ref(false)

// 1. 获取当前配置
const fetchConfig = async () => {
  try {
    loading.value = true

    // --- 调试模式：生成假数据 (生产环境请注释掉此块) ---
    // --- Debug Mode: Generate fake data (Comment out in production) ---
    await new Promise(resolve => setTimeout(resolve, 500)) // 模拟延迟
    settingsConfig.value = [
      {
        key: "bot_name",
        label: "Bot Name",
        type: "string",
        value: "FatinBot",
        description: "The name displayed in logs and messages"
      },
      {
        key: "master_id",
        label: "Master ID",
        type: "string",
        value: "123456789",
        description: "The QQ number of the bot administrator"
      },
      {
        key: "ws_url",
        label: "WebSocket URL",
        type: "string",
        value: "ws://127.0.0.1:3001",
        description: "Connection URL for the backend"
      },
      {
        key: "max_retries",
        label: "Max Retries",
        type: "number",
        value: 5,
        description: "Maximum number of connection retry attempts"
      },
      {
        key: "debug_mode",
        label: "Debug Mode",
        type: "boolean",
        value: true,
        description: "Enable verbose logging for debugging"
      },
      {
        key: "log_level",
        label: "Log Level",
        type: "select",
        options: ["INFO", "WARN", "ERROR", "DEBUG"],
        value: "INFO",
        description: "Minimum log level to record"
      }
    ]
    // -----------------------------------------------------

    // --- 生产环境：API 请求 (生产环境请取消注释并替换上面的调试代码) ---
    // --- Production: API Request (Uncomment and replace debug code above) ---
    /*
    const res = await request.get('/api/settings')
    settingsConfig.value = res.data
    */

  } catch (error) {
    ElMessage.error('加载配置失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 2. 保存配置
const saveConfig = async (formData) => {
  try {
    loading.value = true

    // --- 调试模式：模拟保存 (生产环境请注释掉此块) ---
    // --- Debug Mode: Mock save (Comment out in production) ---
    await new Promise(resolve => setTimeout(resolve, 800))
    console.log('Saving config:', formData)
    ElMessage.success('配置保存成功 (模拟)')
    // -----------------------------------------------------

    // --- 生产环境：API 请求 (生产环境请取消注释并替换上面的调试代码) ---
    // --- Production: API Request (Uncomment and replace debug code above) ---
    /*
    await request.post('/api/settings', formData)
    ElMessage.success('配置保存成功')
    // 重新加载以确认
    fetchConfig()
    */

  } catch (error) {
    ElMessage.error('保存失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 页面加载时自动获取配置
onMounted(() => {
  fetchConfig()
})
</script>

<template>
  <div class="content-wrapper">
    <div class="content-header">
      <h1>系统设置</h1>
    </div>

    <el-card class="settings-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>全局配置</span>
        </div>
      </template>

      <ConfigForm
        :config="settingsConfig"
        :loading="loading"
        @save="saveConfig"
      />
    </el-card>
  </div>
</template>

<style scoped>
.content-header {
  margin-bottom: 20px;
}

.settings-card {
  border-radius: 12px;
  border: 1px solid var(--border-color, #dcdfe6);
}

.card-header {
  font-weight: 600;
}
</style>