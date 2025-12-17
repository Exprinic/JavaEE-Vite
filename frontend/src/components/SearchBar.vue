<template>
  <div class="search-bar-container">
    <div class="search-bar">
      <i class="search-icon"></i>
      <input
          type="text"
          :value="searchTerm"
          @input="handleInput"
          @keyup.enter="performSearch"
          placeholder="Search wallpapers, tags (#tag), categories..."
      />
      <button class="search-button" @click="performSearch">
        <i class="search-button-icon"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
import {ref, watch} from 'vue';
import {useRouter} from 'vue-router';
import {useWallpaperStore} from '../stores/wallpaperStore.js';

const props = defineProps({
  searchTerm: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['update:searchTerm', 'search']);

const router = useRouter();
const wallpaperStore = useWallpaperStore();

const localSearchTerm = ref(props.searchTerm || '');

// 监听props变化
watch(() => props.searchTerm, (newVal) => {
  localSearchTerm.value = newVal || '';
});

const handleInput = (event) => {
  localSearchTerm.value = event.target.value;
  emit('update:searchTerm', localSearchTerm.value);
};

const performSearch = () => {
  // 如果有搜索词，则跳转到Explore页面
  if (localSearchTerm.value.trim()) {
    router.push({name: 'explore', query: {q: localSearchTerm.value.trim()}});
  } else {
    // 如果没有搜索词，跳转到Explore页面但不带查询参数
    router.push({name: 'explore'});
  }
  
  // 触发搜索事件
  emit('search', localSearchTerm.value);
};
</script>

<style scoped>
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
  display: flex;
  align-items: center;
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
  flex-grow: 1;
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

.search-button {
  position: absolute;
  right: 0.5rem;
  background: var(--accent-primary);
  border: none;
  border-radius: 50%;
  width: 2.5rem;
  height: 2.5rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s ease;
}

.search-button:hover {
  background: var(--accent-primary-hover);
}

.search-button-icon {
  width: 16px;
  height: 16px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="white" class="bi bi-search" viewBox="0 0 16 16"><path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"/></svg>');
}
</style>