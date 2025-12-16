import {ref} from 'vue'

export function useValidation() {
    const formatError = ref({
        username: '',
        password: '',
        confirmPassword: '',
        phone: '',
        captcha: ''
    })


    const validatePhone = (phone) => {
        formatError.value.phone = ''
        const regex = /^1[3456789]\d{9}$/
        if (!phone) {
            formatError.value.phone = 'Phone number cannot be empty.'
        } else if (!regex.test(phone)) {
            formatError.value.phone = 'Please enter a valid phone number.'
        } else {
            formatError.value.phone = ''
        }
    }

    const validatePassword = (password) => {

        const regex = /^((?=.*[a-z])(?=.*[A-Z])(?=.*\d)|(?=.*[a-z])(?=.*[A-Z])(?=.*[^\da-zA-Z])|(?=.*[a-z])(?=.*\d)(?=.*[^\da-zA-Z])|(?=.*[A-Z])(?=.*\d)(?=.*[^\da-zA-Z])).{7,}$/
        if (!password) {
            formatError.value.password = 'Password cannot be empty.'
        } else if (password.length < 7) {
            formatError.value.password = 'Password must be at least 7 characters long.'
        } else if (!regex.test(password)) {
            formatError.value.password = 'Password must contain at least three of the following: uppercase letters, lowercase letters, numbers, and special characters.'
        } else {
            formatError.value.password = ''
        }
    }

    const validateUsername = (username) => {
        formatError.value.username = ''
        const regex = /^[a-zA-Z0-9_.-]+$/
        if (!username) {
            formatError.value.username = 'Username cannot be empty.'
        } else if (!regex.test(username)) {
            formatError.value.username = 'Username can only contain letters, numbers, underscores, hyphens, and dots.'
        } else if (username.startsWith('.') || username.endsWith('.')) {
            formatError.value.username = 'Dots cannot appear at the beginning or end of the username.'
        } else if (username.includes('..')) {
            formatError.value.username = 'Username cannot contain consecutive dots.'
        } else {
            formatError.value.username = ''
        }
    }

    const validateConfirmPassword = (password, confirmPassword) => {
        formatError.value.confirmPassword = ''
        if (password !== confirmPassword) {
            formatError.value.confirmPassword = 'The two passwords entered do not match.'
        } else {
            formatError.value.confirmPassword = ''
        }
    }


    // 验证码必须为六位数字
    const validateCaptcha = (captcha) => {
        formatError.value.captcha = ''
        const regex = /^\d{6}$/
        if (!captcha) {
            formatError.value.captcha = 'Captcha cannot be empty.'
        } else if (!regex.test(captcha)) {
            formatError.value.captcha = 'Captcha must be a 6-digit number.'
        } else {
            formatError.value.captcha = ''
        }
    }

    const isFormValid = () => {
        // 检查是否所有字段都没有错误信息
        return Object.values(formatError.value).every(error => error === '')
    }

    return {
        formatError,
        validatePhone,
        validatePassword,
        validateUsername,
        validateConfirmPassword,
        validateCaptcha,
        isFormValid
    }
}