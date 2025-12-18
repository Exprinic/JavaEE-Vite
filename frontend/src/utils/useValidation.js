// utils/useValidation.js - 增强版
import { ref, computed } from 'vue'

export function useValidation() {
    // ============ State ============
    const formatError = ref({
        username: '',
        password: '',
        confirmPassword: '',
        phone: '',
        captcha: ''
    })

    const touchedFields = ref({
        username: false,
        password: false,
        confirmPassword: false,
        phone: false,
        captcha: false
    })

    // ============ Computed Properties ============
    const hasErrors = computed(() => {
        return Object.values(formatError.value).some(error => error !== '')
    })

    const errorCount = computed(() => {
        return Object.values(formatError.value).filter(error => error !== '').length
    })

    // ============ Validation Functions ============

    /**
     * 验证手机号
     */
    const validatePhone = (phone) => {
        touchedFields.value.phone = true
        formatError.value.phone = ''

        if (!phone || phone.trim() === '') {
            formatError.value.phone = '手机号不能为空'
        } else if (!/^1[3-9]\d{9}$/.test(phone)) {
            formatError.value.phone = '请输入有效的手机号'
        } else if (phone.length !== 11) {
            formatError.value.phone = '手机号必须是11位数字'
        }

        return formatError.value.phone === ''
    }

    /**
     * 验证密码
     */
    const validatePassword = (password) => {
        touchedFields.value.password = true
        formatError.value.password = ''

        const strongPasswordRegex = /^((?=.*[a-z])(?=.*[A-Z])(?=.*\d)|(?=.*[a-z])(?=.*[A-Z])(?=.*[^\da-zA-Z])|(?=.*[a-z])(?=.*\d)(?=.*[^\da-zA-Z])|(?=.*[A-Z])(?=.*\d)(?=.*[^\da-zA-Z])).{7,}$/

        if (!password || password.trim() === '') {
            formatError.value.password = '密码不能为空'
        } else if (password.length < 7) {
            formatError.value.password = '密码长度至少为7位'
        } else if (password.length > 20) {
            formatError.value.password = '密码长度不能超过20位'
        } else if (!strongPasswordRegex.test(password)) {
            formatError.value.password = '密码必须包含大写字母、小写字母、数字和特殊字符中的至少三种'
        }

        return formatError.value.password === ''
    }

    /**
     * 验证用户名
     */
    const validateUsername = (username) => {
        touchedFields.value.username = true
        formatError.value.username = ''

        const usernameRegex = /^[a-zA-Z0-9_.-]+$/

        if (!username || username.trim() === '') {
            formatError.value.username = '用户名不能为空'
        } else if (!usernameRegex.test(username)) {
            formatError.value.username = '用户名只能包含字母、数字、下划线、点和短横线'
        } else if (username.startsWith('.') || username.endsWith('.')) {
            formatError.value.username = '点不能出现在用户名开头或结尾'
        } else if (username.includes('..')) {
            formatError.value.username = '用户名不能包含连续的点'
        } else if (username.length < 4) {
            formatError.value.username = '用户名长度至少为4个字符'
        } else if (username.length > 16) {
            formatError.value.username = '用户名长度不能超过16个字符'
        }

        return formatError.value.username === ''
    }

    /**
     * 验证确认密码
     */
    const validateConfirmPassword = (password, confirmPassword) => {
        touchedFields.value.confirmPassword = true
        formatError.value.confirmPassword = ''

        if (!confirmPassword || confirmPassword.trim() === '') {
            formatError.value.confirmPassword = '请确认密码'
        } else if (password !== confirmPassword) {
            formatError.value.confirmPassword = '两次输入的密码不一致'
        }

        return formatError.value.confirmPassword === ''
    }

    /**
     * 验证验证码
     */
    const validateCaptcha = (captcha) => {
        touchedFields.value.captcha = true
        formatError.value.captcha = ''

        if (!captcha || captcha.trim() === '') {
            formatError.value.captcha = '验证码不能为空'
        } else if (!/^\d{6}$/.test(captcha)) {
            formatError.value.captcha = '验证码必须是6位数字'
        }

        return formatError.value.captcha === ''
    }

    /**
     * 验证整个表单
     */
    const validateForm = (formData) => {
        const { username, phone, password, confirmPassword, captcha } = formData

        // 验证所有字段
        const validations = [
            username ? validateUsername(username) : true,
            validatePhone(phone),
            validatePassword(password),
            validateConfirmPassword(password, confirmPassword),
            validateCaptcha(captcha)
        ]

        // 所有验证都通过
        return validations.every(isValid => isValid === true)
    }

    /**
     * 检查表单是否有效
     */
    const isFormValid = () => {
        return !hasErrors.value
    }

    /**
     * 重置验证状态
     */
    const resetValidation = () => {
        formatError.value = {
            username: '',
            password: '',
            confirmPassword: '',
            phone: '',
            captcha: ''
        }

        touchedFields.value = {
            username: false,
            password: false,
            confirmPassword: false,
            phone: false,
            captcha: false
        }
    }

    /**
     * 获取字段错误状态
     */
    const getFieldError = (fieldName) => {
        return {
            hasError: formatError.value[fieldName] !== '',
            message: formatError.value[fieldName],
            touched: touchedFields.value[fieldName]
        }
    }

    /**
     * 标记字段为已触摸
     */
    const touchField = (fieldName) => {
        if (touchedFields.value.hasOwnProperty(fieldName)) {
            touchedFields.value[fieldName] = true
        }
    }

    // ============ 返回 ============
    return {
        // State
        formatError,
        touchedFields,

        // Computed
        hasErrors,
        errorCount,

        // Validation Functions
        validatePhone,
        validatePassword,
        validateUsername,
        validateConfirmPassword,
        validateCaptcha,
        validateForm,

        // Utility Functions
        isFormValid,
        resetValidation,
        getFieldError,
        touchField
    }
}