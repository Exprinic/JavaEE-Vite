import apiClient from '../client/apiClient.js';

export const backgroundApi = {
    getDefaultWallpaper: () => {
        return apiClient.get('/background/default');
    },
    getCurrentUserBackground: () => {
        return apiClient.get('/background/current');
    },
    getBackgroundHistory: () => {
        return apiClient.get('/background/history');
    },
};