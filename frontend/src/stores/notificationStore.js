import { defineStore } from 'pinia';
import {ref} from "vue";

export const useNotificationStore = defineStore('notification',() => {
    const notifications = ref([]);
    function addNotification(notification) {
      const id = Date.now() + Math.random();
      const newNotification = {
          id,
        message: notification.message || 'An unexpected error occurred.',
        type: notification.type || 'info', // 'success', 'error', 'warning', 'info'
      };
      notifications.value.push(newNotification);

      const duration = notification.duration === 0 ? 0 : notification.duration || 3000;
      if (duration !== 0) {
        setTimeout(() => {
          removeNotification(id);
        }, duration);
      }

      return id;
    }
    function removeNotification(id) {
      notifications.value = notifications.value.filter(n => n.id !== id);
    }

    return {
      notifications,
      addNotification,
      removeNotification,
    }
})