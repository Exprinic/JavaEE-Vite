<template>
  <div class="wallpaper-detail-container">
    <button 
      v-if="hasNavigation" 
      :class="{ 'invisible': !canNavigatePrevious }" 
      @click="navigateToPrevious" 
      class="nav-button prev-button">
      &lt;
    </button>
    
    <div v-if="currentWallpaper" class="wallpaper-detail-card">
      <div class="image-container">
        <img 
          :src="getImageUrl(currentWallpaper.fullUrl || currentWallpaper.mediumUrl || currentWallpaper.thumbnailUrl || '')"
          :alt="currentWallpaper.title || 'Wallpaper'" 
          class="wallpaper-image-full"
          loading="lazy">
      </div>
      <div class="details-container">
        <h2>{{ currentWallpaper.title || 'Untitled' }}</h2>
        <p class="artist">by Unknown</p>
        <!-- 简化标签显示，避免访问不存在的属性 -->
        <div class="tags" v-if="currentWallpaper.tags && currentWallpaper.tags.length > 0">
          <span v-for="(tag, index) in currentWallpaper.tags" :key="index" class="tag">#{{ tag }}</span>
        </div>
        <p class="description">{{ currentWallpaper.description || 'No description available' }}</p>
        <div class="button-group">
          <button class="action-btn set-wallpaper-btn" @click="previewAsWallpaper">
            <i class="fas fa-image"></i>
            Preview as Wallpaper
          </button>
          <button class="action-btn set-wallpaper-permanent-btn" @click="setAsWallpaper">
            <i class="fas fa-save"></i>
            Set as Wallpaper
          </button>
          <button class="action-btn download-btn" @click="downloadWallpaper">
            <i class="fas fa-download"></i>
            Download
          </button>
        </div>
      </div>
    </div>
    
    <div v-else-if="loading" class="loading-spinner">
      <p>Loading...</p>
    </div>
    
    <div v-else class="not-found">
      <p>Wallpaper not found.</p>
    </div>
    
    <button 
      v-if="hasNavigation" 
      :class="{ 'invisible': !canNavigateNext }" 
      @click="navigateToNext" 
      class="nav-button next-button">
      >
    </button>
  </div>
</template>

<script setup>
import {computed, onMounted, onUnmounted, watch} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {useWallpaperStore} from '../stores/wallpaperStore';
import {useAuthStore} from '../stores/authStore';
import {useUiStore} from '../stores/uiStore';
import {useUserStore} from '../stores/userStore';
import {storeToRefs} from 'pinia';

import {getImageUrl} from "../utils/image.js";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const uiStore = useUiStore();
const userStore = useUserStore();
const wallpaperStore = useWallpaperStore();

// 不再使用storeToRefs，而是直接使用store的状态
const { loading } = storeToRefs(wallpaperStore);
const { isAuthenticated } = storeToRefs(authStore);

// 控制是否显示导航按钮
const hasNavigation = computed(() => {
  return wallpaperStore.allWallpapers && wallpaperStore.allWallpapers.length > 1;
});

// 创建本地响应式状态来存储当前壁纸
const currentWallpaper = computed(() => {
  // 查找当前壁纸，如果找不到则返回null
  const wallpapers = wallpaperStore.allWallpapers;
  const id = route.params.id;
  
  if (!id || !wallpapers) return null;
  
  // 查找具有匹配ID的壁纸
  const wallpaper = wallpapers.find(w => w && w.id == id);
  return wallpaper || null;
});

const currentWallpaperIndex = computed(() => {
  if (!currentWallpaper.value || !wallpaperStore.allWallpapers) return -1;
  return wallpaperStore.allWallpapers.findIndex(w => w && w.id === currentWallpaper.value.id);
});

const canNavigatePrevious = computed(() => {
  return hasNavigation.value && currentWallpaperIndex.value > 0;
});

const canNavigateNext = computed(() => {
  return hasNavigation.value && 
         wallpaperStore.allWallpapers && 
         currentWallpaper.value && 
         currentWallpaperIndex.value < wallpaperStore.allWallpapers.length - 1;
});

const navigateToNext = () => {
  if (canNavigateNext.value) {
    const nextWallpaperId = wallpaperStore.allWallpapers[currentWallpaperIndex.value + 1].id;
    router.push({name: 'wallpaper-detail', params: {id: nextWallpaperId}});
  }
};

const navigateToPrevious = () => {
  if (canNavigatePrevious.value) {
    const prevWallpaperId = wallpaperStore.allWallpapers[currentWallpaperIndex.value - 1].id;
    router.push({name: 'wallpaper-detail', params: {id: prevWallpaperId}});
  }
};

// 预览壁纸为背景（临时设置，不持久化，无需登录）
const previewAsWallpaper = async () => {
  try {
    // 使用适当的图像URL预览壁纸
    const imageUrl = currentWallpaper.value.fullUrl || 
                     currentWallpaper.value.mediumUrl || 
                     currentWallpaper.value.thumbnailUrl || 
                     '';
                     
    // 设置为临时背景（预览模式，不持久化）
    wallpaperStore.setBackgroundPreview(getImageUrl(imageUrl));
    
    // 创建临时通知
    const { useNotificationStore } = await import('../stores/notificationStore.js');
    const tempNotificationStore = useNotificationStore();
    tempNotificationStore.addNotification({ 
      message: 'Previewing wallpaper! The background will reset when you leave this page.', 
      type: 'success',
      duration: 3000 
    });
  } catch (error) {
    // 创建临时通知
    const { useNotificationStore } = await import('../stores/notificationStore.js');
    const tempNotificationStore = useNotificationStore();
    tempNotificationStore.addNotification({ 
      message: 'Failed to preview wallpaper.', 
      type: 'error',
      duration: 3000 
    });
  }
};

// 永久设置为壁纸背景（持久化，需要登录）
const setAsWallpaper = async () => {
  if (!isAuthenticated.value) {
    // 创建临时通知
    const { useNotificationStore } = await import('../stores/notificationStore.js');
    const tempNotificationStore = useNotificationStore();
    tempNotificationStore.addNotification({ 
      message: 'Please log in first', 
      type: 'info',
      duration: 3000 
    });
    return;
  }

  try {
    // 使用适当的图像URL设置壁纸
    const imageUrl = currentWallpaper.value.fullUrl || 
                     currentWallpaper.value.mediumUrl || 
                     currentWallpaper.value.thumbnailUrl || 
                     '';
                     
    // 永久设置壁纸（持久化）
    await wallpaperStore.setActiveWallpaper(getImageUrl(imageUrl));
    
    // 创建临时通知
    const { useNotificationStore } = await import('../stores/notificationStore.js');
    const tempNotificationStore = useNotificationStore();
    tempNotificationStore.addNotification({ 
      message: 'Wallpaper set successfully! This will be your background next time you visit.', 
      type: 'success',
      duration: 3000
    });
  } catch (error) {
    // 创建临时通知
    const { useNotificationStore } = await import('../stores/notificationStore.js');
    const tempNotificationStore = useNotificationStore();
    tempNotificationStore.addNotification({ 
      message: 'Failed to set wallpaper.', 
      type: 'error',
      duration: 3000
    });
  }
};

const downloadWallpaper = async () => {
  if (!isAuthenticated.value) {
    // 创建临时通知
    const { useNotificationStore } = await import('../stores/notificationStore.js');
    const tempNotificationStore = useNotificationStore();
    tempNotificationStore.addNotification({ 
      message: 'Please log in first', 
      type: 'info',
      duration: 3000 
    });
    return;
  }

  if (currentWallpaper.value) {
    try {
      const link = document.createElement('a');
      // 使用适当的图像URL下载壁纸
      const imageUrl = currentWallpaper.value.fullUrl || 
                       currentWallpaper.value.mediumUrl || 
                       currentWallpaper.value.thumbnailUrl || 
                       '';
                       
      link.href = getImageUrl(imageUrl);
      link.download = `wallpaper-${currentWallpaper.value.id}.jpg`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      
      // 创建临时通知
      const { useNotificationStore } = await import('../stores/notificationStore.js');
      const tempNotificationStore = useNotificationStore();
      tempNotificationStore.addNotification({ 
        message: 'Download started!', 
        type: 'success',
        duration: 3000 
      });
    } catch (error) {
      console.error('Download failed:', error);
      
      // 创建临时通知
      const { useNotificationStore } = await import('../stores/notificationStore.js');
      const tempNotificationStore = useNotificationStore();
      tempNotificationStore.addNotification({ 
        message: 'Download failed', 
        type: 'error',
        duration: 3000 
      });
    }
  }
};

// 页面加载时的处理
const fetchData = async () => {
  const id = route.params.id;
  if (id) {
    try {
      // 尝试从现有列表中查找壁纸，如果找不到则从服务器获取
      await wallpaperStore.fetchWallpaper(id, false); // 不强制刷新
    } catch (error) {
      console.error('Failed to fetch wallpaper:', error);
    }
  }
};

onMounted(() => {
  fetchData();
});

watch(() => route.params.id, (newId, oldId) => {
  if (newId && newId !== oldId) {
    fetchData();
  }
});

// 在组件卸载时恢复活动壁纸
onUnmounted(() => {
  // 恢复活动壁纸，清除临时预览
  wallpaperStore.restoreActiveWallpaper();
});
</script>

<style scoped>
.wallpaper-detail-container {
  padding: 4rem 2rem;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: calc(100vh - 120px);
  gap: 2rem;
  position: relative;
}

.wallpaper-detail-card {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 3rem;
  max-width: 1400px;
  width: 100%;
  background: rgba(36, 36, 36, 0.5);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.image-container {
  position: relative;
  background-color: #000;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.wallpaper-image-full {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.details-container {
  padding: 3rem;
  display: flex;
  flex-direction: column;
}

.details-container h2 {
  font-size: 2.8rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  color: var(--accent-primary);
}

.artist {
  font-size: 1.2rem;
  color: var(--text-muted);
  margin-bottom: 1.5rem;
}

.tags {
  margin-bottom: 1.5rem;
}

.tag {
  display: inline-block;
  background-color: var(--tag-bg-color-active);
  color: var(--tag-text-color);
  padding: 0.5rem 1rem;
  border-radius: 20px;
  margin-right: 0.5rem;
  font-size: 0.9rem;
}

.description {
  font-size: 1rem;
  line-height: 1.6;
  color: var(--text-muted);
  flex-grow: 1;
}

.download-btn i {
  margin-right: 0.5rem;
}

.button-group {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.action-btn {
  padding: 0.9rem 1.8rem;
  border: 2px solid transparent;
  border-radius: 50px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  letter-spacing: 0.5px;
}

.action-btn i {
  margin-right: 0.8rem;
  font-size: 1.1rem;
}

.set-wallpaper-btn {
  background-color: var(--accent-primary);
  color: var(--text-dark);
  border-color: var(--accent-primary);
  box-shadow: 0 4px 15px rgba(128, 133, 142, 0.3);
}

.set-wallpaper-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(128, 133, 142, 0.4);
}

.download-btn {
  background-color: transparent;
  color: var(--text-muted);
  border-color: var(--border-color);
}

.download-btn:hover {
  background-color: var(--border-color);
  color: var(--dropdown-item-bg-hover);
}

.nav-button {
  background: rgba(36, 36, 36, 0.5);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
  font-size: 2rem;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;
  position: sticky;
  top: 50%;
}

.nav-button:hover {
  background: rgba(36, 36, 36, 0.8);
  transform: scale(1.1);
}

.invisible {
  visibility: hidden;
}

.loading-spinner, .not-found {
  text-align: center;
  padding: 5rem;
  font-size: 1.5rem;
  color: var(--text-muted);
}

@media (max-width: 940px) {
  .wallpaper-detail-container {
    padding: 2rem 1rem;
    gap: 1rem;
    align-items: flex-start;
  }

  .wallpaper-detail-card {
    grid-template-columns: 1fr;
    min-width: 500px;
    gap: 1rem;
  }

  .image-container {
    max-height: 60vh;
    border-radius: 20px;
  }

  .details-container {
    padding: 1.5rem;
  }

  .details-container h2 {
    font-size: 2rem;
  }

  .button-group {
    flex-direction: column;
  }

  .nav-button {
    position: absolute;
    top: 35vh;
    transform: translateY(-50%);
  }

  .prev-button {
    left: 0.5rem;
  }

  .next-button {
    right: 0.5rem;
  }
}
</style>