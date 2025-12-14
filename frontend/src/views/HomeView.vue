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
import {ref} from 'vue';
import {useRouter} from 'vue-router';

const router = useRouter();
const searchQuery = ref('');

const search = () => {
  if (searchQuery.value.trim()) {
    router.push({name: 'explore', query: {q: searchQuery.value.trim()}});
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