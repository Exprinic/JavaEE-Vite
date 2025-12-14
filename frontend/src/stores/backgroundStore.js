import {defineStore} from 'pinia';
import {ref} from 'vue';
import {backgroundApi} from '../api/type/background.js';

export const useBackgroundStore = defineStore('background', () => {
    const activeWallpaperUrl = ref('');
    const lastWallpaperUrl = ref('');

    async function initBackground() {
        const response = await backgroundApi.getDefaultWallpaper();
        activeWallpaperUrl.value = response.data.url;
        lastWallpaperUrl.value = response.data.url;
    }

    return {
        activeWallpaperUrl,
        lastWallpaperUrl,
        initBackground
    };
});