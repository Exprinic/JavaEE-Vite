<template>
  <div class="dialog-overlay" v-if="registerDialogVisible">
    <div class="dialog-card" @click.stop>
      <button class="close-button" @click="closeDialogs">&times;</button>
      <div class="logo-container">
        <h1 class="logo">EAZ</h1>
        <p class="tagline">ExtraOrdinary Artistic Zone</p>
      </div>
      <form @submit.prevent="register" autocomplete="off">
        <div class="form-group">
          <label for="username">Username</label>
          <input type="text" id="username" v-model="username" @blur="validateUsername(username)" required
                 autocomplete="username">
          <span class="error-message" v-if="formatError.username">{{ formatError.username }}</span>
        </div>
        <div class="form-group">
          <label for="phone">Phone</label>
          <input type="text" id="phone" v-model="phone" @blur="validatePhone(phone)" required autocomplete="tel">
          <span class="error-message" v-if="formatError.phone">{{ formatError.phone }}</span>
        </div>
        <div class="form-group">
          <label for="password">Password</label>
          <input type="password" id="password" v-model="password"
                 @blur="validatePassword(password)" required autocomplete="new-password">
          <span class="error-message" v-if="formatError.password">{{ formatError.password }}</span>
        </div>
        <div class="form-group">
          <label for="confirm-password">Confirm Password</label>
          <input type="password" id="confirm-password" v-model="confirmPassword"
                 @blur="validateConfirmPassword(password, confirmPassword)" required autocomplete="new-password">
          <span class="error-message" v-if="formatError.confirmPassword">{{ formatError.confirmPassword }}</span>
        </div>
        <div class="form-group verify-code-group">
          <label for="verify-code">Verify Code</label>
          <div class="verify-code-input-wrapper">
<!--            点击获取验证码时不会触发验证码格式错误信息-->
            <input type="text" id="verify-code" v-model="captcha"
                   @blur="shouldValidateCaptcha ? validateCaptcha(captcha) : null" required autocomplete="one-time-code">
            <button @click.prevent="handleGetCodeClick" class="verify-code-get-button">Get Code</button>
          </div>
          <span class="error-message" v-if="formatError.captcha">{{ formatError.captcha }}</span>
        </div>
        <button type="submit" class="auth-button" :disabled="isRegisterDisabled">Register</button>
      </form>
      <div class="links">
        <a href="#" @click.prevent="switchToLogin">Already have an account? Login</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, computed} from 'vue'
import {useAuthStore} from '../../stores/authStore'
import {useUiStore} from '../../stores/uiStore'
import {storeToRefs} from 'pinia'
import {useValidation} from '../../utils/useValidation.js'
import {useErrorStore} from "../../stores/errorStore.js";

const authStore = useAuthStore()
const uiStore = useUiStore()
const errorStore = useErrorStore()
const {registerDialogVisible} = storeToRefs(uiStore)
const {register: authRegister, getCaptcha} = authStore
const {hideDialogs, showLogin} = uiStore

const {
  formatError,
  validatePhone,
  validatePassword,
  validateUsername,
  validateConfirmPassword,
  validateCaptcha,
  isFormValid
} = useValidation()

const username = ref('')
const phone = ref('')
const password = ref('')
const confirmPassword = ref('')
const captcha = ref('')
const shouldValidateCaptcha = ref(true)

const handleGetCodeClick = async () => {
  // Temporarily disable captcha validation to prevent "captcha cannot be empty" error
  shouldValidateCaptcha.value = false
  
  validateUsername(username.value)
  validatePhone(phone.value)
  validatePassword(password.value)
  validateConfirmPassword(password.value, confirmPassword.value)

  const isGetCodeEnabled = !formatError.value.username &&
                           !formatError.value.phone &&
                           !formatError.value.password &&
                           username.value &&
                           phone.value &&
                           password.value;

  if (!isGetCodeEnabled) {
    // Re-enable captcha validation
    shouldValidateCaptcha.value = true
    return
  }

  try {
    await getCaptcha({
      phone: phone.value.trim(),
      password: password.value.trim()
    })
    // Re-enable captcha validation
    shouldValidateCaptcha.value = true
  } catch (e) {
    // Re-enable captcha validation
    shouldValidateCaptcha.value = true
    errorStore.addError(e)
  }
}

const isRegisterDisabled = computed(() => {
  return !isFormValid() || !username.value || !phone.value || !password.value || !confirmPassword.value || !captcha.value
})


const register = async () => {
  try {
    await authRegister({
      username: username.value.trim(),
      phone: phone.value.trim(),
      password: password.value.trim(),
      captcha: captcha.value.trim(),
      // 添加 inviteCode 字段，即使为空也要发送
      inviteCode: ''
    })
  } catch (error) {
    // 错误信息已经在 authStore 中处理并显示给用户
    console.error('Registration error:', error)
  }
}

const switchToLogin = () => {
  console.log('switch to LoginDialog')
  showLogin()
  clearForm()
  formatError.value = {}
}

const closeDialogs = () => {
  console.log('close RegisterDialog')
  hideDialogs()
  clearForm()
  formatError.value = {}
}

const clearForm = () => {
  username.value = ''
  phone.value = ''
  password.value = ''
  confirmPassword.value = ''
  captcha.value = ''
}
</script>

<style scoped>
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog-card {
  background: var(--bg-card);
  padding: 2rem;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 420px;
  text-align: center;
  position: relative;
}

.close-button {
  position: absolute;
  top: 15px;
  right: 15px;
  background: none;
  border: none;
  font-size: 1.8rem;
  cursor: pointer;
  color: var(--text-muted);
}

.logo-container {
  margin-bottom: 2rem;
}

.logo {
  font-size: 3rem;
  font-weight: bold;
  color: var(--accent-primary);
  margin: 0;
}

.tagline {
  color: var(--text-muted);
}

.form-group {
  margin-bottom: 1rem;
  text-align: left;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--text-dark);
  font-weight: 500;
}

input {
  width: 100%;
  padding: 0.7rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  box-sizing: border-box;
  transition: border-color 0.3s;
}

input:focus {
  outline: none;
  border-color: var(--accent-primary);
}

.verify-code-group .verify-code-input-wrapper {
  display: flex;
  align-items: center;
}

.captcha-group input {
  flex-grow: 1;
}

.verify-code-group .verify-code-get-button {
  font-weight: 500;
  white-space: nowrap;

  background-color: var(--accent-primary);
  color: var(--bg-card);

  border-radius: 5px;
  border: 1px solid var(--border-color);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
  padding: 0.8rem 1rem;

  cursor: pointer;
}

.verify-code-group .verify-code-get-button:hover {
  background: var(--accent-primary-hover);
  color: var(--bg-card);
  transition: background 0.5s, color 0.5s;
}

.links {
  margin-top: 1.5rem;
  text-align: center;
}

.links a {
  color: var(--accent-primary);
  text-decoration: none;
  font-weight: 500;
}

.auth-button {
  width: 100%;
  padding: 0.8rem 1rem;
  background-color: var(--accent-primary);
  color: var(--bg-card);
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.auth-button:hover:not(:disabled) {
  background-color: var(--accent-primary-hover);
}

.auth-button:disabled {
  background-color: var(--border-color);
  cursor: not-allowed;
}

.error-message {
  color: var(--error-color);
  font-size: 0.8rem;
}
</style>