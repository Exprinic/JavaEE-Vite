// api/type/auth.js - 完整重构
import apiClient from '../client/apiClient.js'

export const authApi = {
    /**
     * 用户登录
     */
    login: (credentials) => {
        return apiClient.post('/auth/login', credentials)
    },

    /**
     * 用户登出
     */
    logout: (credentials) => {
        return apiClient.post('/auth/logout', credentials, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('access_token')}`
            }
        })
    },

    /**
     * 用户注册
     */
    register: (credentials) => {
        return apiClient.post('/auth/register', credentials)
    },

    /**
     * 获取验证码
     */
    fetchCaptcha: (credentials) => {
        return apiClient.post('/auth/captcha', credentials)
    },

    /**
     * 刷新访问令牌
     */
    refreshToken: (refreshData) => {
        return apiClient.post('/auth/refresh', refreshData)
    }
}
