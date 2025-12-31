<template>
  <div class="content-wrapper">
    <div class="content-header">
      <h1>概览面板</h1>
      <span class="subtitle">{{ currentDate }}</span>
    </div>

    <!-- 1. 顶部统计卡片 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon-wrapper bg-blue">
            <el-icon><Avatar /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">Bot Status</div>
            <div class="stat-value">{{ statusOnline ? 'Online' : 'Offline' }}</div>
            <div class="stat-sub">{{ botInfo.nickname }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon-wrapper bg-green">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">Uptime</div>
            <div class="stat-value text-small">{{ sysInfo.uptime }}</div>
            <div class="stat-sub">Since Startup</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon-wrapper bg-orange">
            <el-icon><Connection /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">Clients</div>
            <div class="stat-value">{{ wsClients.length }}</div>
            <div class="stat-sub">Active Connections</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon-wrapper bg-purple">
            <el-icon><Grid /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">Plugins</div>
            <div class="stat-value">{{ plugins.length }}</div>
            <div class="stat-sub">Loaded Modules</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 2. 中间层：硬件与软件信息 -->
    <el-row :gutter="20" class="mb-20">
      <!-- 硬件监控 -->
      <el-col :xs="24" :sm="24" :md="14" :lg="14">
        <el-card shadow="hover" class="dashboard-card hardware-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">Hardware Monitor</span>
              <el-tag size="small" effect="plain">{{ hwInfo.cpuModel }}</el-tag>
            </div>
          </template>
          <div class="hardware-content">
             <!-- CPU & Memory Circles -->
             <div class="monitor-item">
                <el-progress type="dashboard" :percentage="hwUsed.cpuLoad" :color="customColors" :width="140">
                   <template #default="{ percentage }">
                      <span class="percentage-value">{{ percentage }}%</span>
                      <span class="percentage-label">CPU Load</span>
                   </template>
                </el-progress>
                <div class="monitor-detail">
                   <span>{{ hwInfo.cpuCores }} Cores</span>
                </div>
             </div>
             <div class="monitor-item">
                <el-progress type="dashboard" :percentage="Math.round(hwUsed.memoryUsed / hwUsed.memoryTotal * 100)" :color="customColors" :width="140">
                   <template #default="{ percentage }">
                      <span class="percentage-value">{{ percentage }}%</span>
                      <span class="percentage-label">RAM Usage</span>
                   </template>
                </el-progress>
                <div class="monitor-detail">
                   <span>{{ hwUsed.memoryUsed }} / {{ hwUsed.memoryTotal }} MB</span>
                </div>
             </div>
          </div>
        </el-card>
      </el-col>

      <!-- 软件信息 -->
      <el-col :xs="24" :sm="24" :md="10" :lg="10">
        <el-card shadow="hover" class="dashboard-card software-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">System Info</span>
            </div>
          </template>
          <div class="info-list">
             <div class="info-row">
               <span class="label">System</span>
               <span class="value">{{ sysInfo.systemInfo }}</span>
             </div>
             <div class="info-row">
               <span class="label">Fatin2 Core</span>
               <div class="value-group">
                 <span class="value">{{ sysInfo.version }}</span>
                 <el-tag v-if="sysInfoHasUpdate.main" size="small" type="warning" effect="dark">Update</el-tag>
               </div>
             </div>
             <div class="info-row">
               <span class="label">WebUI</span>
               <div class="value-group">
                 <span class="value">{{ sysInfo.webUiVersion }}</span>
                 <el-tag v-if="sysInfoHasUpdate.webUi" size="small" type="warning" effect="dark">Update</el-tag>
               </div>
             </div>
             <div class="info-row">
               <span class="label">Config</span>
               <div class="value-group">
                 <span class="value">{{ sysInfo.configVersion }}</span>
                 <el-tag v-if="sysInfoHasUpdate.config" size="small" type="warning" effect="dark">Update</el-tag>
               </div>
             </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 3. 底部层：列表 -->
    <el-row :gutter="20">
      <!-- 客户端列表 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card shadow="hover" class="dashboard-card list-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">Connected Clients</span>
            </div>
          </template>
          <el-table :data="wsClients" style="width: 100%" size="small">
            <el-table-column prop="name" label="Client Name" />
            <el-table-column prop="ip" label="IP Address" width="120" />
            <el-table-column prop="status" label="Status" width="80" align="center">
              <template #default="scope">
                <div class="status-dot" :class="{ online: scope.row.status }"></div>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 插件列表 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card shadow="hover" class="dashboard-card list-card">
          <template #header>
            <div class="card-header">
              <span class="header-title">Plugins</span>
            </div>
          </template>
          <el-table :data="plugins" style="width: 100%" size="small" height="250">
            <el-table-column prop="name" label="Plugin" />
            <el-table-column prop="version" label="Ver" width="80" />
            <el-table-column prop="enabled" label="State" width="80" align="center">
               <template #default="scope">
                 <el-tag :type="scope.row.enabled ? 'success' : 'info'" size="small">
                   {{ scope.row.enabled ? 'Active' : 'Stopped' }}
                 </el-tag>
               </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import {Avatar, Timer, Connection, Grid} from '@element-plus/icons-vue'
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
const hwUsed = ref({cpuLoad: 15, memoryUsed: 256, memoryTotal: 1024})
const hwInfo = ref({
  cpuModel: 'Intel(R) Core(TM) i9-17900K CPU @ 10.0GHz',
  cpuCores: 8
})
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
.mb-20 {
  margin-bottom: 20px;
}

/* 统计卡片 */
.stat-card {
  display: flex;
  align-items: center;
  padding: 0;
  height: 100px;
  border: none;
  border-radius: 12px;
  overflow: hidden;
}

.stat-card :deep(.el-card__body) {
  padding: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
}

.stat-icon-wrapper {
  width: 80px;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 32px;
  color: white;
}

.bg-blue { background: linear-gradient(135deg, #36d1dc, #5b86e5); }
.bg-green { background: linear-gradient(135deg, #42e695, #3bb2b8); }
.bg-orange { background: linear-gradient(135deg, #ffc371, #ff5f6d); }
.bg-purple { background: linear-gradient(135deg, #c471ed, #f64f59); }

.stat-info {
  flex: 1;
  padding: 0 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-label {
  font-size: 12px;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-main);
  margin: 4px 0;
}

.stat-value.text-small {
  font-size: 20px;
}

.stat-sub {
  font-size: 12px;
  color: var(--text-secondary);
  opacity: 0.8;
}

/* 通用面板卡片 */
.dashboard-card {
  border-radius: 12px;
  border: 1px solid var(--border-color);
}

.dashboard-card :deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid var(--border-color);
}

.dashboard-card :deep(.el-card__body) {
  padding: 20px;
  display: block; /* Reset flex from stat card */
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-main);
}

/* 硬件监控 */
.hardware-content {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 10px 0;
}

.monitor-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.percentage-value {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: var(--text-main);
}

.percentage-label {
  display: block;
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 5px;
}

.monitor-detail {
  margin-top: 15px;
  font-size: 14px;
  color: var(--text-secondary);
  background: var(--bg-hover);
  padding: 4px 12px;
  border-radius: 12px;
}

/* 软件信息列表 */
.info-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px dashed var(--border-color);
}

.info-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.info-row .label {
  color: var(--text-secondary);
  font-size: 14px;
}

.info-row .value-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-row .value {
  font-weight: 500;
  color: var(--text-main);
}

/* 状态点 */
.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #f56c6c;
  margin: 0 auto;
}

.status-dot.online {
  background-color: #67c23a;
}
</style>