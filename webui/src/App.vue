<template>
  <!-- 整个容器 -->
  <el-container class="layout-container">

    <!-- 侧边栏 -->
    <el-aside :width="isCollapsed ? '64px' : 'var(--sidebar-width)'" class="app-sidebar">

      <!-- 1. 顶部：Logo / 标题区 -->
      <div class="sidebar-header" v-if="!isCollapsed">
        <h2 class="app-title">Fatin2 Admin</h2>
      </div>
      <div class="sidebar-header-collapsed" v-else>
        <h3>F2</h3>
      </div>

      <!-- 2. 中部：导航菜单 -->
      <el-menu
          :default-active="currentView"
          class="custom-menu"
          :collapse="isCollapsed"
          @select="(index) => currentView = index"
      >
        <el-menu-item index="Dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>概览面板</template>
        </el-menu-item>

        <el-menu-item index="Console">
          <el-icon><Monitor /></el-icon>
          <template #title>实时日志</template>
        </el-menu-item>

        <el-menu-item index="Settings">
          <el-icon><Setting /></el-icon>
          <template #title>系统配置</template>
        </el-menu-item>

        <el-menu-item index="Plugins">
          <el-icon><Grid /></el-icon>
          <template #title>插件管理</template>
        </el-menu-item>
      </el-menu>

      <!-- 3. 底部：操作区 (深色模式/退出) -->
      <div class="sidebar-footer">
        <!-- 切换侧边栏按钮 (仅在桌面显示) -->
        <div class="footer-item toggle-btn" @click="toggleSidebar">
          <el-icon v-if="isCollapsed"><Expand /></el-icon>
          <el-icon v-else><Fold /></el-icon>
        </div>

        <div class="footer-actions">
          <!-- 深色模式切换 -->
          <el-tooltip content="切换主题 (WIP)" placement="right" :disabled="!isCollapsed">
            <div class="footer-item" @click="toggleTheme">
              <el-icon><Moon /></el-icon>
              <span v-if="!isCollapsed">深色模式</span>
            </div>
          </el-tooltip>

          <!-- 退出登录 -->
          <el-tooltip content="退出登录" placement="right" :disabled="!isCollapsed">
            <div class="footer-item logout" @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              <span v-if="!isCollapsed">退出</span>
            </div>
          </el-tooltip>
        </div>
      </div>
    </el-aside>

    <!-- 右侧内容区 -->
    <el-main class="app-main">
      <component :is="views[currentView]" />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Odometer, Monitor, Setting, Grid, Moon, SwitchButton, Fold, Expand } from '@element-plus/icons-vue'

// 导入组件
import Dashboard from './components/Dashboard.vue'
import Console from './components/Console.vue'
import Settings from './components/Settings.vue'
import Plugins from "./components/Plugins.vue";

const views = { Dashboard, Console, Settings, Plugins }
const currentView = ref('Dashboard')
const isCollapsed = ref(false)
const isDarkMode = ref(false)

// 响应式逻辑：窗口小于 1000px 自动收起
const checkScreenSize = () => {
  if (window.innerWidth < 1000) {
    isCollapsed.value = true
  } else {
    isCollapsed.value = false
  }
}

// 切换主题 (预留逻辑)
const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value
  if (isDarkMode.value) {
    document.documentElement.classList.add('dark')
  } else {
    document.documentElement.classList.remove('dark')
  }
}

const toggleSidebar = () => isCollapsed.value = !isCollapsed.value

const handleLogout = () => {
  localStorage.removeItem('fatin_token')
  location.reload() // 简单粗暴刷新，触发登录拦截
}

onMounted(() => {
  checkScreenSize()
  window.addEventListener('resize', checkScreenSize)
})

onUnmounted(() => {
  window.removeEventListener('resize', checkScreenSize)
})
</script>

<style scoped>
/* 布局容器 */
.layout-container {
  height: 100vh;
  background-color: var(--bg-app);
}

/* --- 侧边栏样式 --- */
.app-sidebar {
  background-color: var(--bg-sidebar);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  overflow: hidden;
}

.sidebar-header {
  height: 60px;
  display: flex;
  align-items: center;
  padding-left: 20px;
}
.sidebar-header-collapsed {
  height: 60px;
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center;    /* 垂直居中 */
}

.app-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-main);
}

/* 覆盖 Element Menu 样式以符合你的设计 */
.custom-menu {
  border-right: none;
  background-color: transparent;
  flex: 1; /* 占据中间剩余空间 */
}

:deep(.el-menu-item) {
  height: 50px;
  margin: 4px 8px; /* 留一点边距 */
  border-radius: 6px;
  color: var(--text-secondary);
}

:deep(.el-menu-item:hover) {
  background-color: var(--bg-hover);
  color: var(--text-main);
}

/* 选中态：深色叠加层 + 左侧蓝线 */
:deep(.el-menu-item.is-active) {
  background-color: var(--bg-active);
  color: var(--text-main);
  position: relative;
}

/* 左侧蓝色竖线实现 */
:deep(.el-menu-item.is-active)::before {
  content: "";
  position: absolute;
  left: -8px; /* 根据 margin 调整位置 */
  top: 10px;
  bottom: 10px;
  width: 4px;
  background-color: var(--accent-color);
  border-top-right-radius: 4px;
  border-bottom-right-radius: 4px;
}

/* --- 底部按钮区 --- */
.sidebar-footer {
  padding: 10px;
  border-top: 1px solid var(--border-color);
}

.footer-item {
  display: flex;
  align-items: center;
  padding: 10px;
  cursor: pointer;
  border-radius: 6px;
  color: var(--text-secondary);
  transition: background 0.2s;
  font-size: 14px;
  gap: 10px;
  overflow: hidden;
  white-space: nowrap;
}

.footer-item:hover {
  background-color: var(--bg-hover);
  color: var(--text-main);
}

.toggle-btn {
  justify-content: center;
  margin-bottom: 10px;
}

/* 右侧主内容 */
.app-main {
  padding: 20px;
  background-color: var(--bg-app);
  color: var(--text-main);
}
</style>