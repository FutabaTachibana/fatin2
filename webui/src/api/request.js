import axios from 'axios';

const service = axios.create({
    baseURL: 'http://localhost:8080/api', // 指向 Javalin 后端
    timeout: 5000
});

// 请求拦截器：自动附加 Token
service.interceptors.request.use(
    config => {
        const token = localStorage.getItem('fatin_token');
        if (token) {
            config.headers['Authorization'] = 'Bearer ' + token;
        }
        return config;
    },
    error => Promise.reject(error)
);

export default service;