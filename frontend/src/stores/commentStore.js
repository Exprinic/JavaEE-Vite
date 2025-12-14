import { defineStore } from 'pinia'

export const useCommentStore = defineStore('comment', {
  state: () => ({
    comments: [],
    currentWallpaperComments: [],
    replies: [],
    commentDrafts: {},
  }),
  actions: {
    async fetchComments(wallpaperId) {
      // Mock fetch comments
      console.log('Fetching comments for wallpaper', wallpaperId)
      this.currentWallpaperComments = [
        { id: 1, user: 'User A', text: 'Great wallpaper!' },
        { id: 2, user: 'User B', text: 'I love it!' },
      ]
    },
    async addComment(commentData) {
      // Mock add comment
      console.log('Adding comment', commentData)
      this.currentWallpaperComments.push({ id: Date.now(), user: 'New User', text: commentData.text })
    },
    async deleteComment(commentId) {
      // Mock delete comment
      console.log('Deleting comment', commentId)
      this.currentWallpaperComments = this.currentWallpaperComments.filter(c => c.id !== commentId)
    },
    async addReply(replyData) {
      // Mock add reply
      console.log('Adding reply', replyData)
    },
  },
})