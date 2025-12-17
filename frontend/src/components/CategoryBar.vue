<template>
  <div class="filter-bar-container">
    <div class="filter-buttons">
      <button
          v-for="category in categorys"
          :key="category"
          :class="{ active: activeCategory === category }"
          @click="setCategory(category)"
      >
        {{ category }}
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  categorys: {
    type: Array,
    required: true,
  },
  activeCategory: {
    type: String,
    required: true,
  },
});

const emit = defineEmits(['update:activeCategory']);

const setCategory = (category) => {
  emit('update:activeCategory', category);
};
</script>

<style scoped>
.filter-bar-container {
  display: flex;
  justify-content: center;
  padding: 1.5rem 0;
  background-color: transparent;
}

.filter-buttons {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 2rem;
  max-width: 800px;
  width: 100%;
  transition: max-width 0.3s ease;
}

@media (min-width: 1200px) {
  .filter-buttons {
    max-width: 1000px;
  }
}

.filter-buttons button {
  padding: 0.75rem 0;
  border: none;
  border-radius: 0;
  border-bottom: 2px solid transparent;
  background-color: transparent;
  color: var(--filter-text-color);
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-buttons button.active {
  color: var(--filter-text-color-active);
  border-bottom-color: var(--filter-text-color-active);
}

.filter-buttons button:hover:not(.active) {
  color: var(--filter-text-color-hover);
  border-bottom-color: var(--filter-text-color-hover);
}
</style>