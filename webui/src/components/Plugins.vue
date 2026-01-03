<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../api/request'
import ConfigForm from './ConfigForm.vue'

// 插件数据列表
const plugins = ref([])
const loading = ref(false)

// 详情弹窗控制
const dialogVisible = ref(false)
const currentPlugin = ref({}) // 当前选中的插件
const pluginConfig = ref([])   // 传递给 ConfigForm 的配置结构
const configLoading = ref(false)

// 1. 获取插件列表
const fetchPlugins = async () => {
  loading.value = true
  try {
    const res = await request.get('/plugins')
    // 适配后端数据结构
    plugins.value = res.map(p => ({
      id: p.name,
      name: p.displayName || p.name,
      author: p.author,
      version: p.version,
      description: p.description,
      enabled: p.enabled,
      canHotReload: p.canHotReload,
      hasConfig: p.hasConfig
    }))
  } catch (e) {
    ElMessage.error('无法加载插件列表: ' + (e.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 2. 切换插件开关
const togglePlugin = async (plugin) => {
  if (!plugin.canHotReload) return

  const newState = !plugin.enabled
  // 乐观更新 UI
  const oldState = plugin.enabled
  plugin.enabled = newState

  try {
    await request.post(`/plugins/${plugin.id}/toggle`, { enabled: newState })
    ElMessage.success(`${plugin.name} 已${newState ? '启用' : '禁用'}`)
  } catch (e) {
    // 回滚状态
    plugin.enabled = oldState
    ElMessage.error('操作失败: ' + (e.message || '未知错误'))
  }
}

// 3. 打开详情与配置
const openDetails = async (plugin) => {
  currentPlugin.value = plugin
  dialogVisible.value = true
  pluginConfig.value = [] // 重置配置

  if (plugin.hasConfig) {
    configLoading.value = true
    try {
      const res = await request.get(`/plugins/${plugin.id}/config`)
      pluginConfig.value = res
    } catch (e) {
      ElMessage.warning('无法加载插件配置: ' + (e.message || '未知错误'))
    } finally {
      configLoading.value = false
    }
  }
}

// 4. 保存配置
const saveConfig = async (formData) => {
  try {
    configLoading.value = true
    await request.post(`/plugins/${currentPlugin.value.id}/config`, formData)
    ElMessage.success('配置已保存')
    dialogVisible.value = false
  } catch (e) {
    ElMessage.error('保存失败: ' + (e.message || '未知错误'))
  } finally {
    configLoading.value = false
  }
}

onMounted(fetchPlugins)
</script>

<template>
  <div class="content-wrapper">
    <div class="content-header">
      <h1>插件管理</h1>
    </div>

    <div class="plugin-grid" v-loading="loading">
      <!-- 插件卡片 -->
      <el-card
        v-for="p in plugins"
        :key="p.id"
        class="plugin-card"
        shadow="hover"
        :body-style="{ padding: '20px', height: '100%', display: 'flex', flexDirection: 'column' }"
      >
        <div class="card-header-row">
          <div class="meta">
            <div class="name">{{ p.name }}</div>
            <div class="author">by {{ p.author }}</div>
          </div>
          <!-- 开关按钮 -->
          <el-switch
              v-model="p.enabled"
              :disabled="!p.canHotReload"
              @change="() => togglePlugin(p)"
              inline-prompt
              active-text="ON"
              inactive-text="OFF"
          />
        </div>

        <div class="desc">{{ p.description }}</div>

        <div class="card-footer">
          <el-tag size="small" type="info" class="version-tag">v{{ p.version }}</el-tag>
          <el-button link type="primary" @click="openDetails(p)">
            配置 & 详情
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog
        v-model="dialogVisible"
        width="600px"
        align-center
        destroy-on-close
        class="plugin-dialog"
    >
      <div class="plugin-dialog-content">
        <div class="plugin-title-large">{{ currentPlugin.name }}</div>

        <el-divider class="info-divider" />

        <div class="plugin-detail-info">
          <div class="info-row">
            <span class="label">ID:</span>
            <span class="value">{{ currentPlugin.id }}</span>
          </div>
          <div class="info-row">
            <span class="label">作者:</span>
            <span class="value">{{ currentPlugin.author }}</span>
          </div>
          <div class="info-row">
            <span class="label">版本:</span>
            <span class="value">{{ currentPlugin.version }}</span>
          </div>
          <div class="info-row desc-row">
            <span class="label">描述:</span>
            <span class="value">{{ currentPlugin.description }}</span>
          </div>
        </div>

        <el-divider v-if="currentPlugin.hasConfig" content-position="left" class="config-divider">插件配置</el-divider>

        <div v-if="currentPlugin.hasConfig">
          <ConfigForm
            :config="pluginConfig"
            :loading="configLoading"
            @save="saveConfig"
          />
        </div>
        <div v-else class="no-config">
          <el-empty description="该插件无需配置" :image-size="60" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.content-header {
  margin-bottom: 20px;
}

.content-header h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: var(--text-main);
}

/* 网格布局: 响应式 */
.plugin-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

/* 磁贴样式 */
.plugin-card {
  height: 220px;
  border-radius: 12px;
  border: 1px solid var(--border-color);
}

.card-header-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.name {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-main);
}

.author {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.desc {
  flex-grow: 1;
  font-size: 14px;
  color: var(--text-regular);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 15px;
}

/* 底部按钮 */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 15px;
  border-top: 1px dashed var(--border-light);
}

.version-tag {
  font-family: monospace;
}

/* 弹窗样式 */
:deep(.plugin-dialog .el-dialog__header) {
  padding-bottom: 0;
  margin-right: 0; /* 避免关闭按钮影响布局 */
}

:deep(.plugin-dialog .el-dialog__body) {
  padding: 20px 40px 40px 40px; /* 增加内边距 */
}

.plugin-title-large {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-main);
  margin-bottom: 15px;
}

.info-divider {
  margin: 15px 0 20px 0;
}

.plugin-detail-info {
  font-weight: 300; /* 细字体 */
  color: var(--text-regular);
  font-size: 14px;
}

.info-row {
  display: flex;
  margin-bottom: 8px;
  line-height: 1.6;
}

.info-row .label {
  width: 50px;
  flex-shrink: 0;
  color: var(--text-secondary);
  font-weight: normal;
}

.info-row .value {
  color: var(--text-main);
}

.desc-row {
  margin-top: 12px;
}

.config-divider {
  margin-top: 30px;
  margin-bottom: 30px;
}

.config-divider :deep(.el-divider__text) {
  font-weight: normal;
  color: var(--text-secondary);
}

.no-config {
  padding: 20px 0;
}
</style>