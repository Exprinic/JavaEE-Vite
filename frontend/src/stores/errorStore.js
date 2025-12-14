import {defineStore} from 'pinia';
import {ref} from 'vue';
import {errorApi} from '../api/type/error.js';

export const useErrorStore = defineStore('error', () => {
    const errors = ref(null);

    async function uploadError(error) {
        try {
            await errorApi.uploadError(error);
        } catch (e) {
            console.error('Failed to upload error:', e);
        }
    }

    function addError(error) {
        errors.value.push(error);
    }

    function getError() {
        // 返回最新的错误（最后一个元素）
        if (!errors.value || errors.value.length === 0) {
            return null;
        }
        return errors.value[errors.value.length - 1];
    }

    return {
        uploadError,
        addError,
        getError
    };
});