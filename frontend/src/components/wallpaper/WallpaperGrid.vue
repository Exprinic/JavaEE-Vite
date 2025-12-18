<template>
  <div class="wallpaper-grid-container">
    <div v-if="wallpapers && Array.isArray(wallpapers) && wallpapers.length > 0" class="wallpaper-grid">
      <WallpaperCard 
        v-for="(wallpaper, index) in wallpapers" 
        :key="wallpaper.id || index" 
        :wallpaper="wallpaper" />
    </div>
    <div v-else class="no-results">
      <p>No wallpapers found. Try a different filter or search term.</p>
    </div>
  </div>
</template>

<script setup>
import WallpaperCard from './WallpaperCard.vue';

defineProps({
  wallpapers: {
    type: Array,
    required: true,
    default: () => []
  },
});
</script>

<style scoped>
.wallpaper-grid-container {
  padding: 0 2rem 4rem;
}

.wallpaper-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
}

.no-results {
  text-align: center;
  padding: 5rem;
  font-size: 1.2rem;
  color: var(--text-muted);
}
</style>