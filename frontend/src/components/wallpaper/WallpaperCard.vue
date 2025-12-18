<template>
  <div class="wallpaper-card">
    <img 
      :src="getImageUrl(wallpaper.thumbnailUrl)" 
      :alt="wallpaper.title" 
      class="wallpaper-image" 
      @click="openDetailView"
      loading="lazy"
    />
    <div class="overlay">
      <div class="wallpaper-info">
        <h3 class="wallpaper-name">{{ wallpaper.title }}</h3>
        <p class="wallpaper-artist">by {{ wallpaper.artist || 'Unknown' }}</p>
      </div>
      <div class="card-actions">
        <button class="action-button" @click.stop="setAsWallpaper">
          <i class="fas fa-cog"></i>
        </button>
        <button class="action-button download-button" @click.stop="downloadWallpaper">
          <i class="fas fa-download"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {useRouter} from 'vue-router'
import {useAuthStore} from '@/stores/authStore.js'
import {useWallpaperStore} from '@/stores/wallpaperStore.js'
import {useNotificationStore} from '@/stores/notificationStore.js';
import {getImageUrl} from '@/utils/image.js'
import {useUiStore} from "@/stores/uiStore.js";
import {storeToRefs} from "pinia";

const props = defineProps({
  wallpaper: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const authStore = useAuthStore()
const wallpaperStore = useWallpaperStore()
const notificationStore = useNotificationStore();
const uiStore = useUiStore();

const {isAuthenticated} = storeToRefs(authStore);

const openDetailView = () => {
  // 确保id存在再跳转
  if (props.wallpaper && props.wallpaper.id) {
    router.push({name: 'wallpaper-detail', params: {id: props.wallpaper.id}})
  }
}

const downloadWallpaper = () => {
  if (!isAuthenticated.value) {
    notificationStore.addNotification({message: 'Please log in to download the image', type: 'info'});
  } else if (props.wallpaper) {
    const link = document.createElement('a');
    const imageUrl = props.wallpaper.fullUrl || props.wallpaper.mediumUrl || props.wallpaper.thumbnailUrl;
    const fileName = props.wallpaper.title || 'wallpaper';
    link.href = getImageUrl(imageUrl);
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    notificationStore.addNotification({message: 'Download started!', type: 'success'});
  }
};

const setAsWallpaper = async () => {
  if (!isAuthenticated.value) {
    notificationStore.addNotification({message: 'Please log in to set the wallpaper', type: 'info'});
  } else if (props.wallpaper) {
    try {
      // 使用适当的图像URL设置壁纸
      const imageUrl = props.wallpaper.fullUrl || props.wallpaper.mediumUrl || props.wallpaper.thumbnailUrl;
      await wallpaperStore.setActiveWallpaper(getImageUrl(imageUrl));
      notificationStore.addNotification({message: 'Wallpaper set successfully!', type: 'success'});
    } catch (error) {
      notificationStore.addNotification({message: 'Failed to set wallpaper, please try again later', type: 'error'});
    }
  }
};
</script>

<style scoped>
.wallpaper-card {
  position: relative;
  border-radius: 15px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.wallpaper-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.2);
}

.wallpaper-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
  color: var(--text-primary);
  padding: 1.5rem 1rem;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.wallpaper-card:hover .overlay {
  opacity: 1;
  transform: translateY(0);
}

.wallpaper-name {
  font-size: 1.2rem;
  font-weight: 600;
  margin: 0 0 0.25rem 0;
}

.wallpaper-artist {
  font-size: 0.9rem;
  margin: 0;
  opacity: 0.8;
}

.card-actions {
  display: flex;
}

.action-button {
  background: var(--bg-overlay-light);
  border: none;
  color: var(--text-primary);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-left: 10px;
}

.action-button:hover {
  background: rgba(255, 255, 255, 0.4);
}
</style>