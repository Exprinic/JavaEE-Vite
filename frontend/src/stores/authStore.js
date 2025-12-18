import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '../api/type/auth'
import { useNotificationStore } from "@/stores/notificationStore.js";
import { useWallpaperStore } from './wallpaperStore'; // 导入壁纸存储

export const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref(!!localStorage.getItem('token'))
  const loading = ref(false)
  const error = ref(null)

  async function login(credentials) {
    loading.value = true
    error.value = null
    
    try {
      const response = await authApi.login(credentials)
      
      if (response && response.accessToken) {
        localStorage.setItem('user', JSON.stringify(response.userInfo));
        localStorage.setItem('accessToken', response.accessToken)
        localStorage.setItem('refreshToken', response.refreshToken);
        localStorage.setItem('token', response.accessToken)

        isAuthenticated.value = true

        const userInfo = response.userInfo
        user.value = userInfo
        const notificationStore = useNotificationStore();
        notificationStore.addNotification({ 
          message: `Welcome back, ${userInfo.nickname || userInfo.username}!`, 
          type: 'success' 
        })
        
        return response
      } else {
        throw new Error('Invalid response from server')
      }
    } catch (err) {
      console.error('Login failed:', err)
      error.value = err.message || 'Login failed'
      
      const notificationStore = useNotificationStore();
      notificationStore.addNotification({ 
        message: error.value, 
        type: 'error' 
      })
      
      throw err
    } finally {
      loading.value = false
    }
  }

  async function register(userData) {
    loading.value = true
    error.value = null
    
    try {
      const response = await authApi.register(userData)
      
      const notificationStore = useNotificationStore();
      notificationStore.addNotification({ 
        message: 'Registration successful! Please log in.', 
        type: 'success' 
      })
      
      return response
    } catch (err) {
      console.error('Registration failed:', err)
      error.value = err.message || 'Registration failed'
      
      const notificationStore = useNotificationStore();
      notificationStore.addNotification({ 
        message: error.value, 
        type: 'error' 
      })
      
      throw err
    } finally {
      loading.value = false
    }
  }

  function logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('active_wallpaper') // 清除活动壁纸
    
    isAuthenticated.value = false
    user.value = null
    
    // 清除壁纸存储中的缓存数据
    const wallpaperStore = useWallpaperStore();
    wallpaperStore.clearCacheOnLogout();
    
    const notificationStore = useNotificationStore();
    notificationStore.addNotification({ 
      message: 'You have been logged out successfully.', 
      type: 'info' 
    })
  }

  function checkAuth() {
    const token = localStorage.getItem('token')
    const userData = localStorage.getItem('user')
    
    if (token && userData) {
      isAuthenticated.value = true
      try {
        user.value = JSON.parse(userData)
      } catch (e) {
        console.error('Failed to parse user data:', e)
        logout() // 如果用户数据损坏，则登出
      }
    } else {
      isAuthenticated.value = false
      user.value = null
    }
  }

    async function getCaptcha(credentials) {
    loading.value = true
    error.value = null
    
    try {
      const response = await authApi.fetchCaptcha(credentials)
      
      const notificationStore = useNotificationStore();
      notificationStore.addNotification({ 
        message: response.captcha,
        type: 'success' 
      })

    } catch (err) {
      console.error('Failed to fetch captcha:', err)
      error.value = err.message || 'Failed to fetch captcha'
      
      const notificationStore = useNotificationStore();
      notificationStore.addNotification({ 
        message: error.value, 
        type: 'error' 
      })
      
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    isAuthenticated,
    user,
    loading,
    error,
    
    login,
    register,
    logout,
    checkAuth,
    getCaptcha
  }
})