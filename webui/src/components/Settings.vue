<script setup>
import {ref, reactive, onMounted} from 'vue'
import request from '../api/request' // 假设你封装了 axios
import {ElMessage} from 'element-plus'

// 定义表单数据模型
const form = reactive({
  botQq: '',
  masterId: '',
  wsUrl: '',
  debugMode: false,
  autoReply: true
})

// 加载状态
const loading = ref(false)

// 1. 获取当前配置
const fetchConfig = async () => {
  try {
    loading.value = true
    // 发送 GET 请求获取后端配置
    const res = await request.get('/config')
    // 将后端返回的数据覆盖到 form 中
    Object.assign(form, res.data)
  } catch (error) {
    ElMessage.error('加载配置失败: ' + error.message)
  } finally {
    loading.value = false
  }
}

// 2. 保存配置
const saveConfig = async () => {
  try {
    loading.value = true
    // 发送 POST 请求更新配置
    await request.post('/config', form)
    ElMessage.success('配置保存成功，部分修改可能需要重启生效')
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
    <el-card class="settings-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>系统设置</span>
          <el-button type="primary" :loading="loading" @click="saveConfig">保存修改</el-button>
        </div>
      </template>

      <el-form :model="form" label-width="120px" v-loading="loading">

        <el-divider content-position="left">基础设置</el-divider>

        <el-form-item label="机器人 QQ 号">
          <el-input v-model="form.botQq" placeholder="请输入 Bot QQ"/>
        </el-form-item>

        <el-form-item label="管理员 ID">
          <el-input v-model="form.masterId" placeholder="请输入管理员 QQ"/>
        </el-form-item>

        <el-divider content-position="left">网络设置</el-divider>

        <el-form-item label="WebSocket URL">
          <el-input v-model="form.wsUrl" placeholder="ws://127.0.0.1:3001"/>
        </el-form-item>

        <el-divider content-position="left">功能开关</el-divider>

        <el-form-item label="调试模式">
          <el-switch v-model="form.debugMode" active-text="开启" inactive-text="关闭"/>
          <div class="form-tip">开启后将在控制台输出详细日志</div>
        </el-form-item>

        <el-form-item label="自动回复">
          <el-switch v-model="form.autoReply" active-text="启用" inactive-text="禁用"/>
        </el-form-item>

      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.settings-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-left: 10px;
  display: inline-block;
}
</style>