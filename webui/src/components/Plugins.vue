<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../api/request'
import { samplePlugins } from '../api/debugData.js' // 用于调试的假数据

// 插件数据列表
const plugins = ref([])
const loading = ref(false)

// 详情弹窗控制
const dialogVisible = ref(false)
const currentPlugin = ref({}) // 当前选中的插件
const configForm = ref({})   // 动态表单绑定的数据
const formSchema = ref([])   // 表单结构定义

// 1. 获取插件列表
const fetchPlugins = async () => {
  loading.value = true
  try {
    const res = await request.get('/plugins')
    plugins.value = res.data
  } catch (e) {
    ElMessage.error('无法加载插件列表')
  } finally {
    loading.value = false
  }
}

// 2. 切换插件开关
const togglePlugin = async (plugin) => {
  if (!plugin.canHotReload) return

  const newState = !plugin.enabled
  try {
    await request.post(`/plugins/${plugin.id}/toggle`, { enabled: newState })
    plugin.enabled = newState
    ElMessage.success(`${plugin.name} 已${newState ? '启用' : '禁用'}`)
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

// 3. 打开详情与配置
const openDetails = async (plugin) => {
  currentPlugin.value = plugin
  dialogVisible.value = true

  // 如果插件有配置，拉取配置结构和当前值
  if (plugin.hasConfig) {
    try {
      const res = await request.get(`/plugins/${plugin.id}/config`)
      formSchema.value = res.data.schema // 表单定义（字段名、类型、标签）
      configForm.value = res.data.values // 当前值
    } catch (e) {
      ElMessage.warning('无法加载插件配置')
      formSchema.value = []
    }
  } else {
    formSchema.value = []
  }
}

// 4. 保存配置
const saveConfig = async () => {
  try {
    await request.post(`/plugins/${currentPlugin.value.id}/config`, configForm.value)
    ElMessage.success('配置已保存')
    dialogVisible.value = false
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

// onMounted(fetchPlugins)
</script>

<template>
  <div class="content-wrapper">
    <h2 class="page-title">插件管理</h2>

    <div class="plugin-grid" v-loading="loading">
      <!-- 插件磁贴 -->
      <!--调试模式下使用 samplePlugins-->
      <div v-for="p in samplePlugins" :key="p.id" class="plugin-card">
        <div class="card-header">
          <div class="meta">
            <div class="name">{{ p.name }}</div>
            <div class="author">by {{ p.author }}</div>
          </div>
          <!-- 开关按钮 -->
          <el-switch
              v-model="p.enabled"
              :disabled="!p.canHotReload"
              @change="() => togglePlugin(p)"
              style="--el-switch-on-color: #007bff;"
          />
        </div>

        <div class="desc">{{ p.description }}</div>

        <!-- 底部详情按钮 -->
        <div class="card-footer">
          <button class="detail-btn" @click="openDetails(p)">
            查看详情 & 配置
          </button>
        </div>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <el-dialog
        v-model="dialogVisible"
        :title="currentPlugin.name"
        width="600px"
        align-center
    >
      <div class="plugin-detail-info">
        <p><strong>作者：</strong> {{ currentPlugin.author }}</p>
        <p><strong>版本：</strong> {{ currentPlugin.version }}</p>
        <p><strong>描述：</strong> {{ currentPlugin.description }}</p>
      </div>

      <el-divider v-if="formSchema.length > 0" content-position="left">插件配置</el-divider>

      <!-- 动态表单渲染区 -->
      <el-form
          v-if="formSchema.length > 0"
          label-position="top"
          class="dynamic-form"
      >
        <el-form-item
            v-for="field in formSchema"
            :key="field.key"
            :label="field.label"
        >
          <!-- 根据 type 渲染不同组件 -->
          <el-input
              v-if="field.type === 'string'"
              v-model="configForm[field.key]"
          />
          <el-switch
              v-if="field.type === 'boolean'"
              v-model="configForm[field.key]"
          />
          <el-input-number
              v-if="field.type === 'number'"
              v-model="configForm[field.key]"
          />
          <el-select
              v-if="field.type === 'select'"
              v-model="configForm[field.key]"
          >
            <el-option
                v-for="opt in field.options"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div v-else-if="currentPlugin.hasConfig" class="no-config">
        配置加载中或格式错误
      </div>
      <div v-else class="no-config">
        该插件无需配置
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button
              v-if="formSchema.length > 0"
              type="primary"
              @click="saveConfig"
              color="#007bff"
          >
            保存修改
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-title { margin-bottom: 30px; font-weight: 300; }

/* 网格布局: 响应式 */
.plugin-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

/* 磁贴样式 */
.plugin-card {
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  padding: 20px;
  height: 200px; /* 固定高度，符合 2:1 比例的大概 */
  display: flex;
  flex-direction: column;
  transition: all 0.2s;
}

.plugin-card:hover {
  border-color: #007bff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.name { font-size: 1.1rem; font-weight: 600; color: var(--el-text-color-primary); }
.author { font-size: 0.85rem; color: var(--el-text-color-secondary); margin-top: 4px; }
.desc {
  flex-grow: 1;
  font-size: 0.9rem;
  color: var(--el-text-color-regular);
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 底部按钮 */
.card-footer {
  text-align: center;
  margin-top: 15px;
}

.detail-btn {
  background: none;
  border: none;
  color: #007bff;
  cursor: pointer;
  font-size: 0.9rem;
  padding: 5px 15px;
}
.detail-btn:hover { text-decoration: underline; }

/* 弹窗样式 */
.plugin-detail-info p { margin: 8px 0; }
.no-config { color: #999; font-style: italic; margin-top: 20px; }
</style>