<template>
  <div class="explore-container">
    <div class="search-bar-container">
      <SearchBar 
        :search-term="searchTermValue"
        @update:searchTerm="setSearchTerm"
        @search="performSearch"
      />
    </div>
    <div class="content-area">
      <CategoryBar
        :categorys="categories"
        :active-category="selectedCategoryValue"
        @update:activeCategory="setCategory"
      />
      <div v-if="loading" class="loading">Loading wallpapers...</div>
      <div v-else-if="error" class="error">Error: {{ error }}</div>
      <div v-else-if="!wallpapers || !Array.isArray(wallpapers) || wallpapers.length === 0" class="no-results">
        <p>No wallpapers found. Try a different filter or search term.</p>
      </div>
      <WallpaperGrid v-else :wallpapers="wallpapers || []"/>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useWallpaperStore } from '../stores/wallpaperStore.js';
import CategoryBar from '../components/CategoryBar.vue';
import WallpaperGrid from '../components/wallpaper/WallpaperGrid.vue';
import SearchBar from '../components/SearchBar.vue';

const route = useRoute();
const router = useRouter();
const wallpaperStore = useWallpaperStore();

// 使用 ref 包装 store 的响应式属性
const searchTermValue = ref(wallpaperStore.searchTerm || '');
const selectedCategoryValue = ref(wallpaperStore.selectedCategory || 'ALL');

// 监听 store 的变化
watch(() => wallpaperStore.searchTerm, (newVal) => {
  searchTermValue.value = newVal || '';
});

watch(() => wallpaperStore.selectedCategory, (newVal) => {
  selectedCategoryValue.value = newVal || 'ALL';
});

const {
  categories,
  wallpapers,
  loading,
  error,
  searchWallpapers,
  setSearchTerm,
  setCategory,
  init
} = wallpaperStore;

// 处理搜索
const performSearch = (term) => {
  // 更新URL但不触发路由监听器的循环
  if (term && term.trim()) {
    router.push({ name: 'explore', query: { q: term.trim() } });
  } else {
    router.push({ name: 'explore' });
  }
  
  searchWallpapers(term, selectedCategoryValue.value);
};

// 监听路由变化
watch(
  () => route.query.q,
  (newQuery) => {
    searchTermValue.value = newQuery || '';
    setSearchTerm(newQuery || '');
    if (newQuery) {
      searchWallpapers(newQuery, selectedCategoryValue.value);
    } else {
      init();
    }
  }
);

// 监听分类变化
watch(selectedCategoryValue, (newCategory) => {
  // 当分类改变时，执行带分类参数的搜索
  const currentSearchTerm = searchTermValue.value;
  setCategory(newCategory);
  searchWallpapers(currentSearchTerm, newCategory);
});

onMounted(() => {
  const query = route.query.q;
  searchTermValue.value = query || '';
  setSearchTerm(query || '');
  if (query) {
    searchWallpapers(query, selectedCategoryValue.value);
  } else {
    init(); // 这里会使用缓存机制
  }
});
</script>

<style scoped>
.explore-container {
  padding-top: 20px;
}

.content-area {
  margin-top: 20px;
}

.loading, .error {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
}

.error {
  color: red;
}
</style>