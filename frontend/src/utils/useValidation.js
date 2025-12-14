import {ref} from 'vue'

export function useValidation() {
    const formatError = ref({
        username: '',
        password: '',
        confirmPassword: '',
        phone: '',
        verifyCode: ''
    })


    const validatePhone = (phone) => {
        const regex = /^1[3456789]\d{9}$/
        if (!phone) {
            formatError.value.phone = 'Phone number cannot be empty.'
        } else if (!regex.test(phone)) {
            formatError.value.phone = 'Please enter a valid phone number.'
        } else {
            delete formatError.value.phone
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
            delete formatError.value.password
        }
    }

    const validateUsername = (username) => {
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
            delete formatError.value.username
        }
    }

    const validateConfirmPassword = (password, confirmPassword) => {
        if (password !== confirmPassword) {
            formatError.value.confirmPassword = 'The two passwords entered do not match.'
        } else {
            delete formatError.value.confirmPassword
        }
    }


    // 验证码必须为六位数字
    const validateVerifyCode = (verifyCode) => {
        const regex = /^\d{6}$/
        if (!verifyCode) {
            formatError.value.verifyCode = 'Verify code cannot be empty.'
        } else if (!regex.test(verifyCode)) {
            formatError.value.verifyCode = 'Verify code must be a 6-digit number.'
        } else {
            delete formatError.value.verifyCode
        }
    }

    const isFormValid = () => {
        return Object.keys(formatError.value).length === 0
    }

    return {
        formatError,
        validatePhone,
        validatePassword,
        validateUsername,
        validateConfirmPassword,
        validateVerifyCode,
        isFormValid
    }
}