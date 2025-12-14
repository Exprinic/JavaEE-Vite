<template>
  <div class="tag-bar-container" v-if="tags && tags.length > 1">
    <div class="tag-bar">
      <button
        v-for="tag in tags"
        :key="tag"
        :class="{ active: selectedTag === tag }"
        @click="setTag(tag)"
      >
        {{ tag }}
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  tags: {
    type: Array,
    required: true,
  },
  selectedTag: {
    type: String,
    required: true,
  },
});

const emit = defineEmits(['update:selectedTag']);

const setTag = (tag) => {
  emit('update:selectedTag', tag);
};
</script>

<style scoped>
.tag-bar-container {
  width: 100%;
  display: flex;
  justify-content: center;
  margin-bottom: 2rem;
}

.tag-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  justify-content: center;
  max-width: 800px;
  transition: max-width 0.3s ease;
}

@media (min-width: 1200px) {
  .tag-bar {
    max-width: 1000px;
  }
}

.tag-bar button {
  padding: 0.5rem 1rem;
  border: 1px solid var(--tag-border-color);
  border-radius: 20px;
  background-color: transparent;
  color: var(--tag-text-color);
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.tag-bar button.active {
  background-color: var(--tag-bg-color-active);
  border-color: var(--tag-bg-color-active);
  color: var(--tag-text-color-active);
}

.tag-bar button:hover:not(.active) {
  color: var(--tag-text-color-hover);
  border-color: var(--tag-text-color-hover);
}
</style>