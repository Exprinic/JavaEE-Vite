<template>
  <div class="explore-container">
    <div class="search-bar-container">
      <div class="search-bar">
        <i class="search-icon"></i>
        <input
            type="text"
            :value="searchTerm"
            @input="searchWallpapers($event.target.value)"
            placeholder="Search wallpapers, tags, artists..."
        />
      </div>
    </div>
    <div class="content-area">
      <CategoryBar
          :categorys="categories"
          :active-category="selectedCategory"
          @update:activeCategory="setCategory"
      />
      <TagBar
          :tags="tags"
          :selected-tag="selectedTag"
          @update:selectedTag="setTag"
      />
      <WallpaperGrid :wallpapers="wallpapers"/>
    </div>
  </div>
</template>

<script setup>
import {onMounted} from 'vue';
import {useRoute} from 'vue-router';
import {useWallpaperStore} from '../stores/wallpaperStore.js';
import CategoryBar from '../components/CategoryBar.vue';
import TagBar from '../components/TagBar.vue';
import WallpaperGrid from '../components/wallpaper/WallpaperGrid.vue';

const route = useRoute();
const {
  searchTerm,
  selectedCategory,
  selectedTag,
  categories,
  tags,
  wallpapers,
  searchWallpapers,
  setCategory,
  setTag,
  init
} = useWallpaperStore();

onMounted(() => {
  const query = route.query.q;
  if (query) {
    searchWallpapers(query);
  } else {
    init();
  }
});
</script>

<style scoped>
.explore-container {
  padding-top: 20px;
}

.search-bar-container {
  position: relative;
  width: 100%;
  max-width: 600px;
  margin: 1rem auto;
  border-radius: 30px;
  transition: box-shadow 0.3s ease;
}


.search-bar {
  position: relative;
}

.search-icon {
  position: absolute;
  left: 1.5rem;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="%23888" class="bi bi-search" viewBox="0 0 16 16"><path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"/></svg>');
}

.search-bar input {
  width: 100%;
  padding: 0.8rem 1.5rem 0.8rem 3.5rem; /* Adjust padding for icon */
  border-radius: 30px;
  border: 1px solid var(--border-color);
  background-color: var(--bg-card);
  color: var(--text-dark);
  font-size: 0.9rem;
  transition: border-color 0.3s ease;
}

.search-bar input:focus {
  outline: none;
  box-shadow: 0 0 20px var(--border-input-shadow-color);
  /* Add blur effect */
}

.content-area {
  margin-top: 20px;
}
</style>