<template>
  <div class="upload-container">
    <div class="upload-card">
      <h2>Upload Your Masterpiece</h2>
      <p>Share your stunning wallpapers with the world. High-quality images only.</p>

      <div class="upload-layout">
        <div
            class="drop-zone"
            @dragover.prevent="onDragOver"
            @dragleave.prevent="onDragLeave"
            @drop.prevent="onDrop"
            :class="{ 'drag-over': isDragOver }"
            @click="triggerFileInput"
        >
          <div v-if="!previewUrl" class="drop-zone-content">
            <i class="fas fa-cloud-upload-alt"></i>
            <p><strong>Drag & Drop your file here</strong></p>
            <p class="small-text">or click to browse</p>
            <input type="file" ref="fileInput" @change="onFileChange" accept="image/*" class="file-input">
          </div>
          <div v-else class="preview-container">
            <img :src="previewUrl" alt="File preview" class="preview-image"/>
            <button class="remove-file-button" @click.stop="removeFile">&times;</button>
          </div>
        </div>

        <div class="form-container">
          <form @submit.prevent="handleUpload">
            <div class="form-group">
              <label for="title">Title</label>
              <input type="text" id="title" v-model="title" required placeholder="e.g., 'Sunset Over the Mountains'">
            </div>
            <div class="form-group">
              <label for="description">Description</label>
              <textarea id="description" v-model="description"
                        placeholder="A brief description of your wallpaper"></textarea>
            </div>
            <div class="form-group">
              <label>Tags</label>
              <div class="tag-input-container">
                <div v-for="(tag, index) in tags" :key="index" class="tag-item">
                  {{ tag }}
                  <button @click.prevent="removeTag(index)" class="remove-tag-button">&times;</button>
                </div>
                <input
                    type="text"
                    v-model="currentTag"
                    @keydown.enter.prevent="addTag"
                    @keydown.backspace="handleBackspace"
                    placeholder="Add tags and press Enter"
                    class="tag-input"
                />
              </div>
            </div>

            <div v-if="progress > 0" class="progress-bar-container">
              <div class="progress-bar" :style="{ width: progress + '%' }"></div>
            </div>

            <button type="submit" :disabled="loading || !file">
              <span v-if="loading">Uploading...</span>
              <span v-else>Upload Wallpaper</span>
            </button>
            <p v-if="error" class="error-message">{{ error }}</p>
            <p v-if="uploadSuccess" class="success-message">Upload successful!</p>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref} from 'vue'
import {useWallpaper} from '../composables/useWallpaper'
import { wallpapers1 } from '../data/mockData.js'
import { useAuthStore } from '../stores/authStore.js'

const {loading, error, progress, uploadWallpaper} = useWallpaper()

const title = ref('')
const description = ref('')
const tags = ref([])
const currentTag = ref('')
const file = ref(null)
const previewUrl = ref(null)
const isDragOver = ref(false)
const fileInput = ref(null)
const uploadSuccess = ref(false)

const getCurrentUsername = () => {
  const authStore = useAuthStore()
  return authStore.user?.username || 'Anonymous'
}

const onDragOver = () => {
  isDragOver.value = true
}
const onDragLeave = () => {
  isDragOver.value = false
}
const onDrop = (e) => {
  isDragOver.value = false
  const droppedFile = e.dataTransfer.files[0]
  if (droppedFile && droppedFile.type.startsWith('image/')) {
    setFile(droppedFile)
  }
}

const onFileChange = (e) => {
  const selectedFile = e.target.files[0]
  if (selectedFile) {
    setFile(selectedFile)
  }
}

const setFile = (newFile) => {
  file.value = newFile
  previewUrl.value = URL.createObjectURL(newFile)
  uploadSuccess.value = false
  error.value = null
}

const removeFile = () => {
  file.value = null
  previewUrl.value = null
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

const triggerFileInput = () => {
  if (!file.value) {
    fileInput.value.click()
  }
}

const addTag = () => {
  if (currentTag.value.trim() !== '' && !tags.value.includes(currentTag.value.trim())) {
    tags.value.push(currentTag.value.trim())
  }
  currentTag.value = ''
}

const removeTag = (index) => {
  tags.value.splice(index, 1)
}

const handleBackspace = () => {
  if (currentTag.value === '' && tags.value.length > 0) {
    tags.value.pop()
  }
}

const handleUpload = async () => {
  if (!file.value) {
    error.value = 'Please select a file to upload.'
    return
  }

  // Simulate saving the file and getting a new path
  const newPath = await saveFile(file.value)

  const newId = wallpapers1.length > 0 ? Math.max(...wallpapers1.map(w => w.id)) + 1 : 1
  const newWallpaper = {
    id: newId,
    name: title.value,
    artist: getCurrentUsername(),
    imageUrl: newPath,
    tags: tags.value,
    filter: 'Nature',
    description: description.value
  }

  wallpapers1.push(newWallpaper)

  // Simulate successful upload
  uploadSuccess.value = true
  title.value = ''
  description.value = ''
  tags.value = []
  removeFile()
}

const saveFile = async (file) => {
  // This is a mock function. In a real scenario, you would upload the file
  // to a server and get a URL in response. Here, we'll just create a fake path.
  const newFileName = `${Date.now()}_${file.name}`
  const newPath = `/wallpapers/${newFileName}`

  // In a real Node.js environment, you would use fs.copyFile or similar.
  // Since we are in a browser environment, we can't actually save the file.
  // We will just pretend it's saved and use the generated path.
  console.log(`Simulating file save to: public${newPath}`)

  return newPath
}

</script>

<style scoped>
.upload-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 4rem 2rem;
  /* background-color is removed to show the global wallpaper */
}

.upload-card {
  width: 100%;
  max-width: 1000px;
  background: rgba(40, 40, 40, 0.65);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 3rem;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  text-align: center;
}

.upload-card h2 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  color: var(--accent-primary);
}

.upload-card p {
  color: var(--text-dark);
  margin-bottom: 3rem;
}

.upload-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 3rem;
  text-align: left;
}

.drop-zone {
  border: 2px dashed var(--border-color);
  border-radius: 15px;
  padding: 2rem;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  transition: background-color 0.3s, border-color 0.3s;
  position: relative;
  min-height: 300px;
}

.drop-zone.drag-over {
  border-color: var(--box-shadow-upload);
  background-color: var(--accent-primary-alpha-10);
}

.drop-zone-content {
  text-align: center;
  color: var(--text-muted);
}

.drop-zone-content i {
  font-size: 4rem;
  color: var(--accent-primary);
  margin-bottom: 1rem;
}

.small-text {
  font-size: 0.9rem;
}

.file-input {
  display: none;
}

.preview-container {
  width: 100%;
  height: 100%;
  position: relative;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 10px;
}

.remove-file-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.6);
  color: var(--text-primary);
  border: none;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  font-size: 1.5rem;
  line-height: 30px;
  text-align: center;
  cursor: pointer;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

input[type="text"],
textarea {
  width: 100%;
  padding: 0.8rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  background-color: var(--bg-card);
  color: var(--text-dark);
  font-size: 1rem;
}

textarea {
  resize: vertical;
  min-height: 100px;
}

.tag-input-container {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 0.5rem;
}

.tag-item {
  background-color: var(--tag-bg-color-active);
  color: var(--text-primary);
  padding: 0.4rem 0.8rem;
  border-radius: 5px;
  margin: 0.25rem;
  display: flex;
  align-items: center;
}

.remove-tag-button {
  background: none;
  border: none;
  color: var(--text-primary);
  margin-left: 0.5rem;
  cursor: pointer;
  font-size: 1rem;
}

.tag-input {
  flex-grow: 1;
  border: none;
  padding: 0.4rem;
  outline: none;
  background: transparent;
}

.progress-bar-container {
  width: 100%;
  height: 10px;
  background-color: var(--border-color);
  border-radius: 5px;
  margin-top: 1rem;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background-color: var(--accent-primary);
  transition: width 0.3s ease;
}

button[type="submit"] {
  width: 100%;
  padding: 1rem;
  background-color: var(--accent-primary);
  color: var(--text-muted);
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 1rem;
}

button[type="submit"]:disabled {
  background-color: var(--gray-300);
  cursor: not-allowed;
}

.error-message {
  color: var(--error-color);
  margin-top: 1rem;
}

.success-message {
  color: var(--success-color);
  margin-top: 1rem;
}
</style>