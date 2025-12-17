import {defineStore} from 'pinia';
import {ref, computed, watch} from 'vue';
import {wallpaperApi} from '../api/type/wallpaper';
import {useNotificationStore} from "@/stores/notificationStore.js";

export const useWallpaperStore = defineStore('wallpaper', () => {
    // --- STATE ---
    const allWallpapers = ref([]);
    const loading = ref(false);
    const error = ref(null);
    const progress = ref(0);
    const searchTerm = ref('');
    const selectedCategory = ref('ALL');

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

    // --- ACTIONS ---
    async function fetchAllWallpapers() {
        loading.value = true;
        error.value = null;
        try {
            console.log('Fetching all wallpapers...');
            const response = await wallpaperApi.allWallpapers();
            console.log('All wallpapers response:', response);
            // 根据响应结构调整
            allWallpapers.value = response.wallpapers || response.data || response || [];
            console.log('Processed wallpapers count:', allWallpapers.value.length);
        } catch (err) {
            console.error('Failed to fetch wallpapers:', err);
            error.value = err.message || 'Failed to fetch wallpapers';
        } finally {
            loading.value = false;
        }
    }

    async function searchWallpapers(term, category = null) {
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
            
            console.log(`Searching wallpapers with params:`, searchParams);
            const response = await wallpaperApi.searchWallpapers(searchParams);
            console.log('Search response:', response);
            allWallpapers.value = response.wallpapers || response.data || response || [];
            
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
        await fetchAllWallpapers(); // Reload all wallpapers
    }

    // 初始化函数
    async function init() {
        console.log('Initializing wallpaper store...');
        await fetchAllWallpapers();
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

    async function fetchCarouselWallpapers() {
        try {
            console.log('Fetching carousel wallpapers...');
            const response = await wallpaperApi.carouselWallpapers();
            console.log('Raw carousel wallpapers response:', response);
            
            // 检查响应数据结构并正确提取壁纸数据
            let wallpapers = [];
            if (response && typeof response === 'object') {
                if (Array.isArray(response)) {
                    // 直接返回的数组
                    wallpapers = response;
                } else if (response.wallpapers && Array.isArray(response.wallpapers)) {
                    wallpapers = response.wallpapers;
                } else if (response.data && Array.isArray(response.data)) {
                    wallpapers = response.data;
                } else if (response.hasOwnProperty('success')) {
                    // 处理ApiResponse格式 {success: true, data: [...]}
                    wallpapers = response.data || [];
                } else {
                    // 其他情况
                    wallpapers = response.wallpapers || response.data || [];
                }
            } else if (Array.isArray(response)) {
                wallpapers = response;
            }
            
            console.log('Processed carousel wallpapers count:', wallpapers.length);
            console.log('Processed carousel wallpapers:', wallpapers);
            return wallpapers;
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
            return response;

        } catch (err) {
            error.value = err.message || 'An unknown error occurred';
            notificationStore.addNotification({message: `Upload failed: ${error.value}`, type: 'error'});
            throw new Error(error.value);

        } finally {
            loading.value = false;
        }
    };

    return {
        // State
        allWallpapers,
        loading,
        error,
        progress,
        searchTerm,
        selectedCategory,
        
        // Getters
        categories,
        wallpapers,
        
        // Actions
        fetchAllWallpapers,
        searchWallpapers,
        setSearchTerm,
        setCategory,
        clearFilters,
        clearCategoriesAndTags,
        fetchCarouselWallpapers,
        uploadWallpaper,
        init
    };
});