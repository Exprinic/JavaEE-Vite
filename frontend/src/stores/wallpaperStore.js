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
    const selectedTag = ref('ALL');

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

    const tags = computed(() => {
        const tagSet = new Set(['ALL']);
        allWallpapers.value.forEach(wallpaper => {
            wallpaper.tags.forEach(tag => tagSet.add(tag.name));
        });
        return Array.from(tagSet);
    });

    const selectedWallpapers = computed(() => {
        let wallpapers = allWallpapers.value;

        if (selectedCategory.value !== 'ALL') {
            wallpapers = wallpapers.filter(w => w.category === selectedCategory.value);
        }

        if (selectedTag.value !== 'ALL') {
            wallpapers = wallpapers.filter(w => w.tags.some(t => t.name === selectedTag.value));
        }

        return wallpapers;
    });

    // --- ACTIONS ---
    async function fetchAllWallpapers() {
        loading.value = true;
        try {
            const response = await wallpaperApi.allWallpapers();
            allWallpapers.value = response.wallpapers;
        } catch (error) {
            console.error('Failed to fetch wallpapers:', error);
        } finally {
            loading.value = false;
        }
    }

    async function searchWallpapers(term) {
        loading.value = true;
        searchTerm.value = term;
        try {
            const response = await wallpaperApi.searchWallpapers(term);
            allWallpapers.value = response.wallpapers;
            // After a search, we might want to reset category and tag filters
            selectedCategory.value = 'ALL';
            selectedTag.value = 'ALL';
        } catch (error) {
            console.error(`Failed to search wallpapers with term "${term}":`, error);
        } finally {
            loading.value = false;
        }
    }

    function setCategory(category) {
        selectedCategory.value = category;
    }

    function setTag(tag) {
        selectedTag.value = tag;
    }

    async function clearFilters() {
        selectedCategory.value = 'ALL';
        selectedTag.value = 'ALL';
        searchTerm.value = '';
        await fetchAllWallpapers(); // Reload all wallpapers
    }

    // --- PERSISTENCE ---
    const storedCategories = localStorage.getItem('wallpaper_categories');
    if (storedCategories) {
        // This is a simplified approach. In a real app, you might want to merge
        // stored categories with dynamically generated ones.
    }

    const storedTags = localStorage.getItem('wallpaper_tags');
    if (storedTags) {
        // similar to categories
    }

    watch(categories, (newCategories) => {
        localStorage.setItem('wallpaper_categories', JSON.stringify(newCategories));
    }, {deep: true});

    watch(tags, (newTags) => {
        localStorage.setItem('wallpaper_tags', JSON.stringify(newTags));
    }, {deep: true});


    function clearCategoriesAndTags() {
        // This function can be expanded if categories/tags are stored directly in the state
        // For now, they are computed, so clearing them means clearing the source data
        allWallpapers.value = [];
    }

    async function fetchCarouselWallpapers() {
        try {
            const response = await wallpaperApi.carouselWallpapers();
            // Assuming the carousel uses a subset of all wallpapers
            return response.data.slice(0, 5);
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
        selectedTag,
        
        // Getters
        categories,
        tags,
        selectedWallpapers,
        
        // Actions
        fetchAllWallpapers,
        searchWallpapers,
        setCategory,
        setTag,
        clearFilters,
        clearCategoriesAndTags,
        fetchCarouselWallpapers,
        uploadWallpaper,
    };
});