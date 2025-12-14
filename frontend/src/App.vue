<template>
  <Notification/>
  <div class="app-container">
    <div v-if="activeWallpaperUrl" class="background-layer"
         :style="{ backgroundImage: `url(${activeWallpaperUrl})` }"></div>
    <div v-if="lastWallpaperUrl" class="background-layer"
         :style="{ backgroundImage: `url(${lastWallpaperUrl})`, opacity: 0 }"></div>
    <Header/>
    <main class="main-content">
      <RouterView/>
    </main>
    <Footer/>
    <LoginDialog/>
    <RegisterDialog/>
  </div>
</template>

<script setup>
import {RouterView} from 'vue-router';
import Header from './components/layout/Header.vue';
import Footer from './components/layout/Footer.vue';
import LoginDialog from './components/auth/LoginDialog.vue';
import RegisterDialog from './components/auth/RegisterDialog.vue';
import Notification from './components/common/Notification.vue';
import {useAuthStore} from './stores/authStore';
import {useBackgroundStore} from './stores/backgroundStore.js';
import {onMounted} from 'vue';
import {storeToRefs} from "pinia";

const authStore = useAuthStore();
const backgroundStore = useBackgroundStore();
const {activeWallpaperUrl, lastWallpaperUrl} = storeToRefs(backgroundStore);

onMounted(() => {
  authStore.checkAuth();
  backgroundStore.initBackground();
});
</script>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  position: relative;
}

.background-layer {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  z-index: -1;
  transition: opacity 0.5s ease-in-out;
}

.main-content {
  flex: 1;
  padding: 1rem;
  position: relative;
  z-index: 1;
}
</style>