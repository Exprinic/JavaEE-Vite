<template>
  <div class="wallpaper-detail-container">
    <button :class="{ 'invisible': !canNavigatePrevious }" @click="navigateToPrevious" class="nav-button prev-button">
      &lt;
    </button>
    <div v-if="wallpaper" class="wallpaper-detail-card">
      <div class="image-container">
        <img :src="wallpaper.imageUrl" :alt="wallpaper.name" class="wallpaper-image-full">
      </div>
      <div class="details-container">
        <h2>{{ wallpaper.name }}</h2>
        <p class="artist">by {{ wallpaper.artist }}</p>
        <div class="tags">
          <span v-for="tag in wallpaper.tags" :key="tag" class="tag">#{{ tag }}</span>
        </div>
        <p class="description">{{ wallpaper.description }}</p>
        <div class="button-group">
          <button class="action-btn set-wallpaper-btn" @click="setAsWallpaper">
            <i class="fas fa-image"></i>
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
    <button :class="{ 'invisible': !canNavigateNext }" @click="navigateToNext" class="nav-button next-button">&gt;
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

import {useNotificationStore} from '../stores/notificationStore';
import {getImageUrl} from "../utils/image.js";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();
const uiStore = useUiStore();
const userStore = useUserStore();
const wallpaperStore = useWallpaperStore();
const notificationStore = useNotificationStore();

const {wallpaper, loading, wallpapers} = storeToRefs(wallpaperStore);
const {isAuthenticated} = storeToRefs(authStore);

const currentWallpaperIndex = computed(() => {
  if (!wallpaper.value || !wallpapers.value) return -1;
  return wallpapers.value.findIndex(w => w.id === wallpaper.value.id);
});

const canNavigatePrevious = computed(() => {
  return currentWallpaperIndex.value > 0;
});

const canNavigateNext = computed(() => {
  return wallpapers.value && currentWallpaperIndex.value < wallpapers.value.length - 1;
});

const navigateToNext = () => {
  if (canNavigateNext.value) {
    const nextWallpaperId = wallpapers.value[currentWallpaperIndex.value + 1].id;
    router.push({name: 'wallpaper-detail', params: {id: nextWallpaperId}});
  }
};

const navigateToPrevious = () => {
  if (canNavigatePrevious.value) {
    const prevWallpaperId = wallpapers.value[currentWallpaperIndex.value - 1].id;
    router.push({name: 'wallpaper-detail', params: {id: prevWallpaperId}});
  }
};

const setAsWallpaper = async () => {
  if (!isAuthenticated.value) {
    notificationStore.addNotification({ message: 'Please log in first', type: 'info' });
    return;
  }

  try {
    await wallpaperStore.setActiveWallpaper(wallpaper.value.imageUrl);
    wallpaperWasSet = true; // Mark that the wallpaper was set
    notificationStore.addNotification({ message: 'Wallpaper set successfully!', type: 'success' });
  } catch (error) {
    notificationStore.addNotification({ message: 'Failed to set wallpaper.', type: 'error' });
  }
};

const downloadWallpaper = () => {
  if (!isAuthenticated.value) {
    notificationStore.addNotification({ message: 'Please log in first', type: 'info' });
    return;
  }

  if (wallpaper.value) {
    try {
      const link = document.createElement('a');
      link.href = wallpaper.value.imageUrl;
      link.download = `wallpaper-${wallpaper.value.id}.jpg`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      notificationStore.addNotification({ message: 'Download started!', type: 'success' });
    } catch (error) {
      console.error('Download failed:', error);
      notificationStore.addNotification({ message: 'Download failed', type: 'error' });
    }
  }
};

let wallpaperWasSet = false; // Flag to track if the user set the wallpaper

const fetchData = async () => {
  const id = route.params.id;
  await wallpaperStore.fetchWallpaper(id);
  if (wallpaper.value && wallpaper.value.imageUrl) {
    // For preview, directly call setBackground to avoid persistence
    wallpaperStore.setBackground(getImageUrl(wallpaper.value.imageUrl));
  }
};

onMounted(() => {
  wallpaperWasSet = false; // Reset flag on mount
  fetchData();
});

watch(() => route.params.id, (newId, oldId) => {
  if (newId && newId !== oldId) {
    wallpaperWasSet = false;
    fetchData();
  }
});

onUnmounted(() => {
  // Revert to the active wallpaper ONLY if a new one wasn't permanently set on this page
  if (!wallpaperWasSet) {
    if (wallpaperStore.activeWallpaperUrl) {
      wallpaperStore.setBackground(wallpaperStore.activeWallpaperUrl);
    } else {
      // If no active wallpaper is set (e.g., logged-out user), revert to default
      wallpaperStore.setDefaultBackground();
    }
  }
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

@media (max-width: 768px) {
  .wallpaper-detail-container {
    padding: 2rem 1rem;
    gap: 1rem;
    align-items: flex-start;
  }

  .wallpaper-detail-card {
    grid-template-columns: 1fr;
    gap: 1.5rem;
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