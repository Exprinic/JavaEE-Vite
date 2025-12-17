import apiClient from '../client/apiClient.js';

export const backgroundApi = {
    getCurrentUserBackground: () => {
        return apiClient.get('/background/current');
    },
    getBackgroundHistory: () => {
        return apiClient.get('/background/history');
    },
};