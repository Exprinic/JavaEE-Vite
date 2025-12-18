<template>
  <div class="carousel-container">
    <div class="carousel-wrapper">
      <div 
        class="carousel" 
        ref="carouselRef"
        :style="{ transform: `translateX(-${currentIndex * 100}%)` }"
      >
        <div
          v-for="(item, index) in displayedItems"
          :key="index"
          class="carousel-item"
          :class="{ active: index === currentIndex }"
          @mouseenter="showCaption(index)"
          @mouseleave="hideCaption()"
        >
          <img :src="getWallpaperImageUrl(item)" :alt="item.title" class="carousel-image"/>
          <div class="carousel-caption" :class="{ 'show': showCaptionIndex === index }">
            <h3>{{ item.title }}</h3>
            <p>By {{ item.artist || 'Unknown' }}</p>
          </div>
        </div>
      </div>
<!--      <button -->
<!--        v-if="displayedItems.length > 1"-->
<!--        class="carousel-control prev" -->
<!--        @click="prevSlide"-->
<!--        :disabled="displayedItems.length <= 1"-->
<!--      >-->
<!--        &#10094;-->
<!--      </button>-->
<!--      <button -->
<!--        v-if="displayedItems.length > 1"-->
<!--        class="carousel-control next" -->
<!--        @click="nextSlide"-->
<!--        :disabled="displayedItems.length <= 1"-->
<!--      >-->
<!--        &#10095;-->
<!--      </button>-->
    </div>
    <div 
      v-if="displayedItems.length > 1" 
      class="carousel-indicators"
    >
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
import { ref, onMounted, onUnmounted, watch } from 'vue';
import { useWallpaperStore } from '@/stores/wallpaperStore.js';

const wallpaperStore = useWallpaperStore();
const { fetchCarouselWallpapers, carouselWallpapers } = wallpaperStore;

const carouselRef = ref(null);
const currentIndex = ref(0);
const displayedItems = ref([]);
const showCaptionIndex = ref(-1); // 用于控制哪个caption显示

let intervalId;

const nextSlide = () => {
  if (displayedItems.value.length <= 1) return;
  currentIndex.value = (currentIndex.value + 1) % displayedItems.value.length;
};

const prevSlide = () => {
  if (displayedItems.value.length <= 1) return;
  currentIndex.value =
    (currentIndex.value - 1 + displayedItems.value.length) % displayedItems.value.length;
};

const goToSlide = (index) => {
  if (displayedItems.value.length <= 1) return;
  currentIndex.value = index;
};

const showCaption = (index) => {
  showCaptionIndex.value = index;
};

const hideCaption = () => {
  showCaptionIndex.value = -1;
};

const startAutoPlay = () => {
  // 先清除已存在的定时器
  if (intervalId) {
    clearInterval(intervalId);
  }
  
  // 只有在有多张图片时才启动自动播放
  if (displayedItems.value.length > 1) {
    intervalId = setInterval(() => {
      nextSlide();
    }, 5000);
  }
};

const stopAutoPlay = () => {
  if (intervalId) {
    clearInterval(intervalId);
    intervalId = null;
  }
};

// 获取壁纸图片URL的函数
const getWallpaperImageUrl = (wallpaper) => {
  // 优先使用fullUrl，其次是mediumUrl，最后是thumbnailUrl
  return wallpaper.fullUrl || wallpaper.mediumUrl || wallpaper.thumbnailUrl || '/wallpapers/default.jpg';
};

// 监听 displayedItems 的变化，重启自动播放
watch(displayedItems, (newVal) => {
  stopAutoPlay();
  currentIndex.value = 0; // 重置索引
  startAutoPlay();
});

// 监听轮播图壁纸的变化
watch(carouselWallpapers, (newVal) => {
  displayedItems.value = Array.isArray(newVal) ? newVal : [];
}, { immediate: true });

onMounted(async () => {
  try {
    // 使用缓存机制获取轮播图壁纸
    const wallpapers = await fetchCarouselWallpapers(false); // 不强制刷新
    if (!displayedItems.value.length && Array.isArray(wallpapers)) {
      displayedItems.value = wallpapers;
    }
    startAutoPlay();
  } catch (error) {
    console.error('Failed to load carousel wallpapers:', error);
    displayedItems.value = [];
  }
});

onUnmounted(() => {
  stopAutoPlay();
});
</script>

<style scoped>
.carousel-container {
  position: relative;
  width: 100%;
  max-width: 1400px; /* 设置最大宽度 */
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
  opacity: 0;
  transform: translateY(100%);
  transition: all 0.3s ease;
}

.carousel-caption.show {
  opacity: 1;
  transform: translateY(0);
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
  width: 50px;
  height: 50px;
  border-radius: 50%;
  font-size: 1.5rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  opacity: 0; /* 默认隐藏 */
}

/* 悬停时显示控制按钮 */
.carousel-wrapper:hover .carousel-control {
  opacity: 1;
}

.carousel-control:hover:not(:disabled) {
  background: rgba(0, 0, 0, 0.8);
  transform: translateY(-50%) scale(1.1);
}

.carousel-control:disabled {
  opacity: 0.5;
  cursor: not-allowed;
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
  border: 2px solid transparent;
}

.indicator.active {
  background: white;
  border-color: #007bff;
  transform: scale(1.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .carousel-container {
    max-width: 100%;
  }
  
  .carousel-control {
    width: 40px;
    height: 40px;
    font-size: 1.2rem;
  }
  
  .carousel-image {
    height: 300px;
  }
  
  .carousel-control.prev {
    left: 0.5rem;
  }
  
  .carousel-control.next {
    right: 0.5rem;
  }
  
  .carousel-caption {
    padding: 1rem;
  }
  
  .carousel-caption h3 {
    font-size: 1.2rem;
  }
  
  .carousel-caption p {
    font-size: 0.9rem;
  }
}

/* 更好的响应式支持 */
@media (max-width: 480px) {
  .carousel-image {
    height: 250px;
  }
  
  .carousel-control {
    width: 35px;
    height: 35px;
    font-size: 1rem;
  }
  
  .carousel-control.prev {
    left: 0.25rem;
  }
  
  .carousel-control.next {
    right: 0.25rem;
  }
  
  .carousel-caption h3 {
    font-size: 1rem;
  }
  
  .carousel-caption p {
    font-size: 0.8rem;
  }
}

/* 大屏幕优化 */
@media (min-width: 1600px) {
  .carousel-container {
    max-width: 1600px;
  }
  
  .carousel-image {
    height: 600px;
  }
  
  .carousel-control {
    width: 60px;
    height: 60px;
    font-size: 2rem;
  }
  
  .carousel-control.prev {
    left: 2rem;
  }
  
  .carousel-control.next {
    right: 2rem;
  }
}
</style>