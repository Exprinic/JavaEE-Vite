import {defineStore} from 'pinia';
import {ref, computed, watch} from 'vue';
import {wallpaperApi} from '../api/type/wallpaper';

export const useWallpaperStore = defineStore('wallpaper', () => {
    // --- STATE ---
    const allWallpapers = ref([]);
    const loading = ref(false);
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

    const filteredWallpapers = computed(() => {
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
            const response = await wallpaperApi.getCarouselWallpapers();
            allWallpapers.value = response.data;
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
            allWallpapers.value = response.data;
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

    function clearFilters() {
        selectedCategory.value = 'ALL';
        selectedTag.value = 'ALL';
        searchTerm.value = '';
        fetchAllWallpapers(); // Reload all wallpapers
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
            const response = await wallpaperApi.getCarouselWallpapers();
            // Assuming the carousel uses a subset of all wallpapers
            return response.data.slice(0, 5);
        } catch (error) {
            console.error('Failed to fetch carousel wallpapers:', error);
            return [];
        }
    }

    async function uploadWallpaper(formData) {
        loading.value = true;
        try {
            await wallpaperApi.uploadWallpaper(formData);
            // After upload, refresh the wallpaper list
            await fetchAllWallpapers();
        } catch (error) {
            console.error('Failed to upload wallpaper:', error);
            throw error; // Re-throw to be caught in the component
        } finally {
            loading.value = false;
        }
    }

    return {
        allWallpapers,
        loading,
        searchTerm,
        selectedCategory,
        selectedTag,
        categories,
        tags,
        filteredWallpapers,
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