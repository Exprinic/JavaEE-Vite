import {computed, ref} from 'vue';
import {wallpaperApi} from '../api/type/wallpaper.js';
import {useNotificationStore} from '../stores/notificationStore';

export function useWallpaper() {
    const loading = ref(false);
    const error = ref(null);
    const progress = ref(0);

    const uploadWallpaper = async (formData) => {
        loading.value = true;
        error.value = null;
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


    const preFilteredWallpapers = computed(() => {
        let result = allWallpapers.value;
        if (searchQuery.value) {
            const lowerCaseQuery = searchQuery.value.toLowerCase();
            result = result.filter(w => w.name.toLowerCase().includes(lowerCaseQuery));
        }
        if (activeFilter.value !== 'All') {
            result = result.filter(w => w.filter === activeFilter.value);
        }
        return result;
    });

    const availableTags = computed(() => {
        const allTags = preFilteredWallpapers.value.flatMap(w => w.tags);
        return ['All', ...new Set(allTags)];
    });

    const filteredWallpapers = computed(() => {
        let result = preFilteredWallpapers.value;
        if (activeTags.value.length > 0) {
            result = result.filter(w =>
                activeTags.value.every(activeTag => w.tags.includes(activeTag))
            );
        }
        return result;
    });

    // --- ACTIONS ---

    function setFilter(filter) {
        activeFilter.value = filter;
        activeTags.value = [];
    }

    function setSearchQuery(query) {
        searchQuery.value = query;
    }

    function toggleTag(tag) {
        if (tag === 'All') {
            activeTags.value = [];
            return;
        }
        const index = activeTags.value.indexOf(tag);
        if (index > -1) {
            activeTags.value.splice(index, 1);
        } else {
            activeTags.value.push(tag);
        }
    }


    // Dynamically create filters from the fetched data
    const backendFilters = new Set(response.data.map(w => w.filter));
    filters.value = ['All', ...backendFilters];

    return {
        loading,
        error,
        progress,
        uploadWallpaper,
    };
}