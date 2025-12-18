import {defineStore} from 'pinia';
import {ref, computed, watch} from 'vue';
import {wallpaperApi} from '../api/type/wallpaper';
import {useNotificationStore} from "@/stores/notificationStore.js";

export const useWallpaperStore = defineStore('wallpaper', () => {
    // --- STATE ---
    const allWallpapers = ref([]);
    const carouselWallpapers = ref([]); // 专门用于轮播图的壁纸
    const loading = ref(false);
    const error = ref(null);
    const progress = ref(0);
    const searchTerm = ref('');
    const selectedCategory = ref('ALL');
    const activeWallpaperUrl = ref(localStorage.getItem('active_wallpaper') || '/default-background.jpg');
    const temporaryBackground = ref(null); // 用于临时预览的背景
    const lastFetchTime = ref(0); // 上次获取数据的时间
    const lastCarouselFetchTime = ref(0); // 上次获取轮播图数据的时间
    const cacheDuration = 5 * 60 * 1000; // 缓存5分钟

    // --- GETTERS ---
    const categories = computed(() => {
        const categorySet = new Set(['ALL']);
        allWallpapers.value.forEach(wallpaper => {
            if (wallpaper.category) {
                categorySet.add(wallpaper.category);
            }
        });
        return Array.from(categorySet);
    });

    const wallpapers = computed(() => {
        // 直接返回所有壁纸，筛选逻辑已经移到后端
        return allWallpapers.value;
    });

    // 检查缓存是否有效
    const isCacheValid = () => {
        return allWallpapers.value.length > 0 && 
               (Date.now() - lastFetchTime.value) < cacheDuration;
    };
    
    // 检查轮播图缓存是否有效
    const isCarouselCacheValid = () => {
        return carouselWallpapers.value.length > 0 && 
               (Date.now() - lastCarouselFetchTime.value) < cacheDuration;
    };

    // --- ACTIONS ---
    async function fetchAllWallpapers(force = false) {
        // 如果不是强制刷新且缓存有效，则直接返回缓存数据
        if (!force && isCacheValid()) {
            return;
        }
        
        loading.value = true;
        error.value = null;
        try {
            const response = await wallpaperApi.allWallpapers();
            // apiClient拦截器已经处理了ApiResponse格式，直接使用返回的数据
            allWallpapers.value = response || [];
            lastFetchTime.value = Date.now(); // 更新获取时间
        } catch (err) {
            console.error('Failed to fetch wallpapers:', err);
            error.value = err.message || 'Failed to fetch wallpapers';
        } finally {
            loading.value = false;
        }
    }

    async function searchWallpapers(term, category = null, force = false) {
        // 如果不是强制刷新且缓存有效，则直接返回缓存数据
        if (!force && term === searchTerm.value && category === selectedCategory.value && isCacheValid()) {
            return;
        }
        
        loading.value = true;
        error.value = null;
        try {
            // 构建搜索参数
            const searchParams = {};
            
            if (term) {
                searchParams.q = term;
            }
            
            if (category && category !== 'ALL') {
                searchParams.category = category;
            }
            
            const response = await wallpaperApi.searchWallpapers(searchParams);
            
            // apiClient拦截器已经处理了ApiResponse格式，直接使用返回的数据
            allWallpapers.value = response || [];
            lastFetchTime.value = Date.now(); // 更新获取时间
            
            // 更新搜索词和分类状态
            searchTerm.value = term || '';
            if (category !== null) {
                selectedCategory.value = category;
            }
        } catch (err) {
            console.error(`Failed to search wallpapers:`, err);
            error.value = err.message || 'Failed to search wallpapers';
        } finally {
            loading.value = false;
        }
    }

    function setSearchTerm(term) {
        searchTerm.value = term || '';
    }

    function setCategory(category) {
        selectedCategory.value = category;
    }

    async function clearFilters() {
        selectedCategory.value = 'ALL';
        searchTerm.value = '';
        await fetchAllWallpapers(true); // 强制刷新
    }

    // 初始化函数
    async function init(force = false) {
        // 如果不是强制刷新且缓存有效，则直接返回
        if (!force && isCacheValid()) {
            return;
        }
        
        await fetchAllWallpapers(force);
    }

    // --- PERSISTENCE ---
    const storedCategories = localStorage.getItem('wallpaper_categories');
    if (storedCategories) {
        // This is a simplified approach. In a real app, you might want to merge
        // stored categories with dynamically generated ones.
    }

    watch(categories, (newCategories) => {
        localStorage.setItem('wallpaper_categories', JSON.stringify(newCategories));
    }, {deep: true});

    function clearCategoriesAndTags() {
        // This function can be expanded if categories/tags are stored directly in the state
        // For now, they are computed, so clearing them means clearing the source data
        allWallpapers.value = [];
    }

    async function fetchCarouselWallpapers(force = false) {
        // 如果不是强制刷新且缓存有效，则直接返回缓存数据
        if (!force && isCarouselCacheValid()) {
            return carouselWallpapers.value;
        }
        
        try {
            const response = await wallpaperApi.carouselWallpapers();
            
            // apiClient拦截器已经处理了ApiResponse格式，直接使用返回的数据
            carouselWallpapers.value = Array.isArray(response) ? response : [];
            lastCarouselFetchTime.value = Date.now(); // 更新获取时间
            
            return carouselWallpapers.value;
        } catch (error) {
            console.error('Failed to fetch carousel wallpapers:', error);
            return [];
        }
    }

    async function uploadWallpaper(formData){
        loading.value = true;
        progress.value = 0;
        const notificationStore = useNotificationStore();

        try {
            const response = await wallpaperApi.uploadWallpaper(formData, (progressEvent) => {
                progress.value = Math.round((progressEvent.loaded * 100) / progressEvent.total);
            });

            notificationStore.addNotification({message: 'Wallpaper uploaded successfully!', type: 'success'});
            
            // 上传成功后，强制刷新壁纸列表
            await fetchAllWallpapers(true);
            
            return response;

        } catch (err) {
            error.value = err.message || 'An unknown error occurred';
            notificationStore.addNotification({message: `Upload failed: ${error.value}`, type: 'error'});
            throw new Error(error.value);

        } finally {
            loading.value = false;
        }
    };

    // 添加获取壁纸详情的方法
    async function fetchWallpaper(id, force = false) {
        // 先检查是否已经在列表中存在该壁纸
        const existingWallpaper = allWallpapers.value.find(w => w && w.id == id);
        if (!force && existingWallpaper) {
            return existingWallpaper;
        }
        
        loading.value = true;
        error.value = null;
        try {
            const response = await wallpaperApi.getWallpaperDetail(id);
            // 更新当前壁纸
            const index = allWallpapers.value.findIndex(w => w && w.id == id);
            if (index !== -1) {
                // 如果当前壁纸已在列表中，则替换它
                allWallpapers.value.splice(index, 1, response);
            } else {
                // 如果当前壁纸不在列表中，则添加到列表开头
                allWallpapers.value.unshift(response);
            }
            return response;
        } catch (err) {
            console.error('Failed to fetch wallpaper:', err);
            error.value = err.message || 'Failed to fetch wallpaper';
            throw err;
        } finally {
            loading.value = false;
        }
    }

    // 设置背景预览（临时）
    function setBackgroundPreview(url) {
        temporaryBackground.value = url;
        updateBodyBackground(url);
    }

    // 永久设置背景（持久化）
    function setActiveWallpaper(url) {
        activeWallpaperUrl.value = url;
        localStorage.setItem('active_wallpaper', url);
        updateBodyBackground(url);
        // 清除临时背景
        temporaryBackground.value = null;
        return Promise.resolve();
    }

    // 设置背景（通用方法）
    function setBackground(url) {
        updateBodyBackground(url);
    }

    // 更新body背景的辅助函数
    function updateBodyBackground(url) {
        // 移除之前可能添加的内联样式
        document.body.style.backgroundImage = '';
        document.body.style.background = '';
        
        // 添加新的样式
        const styleId = 'wallpaper-background-style';
        let styleElement = document.getElementById(styleId);
        if (!styleElement) {
            styleElement = document.createElement('style');
            styleElement.id = styleId;
            document.head.appendChild(styleElement);
        }
        
        // 使用CSS创建更好的背景效果
        styleElement.innerHTML = `
            body {
                background: url(${url}) no-repeat center center fixed;
                background-size: cover;
                min-height: 100vh;
            }
        `;
    }

    // 设置默认背景
    function setDefaultBackground() {
        const defaultUrl = '/default-background.jpg';
        activeWallpaperUrl.value = defaultUrl;
        localStorage.setItem('active_wallpaper', defaultUrl);
        updateBodyBackground(defaultUrl);
        // 清除临时背景
        temporaryBackground.value = null;
    }

    // 恢复活动壁纸
    function restoreActiveWallpaper() {
        // 如果有临时背景，清除它
        if (temporaryBackground.value) {
            temporaryBackground.value = null;
        }
        
        // 恢复活动壁纸
        updateBodyBackground(activeWallpaperUrl.value);
    }

    // 用户登出时清空缓存
    function clearCacheOnLogout() {
        allWallpapers.value = [];
        carouselWallpapers.value = [];
        lastFetchTime.value = 0;
        lastCarouselFetchTime.value = 0;
        searchTerm.value = '';
        selectedCategory.value = 'ALL';
        // 不清除活动壁纸，因为用户可能只是登出但仍然希望看到背景
    }

    return {
        // State
        allWallpapers,
        carouselWallpapers,
        loading,
        error,
        progress,
        searchTerm,
        selectedCategory,
        activeWallpaperUrl,
        temporaryBackground,
        lastFetchTime,
        lastCarouselFetchTime,
        
        // Getters
        categories,
        wallpapers,
        isCacheValid,
        isCarouselCacheValid,
        
        // Actions
        fetchAllWallpapers,
        searchWallpapers,
        setSearchTerm,
        setCategory,
        clearFilters,
        clearCategoriesAndTags,
        fetchCarouselWallpapers,
        uploadWallpaper,
        fetchWallpaper, // 添加新方法
        init,
        setBackground,
        setBackgroundPreview,
        setActiveWallpaper,
        restoreActiveWallpaper,
        clearCacheOnLogout
    };
});