import axios from 'axios'
import {useNotificationStore} from '../../stores/notificationStore.js';

// 1. 创建 axios 实例
const apiClient = axios.create({
    baseURL: 'http://localhost:8080/backend/api',
    withCredentials: true, // 确保跨域请求时发送凭证
    timeout: 5000,  // 超时时间
    headers: {
        'Content-Type': 'application/json',  // 统一请求头
    },
})

apiClient.interceptors.request.use(
    (config) => {
        // Add token to headers
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        console.log('Sending request:', config.method, config.url, config.data)
        return config;
    },
    (error) => {
        return Promise.reject(error)
    }
)

apiClient.interceptors.response.use(
    (response) => {
        // Directly return the data from the response
        return response.data;
    },
    (error) => {
        console.log('API request error', error)

        // Handle HTTP network errors
        let message = "";
        // HTTP status code
        const status = error.response?.status;
        switch (status) {
            case 400:
                message = "Request error";
                break;
            case 401:
                message = "Unauthorized, please log in";
                break;
            case 403:
                message = "Access denied";
                break;
            case 404:
                message = `Request address error: ${error.response?.config?.url}`;
                break;
            case 408:
                message = "Request timeout";
                break;
            case 500:
                message = "Internal server error";
                break;
            case 501:
                message = "Service not implemented";
                break;
            case 502:
                message = "Gateway error";
                break;
            case 503:
                message = "Service unavailable";
                break;
            case 504:
                message = "Gateway timeout";
                break;
            case 505:
                message = "HTTP version not supported";
                break;
            default:
                message = "Network connection failure";
        }
        const notificationStore = useNotificationStore();
        notificationStore.addNotification({message, type: 'error'});
        return Promise.reject(error)
    }
)

export default apiClient