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

// 响应拦截器
service.interceptors.response.use(
    response => {
        return response.data;
    },
    error => {
        if (error.response && error.response.status === 401) {
            // Token 失效或未登录
            localStorage.removeItem('fatin_token');
            // 如果当前不是在登录页，则刷新页面以触发 App.vue 的登录检查
            // 或者可以使用事件总线通知 App.vue
            if (!window.location.pathname.includes('login')) {
                 // 简单处理：刷新页面��App.vue 会重新检查 token
                 // 注意：这会导致页面刷新，用户体验可能稍差，但在没有路由的情况下是简单有效的
                 // 更好的方式是抛出特定错误，让调用者处理，或者使用全局状态管理
                 // 这里我们选择抛出错误，并在 App.vue 中监听全局未捕获的 Promise 错误或者依赖组件内的错误处理
                 // 但为了确保一定会回到登录页，我们可以直接刷新
                 // location.reload();
                 // 为了避免无限刷新循环（如果登录页本身也报401），这里先不自���刷新，
                 // 而是依赖 App.vue 的逻辑。
                 // 但是 App.vue 只在 mounted 时检查。
                 // 所以我们需要一种机制通知 App.vue。
                 // 由于没有 Vuex/Pinia，我们可以使用自定义事件。
                 window.dispatchEvent(new Event('auth-logout'));
            }
        }
        return Promise.reject(error);
    }
);

export default service;