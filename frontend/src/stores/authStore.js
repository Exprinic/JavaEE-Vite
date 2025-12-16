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
            notificationStore.addNotification({message: captcha.value || 'Failed to fetch verify code.', type: 'error'});
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
            error.value = e.response?.data?.message || 'Login failed';
            notificationStore.addNotification({message: error.value, type: 'error'});
            throw e;
        }
    }

    async function register(credentials) {
        const uiStore = useUiStore();
        const notificationStore = useNotificationStore();

        // --- API Registration Logic ---
        try {
            const userData = await authApi.register(credentials);
            userStore.setUser(userData);
            isAuthenticated.value = true;
            uiStore.hideDialogs();
            notificationStore.addNotification({message: userData.message, type: 'success'});
            await router.push('/login');
        } catch (e) {
            error.value = e.response?.message;
            notificationStore.addNotification({message: error.value, type: 'error'});
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
            message.value = response.message || 'Logout successful!';

            uiStore.hideDialogs();
            notificationStore.addNotification({message: message.value, type: 'success'});
            await router.push('/');
        } catch (e) {
            console.error('Logout error:', e);
            notificationStore.addNotification({message: message.value || 'Logout failed.', type: 'error'});
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