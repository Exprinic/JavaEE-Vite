import apiClient from '../client/apiClient.js';

const wallpaperApi = {
    // 获取所有壁纸
    async allWallpapers() {
        try {
            return await apiClient.get(`/wallpaper/all`);
        } catch (error) {
            console.error('API request error:', error);
            throw error;
        }
    },

    // 搜索壁纸
    async searchWallpapers(params = {}) {
        try {
            const queryParams = new URLSearchParams();
            
            // 添加查询参数
            Object.keys(params).forEach(key => {
                if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
                    queryParams.append(key, params[key]);
                }
            });
            
            return await apiClient.get(`/wallpaper/search?${queryParams.toString()}`);
        } catch (error) {
            console.error('API request error:', error);
            throw error;
        }
    },

    // 获取轮播图壁纸
    async carouselWallpapers() {
        try {
            const response = await apiClient.get(`/wallpaper/carousel`);
            console.log('Raw carousel API response:', response);
            // 确保正确返回数据
            return response;
        } catch (error) {
            console.error('API request error:', error);
            throw error;
        }
    },

    // 上传壁纸
    async uploadWallpaper(formData, onUploadProgress) {
        try {
            const response = await apiClient.post(`/wallpaper/upload`, formData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                },
                onUploadProgress
            });
            return response.data;
        } catch (error) {
            console.error('API request error:', error);
            throw error;
        }
    }
};

export {wallpaperApi};