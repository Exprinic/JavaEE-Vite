<template>
  <div class="dialog-overlay" v-if="loginDialogVisible">
    <div class="dialog-card" @click.stop>
      <button class="close-button" @click="closeDialogs">&times;</button>
      <div class="logo-container">
        <h1 class="logo">EAZ</h1>
        <p class="tagline">ExtraOrdinary Artistic Zone</p>
      </div>
      <form @submit.prevent="login" autocomplete="off">
        <!--        Create a form. When this form is submitted, first prevent the browser's default behavior of refreshing the page, and then call the component's internal method named `login` to handle the login logic.-->
        <div class="form-group">
          <label for="phone">Phone</label>
          <input type="text" id="phone" v-model="phone" @blur="validatePhone(phone)" required autocomplete="username">
          <span class="error-message" v-if="formatError.phone">{{ formatError.phone }}</span>
        </div>
        <div class="form-group">
          <label for="password">Password</label>
          <input type="password" id="password" v-model="password" @blur="validatePassword(password)" required
                 autocomplete="current-password">
          <span class="error-message" v-if="formatError.password">{{ formatError.password }}</span>
        </div>
        <div class="form-group verify-code-group">
          <label for="captcha">Verify Code</label>
          <div class="verify-code-input-wrapper">
            <input type="text" id="captcha" v-model="captcha" @blur="shouldValidateCaptcha ? validateCaptcha(captcha) : null"
                   required autocomplete="one-time-code">
            <button @click.prevent="handleGetCodeClick" class="verify-code-get-button">Get Code</button>
          </div>
          <span class="error-message" v-if="formatError.captcha">{{ formatError.captcha }}</span>
        </div>
        <button type="submit" class="auth-button" :disabled="isLoginDisabled">Login</button>
      </form>
      <div class="links">
        <a href="#" @click.prevent="switchToRegister">Register</a>
        <a href="#">Forgot Password</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, watch, computed} from 'vue'
import {useAuthStore} from '../../stores/authStore'
import {useUiStore} from '../../stores/uiStore'
import {storeToRefs} from 'pinia'
import {useValidation} from '../../utils/useValidation.js'
import {useErrorStore} from "../../stores/errorStore.js";

const authStore = useAuthStore()
const uiStore = useUiStore()
const errorStore = useErrorStore()
const {loginDialogVisible} = storeToRefs(uiStore)
const {login: authLogin, getCaptcha: authGetCaptcha} = authStore
const {hideDialogs, showRegister} = uiStore

const {validatePhone, validatePassword, validateCaptcha, formatError, isFormValid} = useValidation()

const phone = ref('')
const password = ref('')
const captcha = ref('')
const shouldValidateCaptcha = ref(true)

const handleGetCodeClick = async () => {
  // Temporarily disable captcha validation to prevent "captcha cannot be empty" error
  shouldValidateCaptcha.value = false
  
  validatePhone(phone.value)
  validatePassword(password.value)

  // Check if phone and password are valid
  const isGetCodeEnabled = !formatError.value.phone && 
                          !formatError.value.password && 
                          phone.value && 
                          password.value;

  if (!isGetCodeEnabled) {
    // Re-enable captcha validation
    shouldValidateCaptcha.value = true
    return
  }

  try {
    await authGetCaptcha({
      phone: phone.value,
      password: password.value
    })
    // Re-enable captcha validation
    shouldValidateCaptcha.value = true
  } catch (e) {
    // Re-enable captcha validation
    shouldValidateCaptcha.value = true
    errorStore.addError(e)
  }
}

const isLoginDisabled = computed(() => {
  // validatePhone(phone.value)
  // validatePassword(password.value)
  // validateCaptcha(captcha.value)
  // The form is invalid if the required fields are empty.
  return !isFormValid() || !phone.value || !password.value || !captcha.value;
});

const login = async () => {

  try {
    await authLogin({
      phone: phone.value.trim(),
      password: password.value.trim(),
      captcha: captcha.value.trim()
    })
  } catch (error) {
    console.error('Login error:', error);
    errorStore.addError(error)
  }
}

const switchToRegister = () => {
  console.log('switch to RegisterDialog')
  showRegister()
  formatError.value = {}
  // Clear form when switching to register
  clearForm()
}

const closeDialogs = () => {
  console.log('close LoginDialog')
  hideDialogs()
  formatError.value = {}
  // Clear form when closing dialogs
  clearForm()
}


const clearForm = () => {
  phone.value = ''
  password.value = ''
  captcha.value = ''
}

// Watch for dialog visibility changes to clear form when opening
watch(loginDialogVisible, (newVal) => {
  if (newVal) {
    // Dialog is opening, ensure form is cleared
    phone.value = ''
    password.value = ''
    captcha.value = ''
  }
})

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
  z-index: 2000;
}

.dialog-card {
  background: var(--bg-card);
  padding: 2.5rem;
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
  margin-bottom: 2.5rem;
}

.logo {
  font-size: 3.5rem;
  font-weight: bold;
  color: var(--accent-primary);
  margin: 0;
}

.tagline {
  color: var(--text-muted);
}

.form-group {
  margin-bottom: 1.5rem;
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
  padding: 0.8rem 1rem;
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

.verify-code-group input {
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
  display: flex;
  justify-content: space-between;
}

.links a {
  color: var(--accent-primary);
  text-decoration: none;
  font-weight: 500;
}

.links a:hover {
  background-color: var(--accent-primary-hover);
  transition: background-color 0.5s;
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