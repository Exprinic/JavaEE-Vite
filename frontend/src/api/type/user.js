import apiClient from '../client/apiClient.js';

export const userApi = {
    fetchProfile: () => {
        return apiClient.get('/user/profile');
    },
    updateProfile: (profileData) => {
        return apiClient.put('/user/profile', profileData);
    },
    changePassword: (passwordData) => {
        return apiClient.post('/user/change-password', passwordData);
    },
    updateBackground: (wallpaperUrl) => {
        return apiClient.post('/user/profile', {background: wallpaperUrl});
    }
};