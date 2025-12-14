import {ref} from 'vue'
import {defineStore} from 'pinia'

export const useUiStore = defineStore('ui', () => {
    const loginDialogVisible = ref(false)
    const registerDialogVisible = ref(false)

    function showLogin() {
        console.log('showLogin called');
        // Ensure only one dialog is open at a time
        registerDialogVisible.value = false
        loginDialogVisible.value = true
    }

    function showRegister() {
        // Ensure only one dialog is open at a time
        console.log('showRegister called');
        loginDialogVisible.value = false
        registerDialogVisible.value = true
    }

    function hideDialogs() {
        loginDialogVisible.value = false
        registerDialogVisible.value = false
    }

    return {
        loginDialogVisible,
        registerDialogVisible,
        showLogin,
        showRegister,
        hideDialogs
    }
})