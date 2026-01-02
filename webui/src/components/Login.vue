<script setup>
import { ref } from 'vue'
import request from "../api/request.js";

const username = ref('');
const password = ref('');

const login = async () => {
  try {
    const response = await request.post('/login', {
      username: username.value,
      password: password.value
    });
    // 保存 Token
    localStorage.setItem('token', response.data.token);
    alert('登录成功')
  } catch (error) {
    console.error('登录失败', error);
    alert('登陆失败')
  }
};
</script>

<template>
  <input v-model="username" placeholder="用户名" />
  <input v-model="password" type="password" placeholder="密码" />
  <button @click="login">登录</button>
</template>
