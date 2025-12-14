<template>
  <div class="profile-container">
    <h1>Profile</h1>
    <p>This is the profile page</p>
    <div>
      <p>Name: {{ user.name }}</p>
      <p>Email: {{ user.email }}</p>
      <p>Role: {{ user.role }}</p>
      <button @click="logout" class="logout-btn">Logout</button>
    </div>
  </div>
</template>
<script setup>
import {useAuthStore} from '../stores/authStore'
import {useRoute} from 'vue-router'
import {storeToRefs} from 'pinia'

const authStore = useAuthStore()
const {isAuthenticated, user} = storeToRefs(authStore)
const route = useRoute()

function logout() {
  authStore.logout()
}
</script>
<style scoped>
.profile-container {
  text-align: center;
  padding: 20px;
}

.logout-btn {
  margin-top: 20px;
  background-color: var(--accent-primary);
  color: var(--text-primary);
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
</style>