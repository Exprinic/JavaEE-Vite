import apiClient from '../client/apiClient.js';


export const authApi = {
    login: (credentials) => {
        // 如果使用rewrite，假设前端接口为 /api/login，则后端请求应为 /login
        // 代理配置会将 /api/login 转换为 http://localhost:8080/login
        return apiClient.post('/auth/login', credentials);
    }
    ,
    logOut: (credentials) => {
        return apiClient.post('/auth/logout', credentials);
    },
    register: (credentials) => {
        return apiClient.post('/auth/register', credentials);
    },
    fetchCaptcha: (credentials) => {
        return apiClient.post('/auth/captcha', credentials);
    }
}