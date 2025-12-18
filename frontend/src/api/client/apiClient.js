
// api/client/apiClient.js - 增强版
import axios from 'axios'
import { useAuthStore } from '../../stores/authStore.js'
import { useNotificationStore } from '../../stores/notificationStore.js'

// 1. 创建 axios 实例
const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/backend/api',
    withCredentials: true,
    timeout: 10000, // 10秒超时
    headers: {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
    }
})

// 2. 请求拦截器
apiClient.interceptors.request.use(
    (config) => {
        // 添加认证令牌
        const token = localStorage.getItem('access_token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }

        // 添加请求ID用于追踪
        config.headers['X-Request-ID'] = generateRequestId()

        // 记录请求日志（仅开发环境）
        if (import.meta.env.DEV) {
            console.log('📤 发送请求:', {
                method: config.method,
                url: config.url,
                data: config.data,
                headers: config.headers
            })
        }

        return config
    },
    (error) => {
        console.error('请求配置错误:', error)
        return Promise.reject(error)
    }
)

// 3. 响应拦截器
apiClient.interceptors.response.use(
    (response) => {
        // 记录响应日志（仅开发环境）
        if (import.meta.env.DEV) {
            console.log('📥 收到响应:', {
                status: response.status,
                url: response.config.url,
                data: response.data
            })
        }

        // 处理统一的API响应格式
        if (response.data && typeof response.data === 'object') {
            // 如果响应有统一的格式
            if (response.data.hasOwnProperty('code') && response.data.hasOwnProperty('data')) {
                // 检查响应代码
                if (response.data.code !== 200) {
                    // 业务错误
                    const error = new Error(response.data.message || '请求失败')
                    error.code = response.data.code
                    error.data = response.data.data
                    return Promise.reject(error)
                }
                return response.data.data
            }
        }

        return response.data
    },
    async (error) => {
        // 记录错误日志
        console.error('API请求错误:', {
            status: error.response?.status,
            url: error.config?.url,
            message: error.message,
            data: error.response?.data
        })

        // 处理HTTP错误
        if (error.response) {
            const status = error.response.status
            const message = getErrorMessage(status, error.response.data)

            // 特殊处理401错误（未授权）
            if (status === 401) {
                // 尝试刷新令牌
                const authStore = useAuthStore()
                try {
                    const newToken = await authStore.refreshAccessToken()
                    if (newToken) {
                        // 重试原始请求
                        error.config.headers.Authorization = `Bearer ${newToken}`
                        return apiClient(error.config)
                    }
                } catch (refreshError) {
                    // 刷新失败，需要重新登录
                    authStore.clearAuthStorage()

                    const notificationStore = useNotificationStore()
                    notificationStore.addNotification({
                        message: '登录已过期，请重新登录',
                        type: 'warning'
                    })

                    // 跳转到登录页
                    if (window.location.pathname !== '/login') {
                        window.location.href = '/login?redirect=' + encodeURIComponent(window.location.pathname)
                    }
                }
            }

            // 处理403错误（禁止访问）
            if (status === 403) {
                const notificationStore = useNotificationStore()
                notificationStore.addNotification({
                    message: '权限不足，无法访问此资源',
                    type: 'error'
                })
            }

            // 处理其他错误
            const notificationStore = useNotificationStore()
            notificationStore.addNotification({
                message: message,
                type: 'error',
                duration: 5000
            })

            // 创建自定义错误对象
            const customError = new Error(message)
            customError.status = status
            customError.data = error.response.data
            return Promise.reject(customError)
        }

        // 处理网络错误
        if (error.request) {
            const notificationStore = useNotificationStore()
            notificationStore.addNotification({
                message: '网络连接失败，请检查网络设置',
                type: 'error'
            })

            const customError = new Error('网络连接失败')
            customError.isNetworkError = true
            return Promise.reject(customError)
        }

        // 其他错误
        return Promise.reject(error)
    }
)

// 4. 辅助函数
function generateRequestId() {
    return 'req_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

function getErrorMessage(status, responseData) {
    const defaultMessages = {
        400: '请求参数错误',
        401: '未授权，请重新登录',
        403: '禁止访问',
        404: '资源未找到',
        408: '请求超时',
        500: '服务器内部错误',
        502: '网关错误',
        503: '服务不可用',
        504: '网关超时'
    }

    // 优先使用服务器返回的错误消息
    if (responseData && responseData.message) {
        return responseData.message
    }

    // 使用默认消息
    return defaultMessages[status] || `请求失败 (${status})`
}

// 5. 导出
export default apiClient

// 6. 添加工具函数
export const apiUtils = {
    /**
     * 取消请求
     */
    createCancelToken: () => {
        return axios.CancelToken.source()
    },

    /**
     * 检查是否是取消错误
     */
    isCancel: (error) => {
        return axios.isCancel(error)
    },

    /**
     * 设置全局请求超时
     */
    setTimeout: (timeout) => {
        apiClient.defaults.timeout = timeout
    },

    /**
     * 设置基础URL
     */
    setBaseURL: (baseURL) => {
        apiClient.defaults.baseURL = baseURL
    }
}