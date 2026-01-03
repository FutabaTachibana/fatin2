<script setup>
import {ref, onMounted} from 'vue'
import request from '../api/request'
import {ElMessage} from 'element-plus'
import ConfigForm from './ConfigForm.vue'

// 配置数据
const settingsConfig = ref([])
const loading = ref(false)

// 1. 获取当前配置
const fetchConfig = async () => {
  try {
    loading.value = true
    const res = await request.get('/settings')
    settingsConfig.value = res
  } catch (error) {
    ElMessage.error('加载配置失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 2. 保存配置
const saveConfig = async (formData) => {
  try {
    loading.value = true
    await request.post('/settings', formData)
    ElMessage.success('配置保存成功')
    // 重新加载以确认
    fetchConfig()
  } catch (error) {
    ElMessage.error('保存失败: ' + (error.message || '未知错误'))
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