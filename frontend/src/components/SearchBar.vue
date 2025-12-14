<template>
  <div class="search-container">
    <div class="search-input-wrapper" :class="{ 'has-content': localSearchQuery.trim() !== '' }" id="searchWrapper">
      <i class="fas fa-search search-icon" @click="performSearch"></i>
      <input
          type="text"
          class="search-input"
          id="searchInput"
          placeholder="Search..."
          v-model="localSearchQuery"
          @keyup.enter="performSearch"
      />
    </div>
  </div>
</template>

<script setup>
import {ref, watch} from 'vue';

const props = defineProps({
  searchQuery: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['update:searchQuery', 'search']);

const localSearchQuery = ref(props.searchQuery);

watch(() => props.searchQuery, (newValue) => {
  localSearchQuery.value = newValue;
});

watch(localSearchQuery, (newValue) => {
  emit('update:searchQuery', newValue);
});

const performSearch = () => {
  emit('search', localSearchQuery.value);
};
</script>

<style scoped>
/* Search box container */
.search-container {
  position: relative;
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
}

/* Search box wrapper - default state */
.search-input-wrapper {
  display: flex;
  align-items: center;
  background-color: #f8f9fa;
  border-radius: 50px;
  padding: 15px 25px;
  border: 2px solid transparent;
  transition: all 0.4s ease;
  position: relative;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

/* Search icon */
.search-icon {
  color: #7f8c8d;
  font-size: 1.2rem;
  margin-right: 15px;
  transition: color 0.3s ease;
  cursor: pointer; /* Add cursor pointer for clickability */
}

/* Search input box */
.search-input {
  width: 100%;
  border: none;
  outline: none;
  background-color: transparent;
  font-size: 1.1rem;
  color: #333;
  padding: 5px 0;
}

.search-input::placeholder {
  color: #95a5a6;
}

/* Focus effect */
.search-input-wrapper:focus-within {
  /*border: 2px solid #3498db;*/
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 0 0 4px rgba(52, 152, 219, 0.1),
  0 5px 20px rgba(52, 152, 219, 0.2);
  transform: translateY(-2px);
}

/* Blur effect layer */
.search-input-wrapper:focus-within::before {
  content: '';
  position: absolute;
  top: -3px;
  left: -3px;
  right: -3px;
  bottom: -3px;
  border-radius: 52px;
  background: linear-gradient(45deg, #3498db, #9b59b6, #e74c3c);
  z-index: -1;
  filter: blur(10px);
  opacity: 0.6;
  animation: gradient-border 3s ease infinite;
}

.search-input-wrapper:focus-within .search-icon {
  color: #3498db;
}

/* Gradient border animation */
@keyframes gradient-border {
  0%, 100% {
    opacity: 0.6;
  }
  50% {
    opacity: 0.9;
  }
}

/* Optional: Keep focus effect when input has content */
.search-input-wrapper.has-content {
  border-color: rgba(52, 152, 219, 0.5);
  background-color: rgba(255, 255, 255, 0.98);
}

@media (min-width: 992px) {
  .search-container {
    max-width: 800px;
  }
}
</style>