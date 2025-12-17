import {defineStore} from 'pinia';
import {ref} from 'vue';
import {backgroundApi} from '../api/type/background.js';

export const useBackgroundStore = defineStore('background', () => {
    const activeWallpaperUrl = ref('');
    const lastWallpaperUrl = ref('');

    async function initBackground() {
        try {
            // 直接使用前端public目录下的图片作为默认背景
            const defaultWallpaper = '';
            activeWallpaperUrl.value = defaultWallpaper;
            lastWallpaperUrl.value = defaultWallpaper;
            
            // 如果需要从后端获取默认背景，可以取消下面的注释
            // const response = await backgroundApi.getDefaultWallpaper();
            // activeWallpaperUrl.value = response.defaultBackgroundUrl;
            // lastWallpaperUrl.value = response.defaultBackgroundUrl;
        } catch (error) {
            console.error('Failed to initialize background:', error);
            // 出错时使用默认图片
            const fallbackImage = '/wallpapers/1.jpg';
            activeWallpaperUrl.value = fallbackImage;
            lastWallpaperUrl.value = fallbackImage;
        }
    }

    return {
        activeWallpaperUrl,
        lastWallpaperUrl,
        initBackground
    };
});