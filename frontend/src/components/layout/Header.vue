<template>
  <header class="header">
    <div class="header-left">
      <router-link to="/" class="logo-container">
        <img src="/logo/logo.png" alt="EAZ Logo" class="logo-img"/>
        <div class="logo-text">
          <h1>EAZ</h1>
          <p>ExtraOrdinary Artistic Zone</p>
        </div>
      </router-link>
    </div>

    <div class="header-right">
      <nav class="nav-links">
        <router-link to="/explore">Explore</router-link>
        <router-link to="/upload">Upload</router-link>
        <router-link v-if="user && user.role === 'admin'" to="/admin">Admin</router-link>
      </nav>
      <div class="user-actions">
        <template v-if="isAuthenticated">
          <div class="user-menu" ref="userMenuRef">
            <button @click="isDropdownOpen = !isDropdownOpen" class="btn user-menu-trigger">
              <span>Welcome, {{ user.nickname }}</span>
              <i class="fas fa-chevron-down" style="margin-left: 8px; transition: transform 0.3s;"
                 :style="{ transform: isDropdownOpen ? 'rotate(180deg)' : 'rotate(0deg)' }"></i>
            </button>
            <div class="dropdown-menu" v-if="isDropdownOpen">
              <router-link to="/profile" @click="isDropdownOpen = false" class="dropdown-item">Profile</router-link>
              <a href="#" @click.prevent="handleSwitchAccount" class="dropdown-item">Switch Account</a>
              <a href="#" @click.prevent="handleLogout" class="dropdown-item">Logout</a>
            </div>
          </div>
        </template>
        <template v-else>
          <button @click="uiStore.showLogin" class="btn btn-primary">Login</button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import {useAuthStore} from '../../stores/authStore';
import {useUiStore} from '../../stores/uiStore';
import {useUserStore} from '../../stores/userStore'; // Import userStore
import {useRoute, useRouter} from 'vue-router';
import {storeToRefs} from 'pinia';
import {ref, onMounted, onUnmounted} from 'vue';

const authStore = useAuthStore();
const uiStore = useUiStore();
const userStore = useUserStore(); // Initialize userStore
const router = useRouter();

const {isAuthenticated} = storeToRefs(authStore);
const {user} = storeToRefs(userStore); // Get user from userStore
const route = useRoute()

const searchQuery = ref('');
const performSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({name: 'Explore', query: {q: searchQuery.value.trim()}});
  }
};

const isDropdownOpen = ref(false)
const userMenuRef = ref(null)

const handleSearchClick = (event) => {
  if (!isAuthenticated.value) {
    event.preventDefault()
    alert('Please log in to search')
    uiStore.showLogin()
  }
}

const Logout = async()=>{
  await authStore.logout({
    nickname: user.value.nickname,
    phone: user.value.phone,
  })
}



const handleLogout = () => {
  isDropdownOpen.value = false
  Logout()
  uiStore.hideDialogs()
}

const handleSwitchAccount = () => {
  isDropdownOpen.value = false
  Logout()
  setTimeout(() => {
    uiStore.showLogin()
  }, 100)
}

const handleClickOutside = (event) => {
  if (userMenuRef.value && !userMenuRef.value.contains(event.target)) {
    isDropdownOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  height: 80px;
  position: sticky;
  top: 0;
  z-index: 1000;
  width: 100%;
  background: var(--nav-bg-color);
  -webkit-backdrop-filter: blur(var(--bg-overlay-blur));
  backdrop-filter: blur(var(--bg-overlay-blur));
  border-bottom: 1px solid var(--border-color);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.header-left, .header-center, .header-right {
  display: flex;
  align-items: center;
}

.logo-container {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: inherit;
}

.logo-img {
  height: 50px;
  margin-right: 1rem;
}

.logo-text h1 {
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--accent-primary);
  margin: 0;
}

.logo-text p {
  font-size: 0.8rem;
  color: var(--text-secondary);
  margin: 0;
}


.nav-links a {
  margin: 0 1.5rem;
  text-decoration: none;
  color: var(--nav-text-color);
  font-size: 1.1rem;
  font-weight: 500;
  transition: color 0.3s ease;
}

.nav-links a:hover {
  color: var(--nav-text-color-hover);
}

.user-actions {
  display: flex;
  align-items: center;
}

.user-menu {
  position: relative;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
}

.user-menu-trigger {
  background: none;
  border: none;
  color: var(--nav-text-color);
  cursor: pointer;
  display: flex;
  align-items: center;
  font-size: 1rem;
  font-weight: 500;
}

.user-menu-trigger:hover {
  color: var(--nav-text-color-hover);
}

.dropdown-menu {
  position: absolute;
  top: calc(100% + 22px);
  right: 0;
  background-color: var(--nav-bg-color);
  -webkit-backdrop-filter: blur(var(--bg-overlay-blur));
  backdrop-filter: blur(var(--bg-overlay-blur));
  border: 1px solid var(--border-color);
  border-radius: 8px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  list-style: none;
  padding: 0.5rem;
  margin: 0;
  min-width: 160px;
  z-index: 1001;
}

.dropdown-item {
  display: block;
  padding: 0.8rem 1rem;
  color: var(--nav-text-color);
  text-decoration: none;
  font-size: 1rem;
  white-space: nowrap;
  border-radius: 4px;
}

.dropdown-item:hover {
  background-color: var(--dropdown-item-bg-hover);
  color: var(--nav-text-color-hover);
}

.btn {
  padding: 0.6rem 1.2rem;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  margin-left: 1rem;
}

.btn-primary {
  background-color: var(--accent-primary);
  color: var(--text-primary);
  transition: background-color 0.3s ease;
}

.btn-primary:hover {
  background-color: var(--accent-primary-hover);
}

@media (max-width: 768px) {
  .logo-text {
    display: none;
  }

  .header {
    padding: 1rem;
  }

  .nav-links a {
    margin: 0 0.8rem;
    font-size: 1rem;
  }
}
</style>