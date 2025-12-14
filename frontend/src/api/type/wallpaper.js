import apiClient from '../client/apiClient.js';

export const wallpaperApi = {
    getCarouselWallpapers: () => {
        return apiClient.get('/wallpaper/carousel');
    },
    searchWallpapers: (term) => {
        return apiClient.get(`/wallpaper/search?term=${term}`);
    },
    getRandomWallpaper: () => {
        return apiClient.get('/wallpaper/random');
    },
    uploadWallpaper: (formData) => {
        return apiClient.post('/wallpaper/upload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    }
};