// stores/authStore.js - 完整重构
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '../api/type/auth'
import { useNotificationStore } from "./notificationStore.js"
import { useUserStore } from './userStore'
import { useRouter } from 'vue-router'

export const useAuthStore = defineStore('auth', () => {
  // ============ State ============
  const isAuthenticated = ref(false)
  const loading = ref(false)
  const error = ref(null)
  const authChecked = ref(false)

  // ============ Getters ============
  const isLoading = computed(() => loading.value)
  const hasError = computed(() => error.value !== null)
  const isAuthChecked = computed(() => authChecked.value)

  // ============ Actions ============

  /**
   * 初始化认证状态
   */
  function initializeAuth() {
    const token = getStoredAccessToken()
    const userId = getStoredUserId()

    if (token && userId) {
      // 验证token是否过期
      const tokenExpiry = getStoredTokenExpiry()
      if (tokenExpiry && Date.now() < tokenExpiry) {
        isAuthenticated.value = true
        // 加载用户信息
        loadUserInfo(userId)
      } else {
        // Token已过期，尝试刷新
        attemptTokenRefresh()
      }
    }

    authChecked.value = true
  }

  /**
   * 用户登录
   */
  async function login(credentials) {
    loading.value = true
    error.value = null

    try {
      const response = await authApi.login(credentials)

      if (response && response.accessToken) {
        // 存储认证信息
        storeAuthData(response)

        // 更新状态
        isAuthenticated.value = true

        // 更新用户信息
        const userStore = useUserStore()
        userStore.setUser(response.userInfo)

        // 显示通知
        const notificationStore = useNotificationStore()
        notificationStore.addNotification({
          message: `欢迎回来，${response.userInfo.nickname || response.userInfo.username}!`,
          type: 'success'
        })

        // 启动自动刷新token
        scheduleTokenRefresh(response.expiresIn)

        return response
      } else {
        throw new Error('无效的服务器响应')
      }
    } catch (err) {
      console.error('登录失败:', err)
      error.value = err.message || '登录失败'

      const notificationStore = useNotificationStore()
      notificationStore.addNotification({
        message: error.value,
        type: 'error'
      })

      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * 用户注册
   */
  async function register(userData) {
    loading.value = true
    error.value = null

    try {
      const response = await authApi.register(userData)

      if (response && response.accessToken) {
        // 存储认证信息（注册即登录）
        storeAuthData(response)

        // 更新状态
        isAuthenticated.value = true

        // 更新用户信息
        const userStore = useUserStore()
        userStore.setUser(response.userInfo)

        // 显示通知
        const notificationStore = useNotificationStore()
        notificationStore.addNotification({
          message: `注册成功！欢迎 ${response.userInfo.nickname}!`,
          type: 'success'
        })

        // 启动自动刷新token
        scheduleTokenRefresh(response.expiresIn)

        return response
      } else {
        throw new Error('无效的服务器响应')
      }
    } catch (err) {
      console.error('注册失败:', err)
      error.value = err.message || '注册失败'

      const notificationStore = useNotificationStore()
      notificationStore.addNotification({
        message: error.value,
        type: 'error'
      })

      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * 用户登出
   */
  async function logout() {
    try {
      const accessToken = getStoredAccessToken()
      if (accessToken) {
        await authApi.logout({ token: accessToken })
      }
    } catch (err) {
      console.warn('登出API调用失败:', err)
      // 即使API失败，也执行本地清理
    } finally {
      // 清理本地存储
      clearAuthStorage()

      // 清理Pinia状态
      isAuthenticated.value = false

      // 清理用户信息
      const userStore = useUserStore()
      userStore.clearUser()

      // 清理壁纸存储
      const wallpaperStore = useWallpaperStore()
      if (wallpaperStore.clearCacheOnLogout) {
        wallpaperStore.clearCacheOnLogout()
      }

      // 取消token刷新定时器
      cancelScheduledRefresh()

      // 显示通知
      const notificationStore = useNotificationStore()
      notificationStore.addNotification({
        message: '已成功登出',
        type: 'info'
      })

      // 跳转到登录页
      const router = useRouter()
      router.push('/login')
    }
  }

  /**
   * 获取验证码
   */
  async function getCaptcha(credentials) {
    loading.value = true
    error.value = null

    try {
      const response = await authApi.fetchCaptcha(credentials)

      const notificationStore = useNotificationStore()
      notificationStore.addNotification({
        message: `验证码已发送: ${response.captcha}`,
        type: 'info',
        duration: 10000 // 10秒
      })

      return response
    } catch (err) {
      console.error('获取验证码失败:', err)
      error.value = err.message || '获取验证码失败'

      const notificationStore = useNotificationStore()
      notificationStore.addNotification({
        message: error.value,
        type: 'error'
      })

      throw err
    } finally {
      loading.value = false
    }
  }

  /**
   * 刷新访问令牌
   */
  async function refreshAccessToken() {
    const refreshToken = getStoredRefreshToken()
    if (!refreshToken) {
      throw new Error('没有可用的刷新令牌')
    }

    try {
      const response = await authApi.refreshToken({ refreshToken })

      if (response && response.accessToken) {
        // 更新存储的token
        updateStoredTokens(response.accessToken, response.refreshToken, response.expiresIn)

        // 重新调度刷新
        scheduleTokenRefresh(response.expiresIn)

        return response.accessToken
      }
    } catch (err) {
      console.error('刷新令牌失败:', err)
      // 刷新失败，需要重新登录
      clearAuthStorage()
      isAuthenticated.value = false

      const notificationStore = useNotificationStore()
      notificationStore.addNotification({
        message: '登录已过期，请重新登录',
        type: 'warning'
      })

      throw err
    }
  }

  /**
   * 检查认证状态
   */
  function checkAuth() {
    const token = getStoredAccessToken()
    const tokenExpiry = getStoredTokenExpiry()

    if (token && tokenExpiry && Date.now() < tokenExpiry) {
      isAuthenticated.value = true
      return true
    } else if (token && getStoredRefreshToken()) {
      // Token过期但有刷新令牌，尝试刷新
      attemptTokenRefresh()
      return false
    } else {
      isAuthenticated.value = false
      return false
    }
  }

  // ============ 私有辅助方法 ============

  /**
   * 存储认证数据
   */
  function storeAuthData(response) {
    const { accessToken, refreshToken, expiresIn, userInfo } = response

    // 计算过期时间戳
    const expiryTime = Date.now() + (expiresIn * 1000)

    // 存储到localStorage
    localStorage.setItem('access_token', accessToken)
    localStorage.setItem('refresh_token', refreshToken)
    localStorage.setItem('token_expiry', expiryTime.toString())
    localStorage.setItem('user_id', userInfo.userId.toString())

    // 只存储必要的用户信息
    const minimalUserInfo = {
      userId: userInfo.userId,
      nickname: userInfo.nickname,
      avatar: userInfo.avatar,
      phone: userInfo.phone,
      email: userInfo.email,
      userType: userInfo.userType
    }
    localStorage.setItem('user_info', JSON.stringify(minimalUserInfo))
  }

  /**
   * 更新存储的令牌
   */
  function updateStoredTokens(accessToken, refreshToken, expiresIn) {
    const expiryTime = Date.now() + (expiresIn * 1000)

    localStorage.setItem('access_token', accessToken)
    if (refreshToken) {
      localStorage.setItem('refresh_token', refreshToken)
    }
    localStorage.setItem('token_expiry', expiryTime.toString())
  }

  /**
   * 清理认证存储
   */
  function clearAuthStorage() {
    localStorage.removeItem('access_token')
    localStorage.removeItem('refresh_token')
    localStorage.removeItem('token_expiry')
    localStorage.removeItem('user_id')
    localStorage.removeItem('user_info')

    // 清理sessionStorage
    sessionStorage.clear()
  }

  /**
   * 获取存储的访问令牌
   */
  function getStoredAccessToken() {
    return localStorage.getItem('access_token')
  }

  /**
   * 获取存储的刷新令牌
   */
  function getStoredRefreshToken() {
    return localStorage.getItem('refresh_token')
  }

  /**
   * 获取存储的令牌过期时间
   */
  function getStoredTokenExpiry() {
    const expiry = localStorage.getItem('token_expiry')
    return expiry ? parseInt(expiry) : null
  }

  /**
   * 获取存储的用户ID
   */
  function getStoredUserId() {
    const userId = localStorage.getItem('user_id')
    return userId ? parseInt(userId) : null
  }

  /**
   * 加载用户信息
   */
  function loadUserInfo(userId) {
    const userInfoStr = localStorage.getItem('user_info')
    if (userInfoStr) {
      try {
        const userInfo = JSON.parse(userInfoStr)
        const userStore = useUserStore()
        userStore.setUser(userInfo)
      } catch (e) {
        console.error('解析用户信息失败:', e)
      }
    }
  }

  /**
   * 尝试刷新令牌
   */
  async function attemptTokenRefresh() {
    try {
      await refreshAccessToken()
      isAuthenticated.value = true
    } catch (err) {
      isAuthenticated.value = false
    }
  }

  /**
   * 调度令牌刷新
   */
  let refreshTimer = null
  function scheduleTokenRefresh(expiresIn) {
    // 取消现有的定时器
    cancelScheduledRefresh()

    // 在token过期前5分钟刷新
    const refreshDelay = (expiresIn - 300) * 1000 // 转换为毫秒

    if (refreshDelay > 0) {
      refreshTimer = setTimeout(async () => {
        try {
          await refreshAccessToken()
        } catch (err) {
          console.error('自动刷新令牌失败:', err)
        }
      }, refreshDelay)
    }
  }

  /**
   * 取消调度的刷新
   */
  function cancelScheduledRefresh() {
    if (refreshTimer) {
      clearTimeout(refreshTimer)
      refreshTimer = null
    }
  }

  // ============ 返回 ============
  return {
    // State
    isAuthenticated,
    loading,
    error,
    authChecked,

    // Getters
    isLoading,
    hasError,
    isAuthChecked,

    // Actions
    initializeAuth,
    login,
    register,
    logout,
    getCaptcha,
    refreshAccessToken,
    checkAuth,

    // 工具方法
    getStoredAccessToken,
    clearAuthStorage
  }
})

// 导入壁纸存储（如果存在）
const useWallpaperStore = () => {
  // 这是一个占位函数，实际应该从对应的文件导入
  return {
    clearCacheOnLogout: () => {}
  }
}