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

    const uiStore = useUiStore();
    const notificationStore = useNotificationStore();
    const userStore = useUserStore();

    async function getCaptcha(credentials) {
        try {
            const response = await authApi.fetchCaptcha(credentials);
            captcha.value = response.captcha;

            notificationStore.addNotification({message: `Your verify code is: ${response.captcha}`, type: 'success'});
        } catch (e) {
            console.error('Failed to fetch verify code:', e);
            notificationStore.addNotification({message: 'Failed to fetch verify code.', type: 'error'});
        }
    }

    async function login(credentials) {
        // --- Test User Logic (for development and testing) ---
        // if (
        //     credentials.phone === '13800000000' &&
        //     credentials.password === 'Test123!'
        // ) {
        //     console.log('Bypassing login for test user');
        //     const testUser = {
        //         id: 'test-user',
        //         name: 'Test User',
        //         phone: '13800000000',
        //         password: 'Test123!',
        //         role: 'user',
        //         imageUrl: './wallpapers/1.jpg'
        //     };
        //     userStore.setUser(testUser);
        //     isAuthenticated.value = true;
        //     uiStore.hideDialogs();
        //     notificationStore.addNotification({message: 'Login successful!', type: 'success'});
        //     await router.push('/');
        //     return;
        // }
        //
        // // --- Hardcoded Admin Logic (for development and testing) ---
        // if (
        //     credentials.phone === '13800000000' &&
        //     credentials.password === 'Test123!'
        // ) {
        //     console.log('Hardcoded admin login successful');
        //     const adminUser = {
        //         id: '2',
        //         name: 'Gabino Slater',
        //         phone: '13800000000',
        //         password: 'Test123!',
        //         role: 'admin',
        //         imageUrl: './wallpapers/2.jpg'
        //     };
        //
        //     userStore.setUser(adminUser);
        //     isAuthenticated.value = true;
        //     uiStore.hideDialogs();
        //     notificationStore.addNotification({message: 'Login successful!', type: 'success'});
        //     await router.push('/');
        //     return;
        // }

        // --- API Login Logic ---
        try {
            const userData = await authApi.login(credentials);
            userStore.setUser(userData);
            isAuthenticated.value = true;
            uiStore.hideDialogs();
            notificationStore.addNotification({message: 'Login successful!', type: 'success'});
            await router.push('/');
        } catch (e) {
            console.error('Login error:', e); // 添加错误日志
            // 错误消息现在从统一响应格式中获取
            error.value = e.response?.data?.message || 'Login failed';
            notificationStore.addNotification({message: error.value, type: 'error'});
            // await getCaptcha(); // Refresh captcha on failed login
            throw e;
        }
    }

    async function register(credentials) {
        // --- Temporary Registration (for development and testing) ---
        // Set to `true` to enable temporary registration without API call
        const temporaryRegisterEnabled = false;

        if (temporaryRegisterEnabled) {
            console.log('Using temporary registration');
            localStorage.setItem('user', JSON.stringify({...credentials, id: Date.now().toString()}));
            uiStore.hideDialogs();
            notificationStore.addNotification({message: 'Registration successful! Please log in.', type: 'success'});
            return;
        }

        // --- API Registration Logic ---
        try {
            await authApi.register(credentials);
            uiStore.hideDialogs();
            notificationStore.addNotification({message: 'Registration successful! Please log in.', type: 'success'});
        } catch (e) {
            error.value = e.response?.data?.message || 'Registration failed';
            notificationStore.addNotification({message: error.value, type: 'error'});
            // await getCaptcha();
            throw e;
        }
    }

    function logout() {
        userStore.clearUser();
        isAuthenticated.value = false;
        router.push('/');
        
        // Also hide any open auth dialogs
        uiStore.hideDialogs();
    }

    function checkAuth() {
        const userStore = useUserStore();
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