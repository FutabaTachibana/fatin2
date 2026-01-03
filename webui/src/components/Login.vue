<script setup>
import { ref } from 'vue'
import request from "../api/request.js";
import { ElMessage } from 'element-plus'

const emit = defineEmits(['login-success'])

const token = ref('');
const loading = ref(false);

const login = async () => {
  if (!token.value) {
    ElMessage.warning('请输入 Token')
    return
  }

  loading.value = true
  try {
    const response = await request.post('/login', {
      token: token.value
    });
    // 保存 Token
    localStorage.setItem('fatin_token', response.token); // 注意：后端返回的是 { token: "..." }，request.js 可能会直接返回 data
    ElMessage.success('登录成功')
    emit('login-success')
  } catch (error) {
    console.error('登录失败', error);
    ElMessage.error('登录失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
};
</script>

<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>Fatin2 Admin Login</h2>
        </div>
      </template>
      <div class="form-item">
        <el-input v-model="token" type="password" placeholder="请输入 WebUI Token" show-password @keyup.enter="login" />
      </div>
      <div class="form-item">
        <el-button type="primary" :loading="loading" @click="login" style="width: 100%">登录</el-button>
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: var(--bg-app, #f0f2f5);
}

.login-card {
  width: 400px;
}

.card-header {
  text-align: center;
}

.form-item {
  margin-bottom: 20px;
}
</style>
