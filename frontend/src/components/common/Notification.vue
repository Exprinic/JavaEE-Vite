<template>
  <div class="notification-container">
    <transition-group name="notification-fade" tag="div">
      <div v-for="notification in notifications" :key="notification.id" :class="['notification', `notification-${notification.type}`]">
        <i :class="['notification-icon', getIconClass(notification.type)]"></i>
        <p class="notification-message">{{ notification.message }}</p>
        <button @click="removeNotification(notification.id)" class="close-btn">&times;</button>
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { useNotificationStore } from '../../stores/notificationStore';
import { storeToRefs } from 'pinia';

const notificationStore = useNotificationStore();
const { notifications } = storeToRefs(notificationStore);
const { removeNotification } = notificationStore;

const getIconClass = (type) => {
  switch (type) {
    case 'success':
      return 'fas fa-check-circle';
    case 'error':
      return 'fas fa-times-circle';
    case 'warning':
      return 'fas fa-exclamation-triangle';
    case 'info':
    default:
      return 'fas fa-info-circle';
  }
};
</script>

<style scoped>
.notification-container {
  position: fixed;
  top: 80px; /* Adjusted to be below the header */
  right: 20px;
  z-index: 10000;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.notification {
  display: flex;
  align-items: center;
  width: 350px;
  max-width: 90vw;
  padding: 1rem 1.5rem;
  border-radius: 8px;
  box-shadow: var(--box-shadow-lg);
  border-left: 5px solid;
  background-color: var(--bg-card);
  color: var(--text-dark);
}

.notification-icon {
  font-size: 1.5rem;
  margin-right: 1rem;
}

.notification-message {
  flex-grow: 1;
  margin: 0;
  font-size: 0.95rem;
}

.close-btn {
  background: none;
  border: none;
  color: var(--text-muted);
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0 0 0 1rem;
  opacity: 0.7;
  transition: opacity 0.3s ease;
}

.close-btn:hover {
  opacity: 1;
}

/* Notification Types */
.notification-success {
  border-color: var(--success-color);
}
.notification-success .notification-icon {
  color: var(--success-color);
}

.notification-error {
  border-color: var(--error-color);
}
.notification-error .notification-icon {
  color: var(--error-color);
}

.notification-warning {
  border-color: var(--warning-color);
}
.notification-warning .notification-icon {
  color: var(--warning-color);
}

.notification-info {
  border-color: var(--info-color);
}
.notification-info .notification-icon {
  color: var(--info-color);
}

/* Transitions */
.notification-fade-enter-active,
.notification-fade-leave-active {
  transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.notification-fade-enter-from,
.notification-fade-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

.notification-fade-move {
  transition: transform 0.4s ease;
}
</style>