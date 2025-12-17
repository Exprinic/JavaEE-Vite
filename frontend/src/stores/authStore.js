import {defineStore} from 'pinia';
import {ref} from 'vue';
import router from '../router'; // 直接导入路由实例
import {
    authApi
} from '../api/type/auth';
import {useUserStore} from './userStore';
import {useUiStore} from "./uiStore";
import {useNotificationStore} from "./notificationStore";
import {useWallpaperStore} from './wallpaperStore'; // Import wallpaper store

export const useAuthStore = defineStore('auth', () => {
    const error = ref(null);
    const captcha = ref('');
    const isAuthenticated = ref(false);

    const userStore = useUserStore();

    async function getCaptcha(credentials) {
        const notificationStore = useNotificationStore();
        try {
            const response = await authApi.fetchCaptcha(credentials);
            captcha.value = response.captcha;

            notificationStore.addNotification({message: captcha.value, type: 'success'});
        } catch (e) {
            console.error('Failed to fetch verify code:', e);
            notificationStore.addNotification({message: captcha.value || '获取验证码失败', type: 'error'});
        }
    }

    async function login(credentials) {
        const uiStore = useUiStore();
        const notificationStore = useNotificationStore();
        // --- API Login Logic ---
        try {
            const userData = await authApi.login(credentials);
            userStore.setUser(userData);
            isAuthenticated.value = true;
            uiStore.hideDialogs();
            notificationStore.addNotification({message: userData.message, type: 'success'});
            await router.push('/');
        } catch (e) {
            console.error('Login error:', e); // 添加错误日志
            // 错误消息现在从统一响应格式中获取
            error.value = e.response?.data?.message || '登录失败，请稍后再试';
            notificationStore.addNotification({message: error.value, type: 'error'});
            throw e;
        }
    }

    async function register(credentials) {
        const uiStore = useUiStore();
        const notificationStore = useNotificationStore();

        // --- API Registration Logic ---
        try {
            // 确保所有必需的字段都存在，包括 inviteCode
            const registerData = {
                username: credentials.username,
                phone: credentials.phone,
                password: credentials.password,
                captcha: credentials.captcha,
                inviteCode: credentials.inviteCode || '' // 添加 inviteCode 字段
            };
            
            const userData = await authApi.register(registerData);
            userStore.setUser(userData);
            isAuthenticated.value = true;
            uiStore.hideDialogs();
            notificationStore.addNotification({message: userData.message, type: 'success'});
            await router.push('/login');
        } catch (e) {
            // 提供更具体的错误消息
            const errorMessage = e.response?.data?.message || '注册失败，请检查输入信息或稍后再试';
            error.value = errorMessage;
            notificationStore.addNotification({message: errorMessage, type: 'error'});
            throw e;
        }
    }

    async function logout(credentials) {
        const uiStore = useUiStore();
        const notificationStore = useNotificationStore();
        const message = ref( '');
        try {
            const response = await authApi.logOut(credentials);
            userStore.clearUser();
            isAuthenticated.value = false;
            message.value = response.message || '登出成功！';

            uiStore.hideDialogs();
            notificationStore.addNotification({message: message.value, type: 'success'});
            await router.push('/');
        } catch (e) {
            console.error('Logout error:', e);
            notificationStore.addNotification({message: message.value || '登出失败，请稍后再试', type: 'error'});
        }
    }

    function checkAuth() {
        const notificationStore = useNotificationStore();
        const wallpaperStore = useWallpaperStore();
        const storedUser = localStorage.getItem('user');
        if (storedUser) {
            const user = JSON.parse(storedUser);
            userStore.setUser(user);
            isAuthenticated.value = true;

            const userWallpaper = localStorage.getItem(`user-wallpaper-${user.id}`);
            if (userWallpaper) {
                // wallpaperStore.loadUserWallpaper(userWallpaper);
            }
        }
    }

    return {
        error,
        captcha: captcha,
        isAuthenticated,
        getCaptcha,
        login,
        register,
        logout,
        checkAuth
    };
});