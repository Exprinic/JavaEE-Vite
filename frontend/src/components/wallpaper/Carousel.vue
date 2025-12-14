<template>
  <div class="carousel-container">
    <div class="carousel-wrapper" :style="{ transform: `translateX(-${currentIndex * 100}%)` }">
      <div v-for="(image, index) in images" :key="index" class="carousel-slide">
        <img :src="getImageUrl(image.imageUrl)" :alt="image.name" />
        <div class="overlay">
          <div class="image-info">
            <h3 class="image-name">{{ image.name }}</h3>
            <p class="image-artist">by {{ image.artist }}</p>
          </div>
        </div>
      </div>
    </div>
    <button class="carousel-control prev" @click="prev">&#10094;</button>
    <button class="carousel-control next" @click="next">&#10095;</button>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { getImageUrl } from '../../utils/image.js';
import { useWallpaperStore } from '../../stores/wallpaperStore';

const wallpaperStore = useWallpaperStore();
const images = ref([]);
const currentIndex = ref(0);
let intervalId = null;

onMounted(async () => {
  try {
    images.value = await wallpaperStore.fetchCarouselWallpapers();
    startAutoPlay();
  } catch (error) {
    console.error('Failed to fetch wallpapers:', error);
  }
});

const next = () => {
  currentIndex.value = (currentIndex.value + 1) % images.length;
};

const prev = () => {
  currentIndex.value = (currentIndex.value - 1 + images.length) % images.length;
};

const startAutoPlay = () => {
  intervalId = setInterval(next, 3000);
};

const stopAutoPlay = () => {
  clearInterval(intervalId);
};

onMounted(() => {
  startAutoPlay();
});

onUnmounted(() => {
  stopAutoPlay();
});
</script>

<style scoped>
.carousel-container {
  position: relative;
  width: 90%;
  max-width: 900px;
  margin: 1rem auto;
  overflow: hidden;
  border-radius: 15px;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  background-color: rgba(255, 255, 255, 0.1);
  transition: box-shadow 0.3s ease;
}

.carousel-wrapper {
  display: flex;
  height: 100%;
  transition: transform 0.5s ease;
}

.carousel-slide {
  flex: 0 0 100%;
  position: relative;
}

.carousel-slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
  color: var(--text-primary);
  padding: 1.5rem 1rem;
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.carousel-slide:hover .overlay {
  opacity: 1;
  transform: translateY(0);
}

.image-name {
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0 0 0.25rem 0;
}

.image-artist {
  font-size: 0.8rem;
  margin: 0;
  opacity: 0.8;
}

.carousel-control {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: rgba(0, 0, 0, 0.3);
  color: white;
  border: none;
  padding: 0.8rem;
  cursor: pointer;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1.2rem;
  backdrop-filter: blur(5px);
  -webkit-backdrop-filter: blur(5px);
  transition: all 0.3s ease;
}

.carousel-control.prev {
  left: 0.8rem;
}

.carousel-control.next {
  right: 0.8rem;
}

@media (min-width: 768px) {
  .carousel-container {
    width: 60%;
  }

  .carousel-control {
    padding: 1rem;
    width: 50px;
    height: 50px;
    font-size: 1.5rem;
  }

  .carousel-control.prev {
    left: 1rem;
  }

  .carousel-control.next {
    right: 1rem;
  }
}
</style>