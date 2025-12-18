<template>
  <div class="home-container">
    <div class="hero-section">
      <h1>EAZ.Y</h1>
      <p>Discover and share the world's best wallpapers.</p>
    </div>
    <SearchBar @search="search"/>
    <Carousel/>
  </div>
</template>

<script setup>
import SearchBar from '../components/SearchBar.vue';
import Carousel from '../components/wallpaper/Carousel.vue';
import {ref, onMounted} from 'vue';
import {useRouter} from 'vue-router';
import { useWallpaperStore } from '../stores/wallpaperStore'; // 导入壁纸存储

const router = useRouter();
const searchQuery = ref('');

// 在主页加载时初始化壁纸存储（使用缓存）
const wallpaperStore = useWallpaperStore();
onMounted(() => {
  wallpaperStore.init(); // 初始化壁纸存储，会使用缓存机制
});

const search = (query) => {
  if (query && query.trim()) {
    router.push({name: 'explore', query: {q: query.trim()}});
  }
};
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.hero-section {
  text-align: center;
  padding: 4rem 2rem;
  color: var(--text-dark);
}

.hero-section h1 {
  font-size: 4rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.hero-section p {
  font-size: 1.25rem;
  color: var(--text-muted);
}
</style>