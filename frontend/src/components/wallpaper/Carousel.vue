<template>
  <div class="carousel-container">
    <div class="carousel-wrapper">
      <div class="carousel" ref="carouselRef">
        <div
            v-for="(item, index) in displayedItems"
            :key="index"
            class="carousel-item"
            :class="{ active: index === currentIndex }"
        >
          <img :src="getWallpaperImageUrl(item)" :alt="item.title" class="carousel-image"/>
          <div class="carousel-caption">
            <h3>{{ item.title }}</h3>
            <p>{{ item.description }}</p>
          </div>
        </div>
      </div>
      <button class="carousel-control prev" @click="prevSlide">&#10094;</button>
      <button class="carousel-control next" @click="nextSlide">&#10095;</button>
    </div>
    <div class="carousel-indicators">
      <span
          v-for="(item, index) in displayedItems"
          :key="index"
          class="indicator"
          :class="{ active: index === currentIndex }"
          @click="goToSlide(index)"
      ></span>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, onUnmounted} from 'vue';
import {useWallpaperStore} from '../../stores/wallpaperStore';

const wallpaperStore = useWallpaperStore();
const { fetchCarouselWallpapers } = wallpaperStore;

const carouselRef = ref(null);
const currentIndex = ref(0);
const displayedItems = ref([]);

let intervalId;

const nextSlide = () => {
  currentIndex.value = (currentIndex.value + 1) % displayedItems.value.length;
};

const prevSlide = () => {
  currentIndex.value =
      (currentIndex.value - 1 + displayedItems.value.length) % displayedItems.value.length;
};

const goToSlide = (index) => {
  currentIndex.value = index;
};

const startAutoPlay = () => {
  intervalId = setInterval(() => {
    nextSlide();
  }, 5000); // Change slide every 5 seconds
};

const stopAutoPlay = () => {
  if (intervalId) {
    clearInterval(intervalId);
  }
};

// 获取壁纸图片URL的函数
const getWallpaperImageUrl = (wallpaper) => {
  // 优先使用fullUrl，其次是mediumUrl，最后是thumbnailUrl
  return wallpaper.fullUrl || wallpaper.mediumUrl || wallpaper.thumbnailUrl || '/wallpapers/default.jpg';
};

onMounted(async () => {
  // Fetch carousel wallpapers from the store
  const wallpapers = await fetchCarouselWallpapers();
  displayedItems.value = wallpapers;
  startAutoPlay();
});

onUnmounted(() => {
  stopAutoPlay();
});
</script>

<style scoped>
.carousel-container {
  position: relative;
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 0;
}

.carousel-wrapper {
  position: relative;
  overflow: hidden;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.carousel {
  display: flex;
  transition: transform 0.5s ease-in-out;
}

.carousel-item {
  min-width: 100%;
  position: relative;
}

.carousel-image {
  width: 100%;
  height: 500px;
  object-fit: cover;
}

.carousel-caption {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
  color: white;
  padding: 2rem;
}

.carousel-caption h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.5rem;
}

.carousel-caption p {
  margin: 0;
  font-size: 1rem;
}

.carousel-control {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  padding: 1rem;
  font-size: 1.5rem;
  cursor: pointer;
  transition: background 0.3s ease;
}

.carousel-control:hover {
  background: rgba(0, 0, 0, 0.8);
}

.carousel-control.prev {
  left: 1rem;
}

.carousel-control.next {
  right: 1rem;
}

.carousel-indicators {
  display: flex;
  justify-content: center;
  margin-top: 1rem;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  margin: 0 0.5rem;
  cursor: pointer;
  transition: background 0.3s ease;
}

.indicator.active {
  background: white;
}
</style>