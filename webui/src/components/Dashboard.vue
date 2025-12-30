<template>
  <div class="content-wrapper">
    <div class="content-header">
      <h1>概览面板</h1>
      <span class="subtitle">{{ currentDate }}</span>
    </div>

    <!-- 磁贴布局 -->
    <el-row :gutter="20">

      <!-- 左侧列 -->
      <el-col :xs="24" :sm="24" :md="8" :lg="10">
        <!-- 1. 机器人基础信息卡片 (宽卡片) -->
        <el-card shadow="hover" class="common-card bot-card mb-20">
          <div class="card-header">
            <span class="label">Bot Profile</span>
            <el-tag :type="statusOnline ? 'success' : 'danger'" size="small">
              {{ statusOnline ? 'Online' : 'Offline' }}
            </el-tag>
          </div>
          <div class="card-content">
            <div class="big-text">{{ botInfo.nickname }}</div>
            <div class="sub-text">账号: {{ botInfo.qq }}</div>
          </div>
        </el-card>

        <!-- 4. 软件版本信息 (列表式) -->
        <el-card shadow="hover" class="common-card high-card list-card mb-20">
          <div class="card-header"><span class="label">Software Info</span></div>
          <div class="detail-list">
            <div class="detail-item">
              <span>Fatin2 版本</span>
              <div class="item-right">
                <span class="val">{{ sysInfo.version }}</span>
                <el-popover v-if="sysInfoHasUpdate.main"
                            title="有可用版本更新"
                            :content="sysInfo.version + ' 已经不是最新版本'"
                            placement="top"
                >
                  <template #reference>
                    <el-icon>
                      <InfoFilled/>
                    </el-icon>
                  </template>
                </el-popover>
              </div>
            </div>
            <div class="detail-item">
              <span>配置版本</span>
              <div class="item-right">
                <span class="val">{{ sysInfo.configVersion }}</span>
                <el-popover v-if="sysInfoHasUpdate.config"
                            content="有可用版本更新"
                            :content="sysInfo.configVersion + ' 已经不是最新版本'"
                            placement="top"
                            effect="dark"
                >
                  <template #reference>
                    <el-icon>
                      <InfoFilled/>
                    </el-icon>
                  </template>
                </el-popover>
              </div>
            </div>
            <div class="detail-item">
              <span>WebUI 版本</span>
              <div class="item-right">
                <span class="val">{{ sysInfo.webUiVersion }}</span>
                <el-popover v-if="sysInfoHasUpdate.webUi"
                            content="有可用版本更新"
                            :content="sysInfo.webUiVersion + ' 已经不是最新版本'"
                            placement="top"
                            effect="dark"
                >
                  <template #reference>
                    <el-icon>
                      <InfoFilled/>
                    </el-icon>
                  </template>
                </el-popover>
              </div>
            </div>
            <div class="detail-item">
              <span>系统版本</span>
              <div class="item-right">
                <span class="val">{{ sysInfo.systemInfo }}</span>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 6. 客户端列表 (宽) -->
        <el-card shadow="hover" class="common-card scroll-card mb-20">
          <div class="card-header"><span class="label">Connected Clients</span></div>
          <el-table :data="wsClients" size="small" style="width: 100%" :show-header="false">
            <el-table-column prop="ip" label="IP"/>
            <el-table-column prop="name" label="name" align="center"/>
            <el-table-column prop="status" label="status" align="right">
              <template #default="scope">
                <el-tag :type="scope.row.status ? 'success' : 'danger'" size="small">
                  {{ scope.row.status ? 'Online' : 'Offline' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧列 -->
      <el-col :xs="24" :sm="24" :md="16" :lg="14">
        <el-row :gutter="20" class="mb-20">
          <!-- 2. 连接数 (方块) -->
          <el-col :span="12">
            <el-card shadow="hover" class="common-card">
              <div class="card-header"><span class="label">Clients</span></div>
              <div class="center-content">
                <span class="number">{{ wsClients.length }}</span>
                <span class="unit">Active</span>
              </div>
            </el-card>
          </el-col>

          <!-- 3. 运行时间 (方块) -->
          <el-col :span="12">
            <el-card shadow="hover" class="common-card">
              <div class="card-header"><span class="label">Uptime</span></div>
              <div class="center-content">
                <span class="mid-text">{{ sysInfo.uptime }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 7. 插件信息 -->
        <el-card shadow="hover" class="common-card scroll-card mb-20">
          <div class="card-header"><span class="label">Plugins</span></div>
          <div>
            <el-row :gutter="10">
              <el-col :xs="24" :sm="24" :md="8" :lg="8">
                <div class="center-content">
                  <span class="number">{{ plugins.length }}</span>
                  <span class="unit">Active</span>
                </div>
              </el-col>
              <el-col :xs="24" :sm="24" :md="16" :lg="16">
                <div>
                  <el-table :data="plugins" size="small" height="95" stripe style="width: 100%" :show-header="false">
                    <el-table-column prop="name" label="Plugin Name" width="150" show-overflow-tooltip/>
                    <el-table-column prop="version" label="Version"/>
                    <el-table-column prop="enabled" label="Status" align="right">
                      <template #default="scope">
                        <el-tag :type="scope.row.enabled ? 'success' : 'danger'" size="small">
                          {{ scope.row.enabled ? 'Enable' : 'Disable' }}
                        </el-tag>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </el-col>
             </el-row>
          </div>
        </el-card>

        <!-- 5. 硬件信息 (双排) -->
        <el-card shadow="hover" class="common-card higher-card hardware-card mb-20">
          <div class="card-header"><span class="label">Hardware</span></div>
          <el-row :gutter="20">
            <el-col :xs="24" :sm="24" :md="12" :lg="12">
              <el-progress class="center-content" :type="'circle'" :percentage="hwInfo.cpuLoad" :color="customColors"/>
              <span class="center-content">CPU Load</span>
            </el-col>
            <el-col :xs="24" :sm="24" :md="12" :lg="12">
              <el-progress class="center-content" :type="'circle'" :percentage="hwInfo.memoryPercent" :color="customColors"/>
              <span class="center-content">Memory Usage</span>
            </el-col>
          </el-row>
        </el-card>

      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import {InfoFilled} from '@element-plus/icons-vue'
import {samplePlugins} from '../api/debugData.js'

// 模拟数据 (后端对接时替换)
const currentDate = new Date().toLocaleDateString()
const statusOnline = ref(true)
const botInfo = ref({nickname: 'FatinBot', qq: '123456789'})
const sysInfo = ref({
  version: 'v2.1.0',
  configVersion: '20250101',
  webUiVersion: 'v1.0.0',
  systemInfo: 'Lindows 26H4 Tahoe',
  uptime: '3d 2h 15m'
})
const sysInfoHasUpdate = ref({main: true, config: false, webUi: false})
const hwInfo = ref({cpuLoad: 15, memoryUsed: 256, memoryTotal: 1024, memoryPercent: 25})
const wsClients = ref([
  {ip: '127.0.0.1', name: 'go-cqhttp/v1.0', status: true},
  {ip: '192.168.1.5', name: 'Onebot-Helper', status: true}
])
const plugins = samplePlugins

const customColors = [
  {color: '#5cb87a', percentage: 60},
  {color: '#e6a23c', percentage: 80},
  {color: '#f56c6c', percentage: 100},
]
</script>

<style scoped>

/* 卡片样式 */
.common-card {
  height: 160px;
  border: 1px solid var(--border-color);
  background-color: var(--bg-card);
  border-radius: 8px;
}

.mb-20 {
  margin-bottom: 20px;
}

.high-card {
  height: 260px;
}

.higher-card {
  height: 340px;
}

:deep(.el-card__body) {
  padding: 20px;
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.label {
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--text-secondary);
  font-weight: 600;
}

/* 字体排版 */
.big-text {
  font-size: 28px;
  font-weight: 600;
  color: var(--text-main);
  margin-top: auto;
}

.sub-text {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: auto;
}

.center-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.number {
  font-size: 48px;
  font-weight: 300;
  color: var(--accent-color);
  line-height: 1;
}

.unit {
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 5px;
}

.mid-text {
  font-size: 24px;
  font-weight: 500;
  color: var(--text-main);
}

/* 列表样式 */
.detail-list {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  border-bottom: 1px dashed var(--border-color);
  padding-top: 20px;
}

.detail-item:first-child {
  padding-top: 0;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-item .val {
  font-family: monospace;
  color: var(--text-secondary);
}

.detail-item .item-right {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 硬件卡片调整 */
.hw-row {
  margin-top: 6px;
}

.hw-label {
  font-size: 12px;
  color: var(--text-secondary);
  display: block;
  margin-bottom: 4px;
}

/* 滚动列表特殊处理 */
.scroll-card {
  height: auto;
  min-height: 160px;
}
</style>