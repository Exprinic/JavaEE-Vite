<template>
  <div class="admin-layout">
    <aside class="sidebar">
      <h1 class="admin-title">Admin</h1>
      <nav>
        <ul>
          <li><a href="#" @click.prevent="activeTab = 'dashboard'">Dashboard</a></li>
          <li><a href="#" @click.prevent="activeTab = 'users'">User Management</a></li>
          <li><a href="#" @click.prevent="activeTab = 'wallpapers'">Wallpaper Management</a></li>
          <li><a href="#" @click.prevent="activeTab = 'settings'">System Settings</a></li>
        </ul>
      </nav>
    </aside>
    <main class="main-content">
      <div v-if="activeTab === 'dashboard'">
        <h2>Dashboard</h2>
        <p>Welcome to the admin dashboard. Here you can see an overview of the system.</p>
      </div>
      <div v-if="activeTab === 'users'">
        <h2>User Management</h2>
        <p>Here you can manage users.</p>
      </div>
      <div v-if="activeTab === 'wallpapers'">
        <h2>Wallpaper Management</h2>
        <p>Here you can manage wallpapers.</p>
      </div>
      <div v-if="activeTab === 'settings'">
        <h2>System Settings</h2>
        <p>Here you can configure system settings.</p>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const activeTab = ref('dashboard');
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 250px;
  background-color: var(--bg-card);
  color: var(--text-primary);
  padding: 2rem;
}

.admin-title {
  font-size: 2rem;
  font-weight: bold;
  text-align: center;
  margin-bottom: 2rem;
}

.sidebar nav ul {
  list-style: none;
  padding: 0;
}

.sidebar nav li a {
  display: block;
  color: var(--text-primary);
  text-decoration: none;
  padding: 1rem;
  border-radius: 5px;
  transition: background-color 0.3s;
}

.sidebar nav li a:hover {
  background-color: var(--bg-overlay-dark);
}

.main-content {
  flex-grow: 1;
  padding: 2rem;
  background-color: var(--bg-color);
}

.main-content h2 {
  font-size: 1.8rem;
  margin-bottom: 1rem;
}
</style>