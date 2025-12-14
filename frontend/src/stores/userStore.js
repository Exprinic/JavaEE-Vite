import {defineStore} from 'pinia';
import {ref} from 'vue';
import {userApi} from '../api/type/user.js';
import {useNotificationStore} from './notificationStore';
import {useWallpaperStore} from './wallpaperStore';
import {getImageUrl} from "../utils/image.js";

export const useUserStore = defineStore('user', () => {
    const user = ref(null);
    const uploadedWallpapers = ref([]);
    const favoriteWallpapers = ref([]);
    const cart = ref([]);

    const wallpaperStore = useWallpaperStore();
    const notificationStore = useNotificationStore();

    function setUser(userData) {
        user.value = userData;
        localStorage.setItem('user', JSON.stringify(userData));
        wallpaperStore.loadUserWallpaper(getImageUrl(userData.imageUrl));
    }

    function clearUser() {
        wallpaperStore.clearActiveWallpaper();
        user.value = null;
        localStorage.removeItem('user');
    }

    async function fetchProfile() {
        try {
            profile.value = await userApi.fetchProfile();
        } catch (error) {
            notificationStore.addNotification({message: 'Failed to fetch user profile.', type: 'error'});
        }
    }

    async function updateProfile(profileData) {
        try {
            await userApi.updateProfile(profileData);
            user.value = {...user.value, ...profileData};
            localStorage.setItem('user', JSON.stringify(user.value));
            notificationStore.addNotification({message: 'Profile updated successfully.', type: 'success'});
        } catch (error) {
            notificationStore.addNotification({message: 'Failed to update profile.', type: 'error'});
        }
    }

    async function changePassword(passwordData) {
        try {
            await userApi.changePassword(passwordData);
            notificationStore.addNotification({message: 'Password changed successfully.', type: 'success'});
        } catch (error) {
            notificationStore.addNotification({message: 'Failed to change password.', type: 'error'});
        }
    }

    async function updateUserBackground(wallpaperUrl) {
        try {
            await userApi.updateBackground(wallpaperUrl);
            if (user.value) {
                user.value.background = wallpaperUrl;
                localStorage.setItem('user', JSON.stringify(user.value));
            }
            notificationStore.addNotification({message: 'User background updated successfully.', type: 'success'});
        } catch (error) {
            notificationStore.addNotification({message: 'Failed to update user background.', type: 'error'});
        }
    }

    return {
        user,
        uploadedWallpapers,
        favoriteWallpapers,
        setUser,
        clearUser,
        fetchProfile,
        updateProfile,
        changePassword,
        updateUserBackground
    };
});