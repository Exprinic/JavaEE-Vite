import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/authStore';
import { useUserStore } from '../stores/userStore';
import { useNotificationStore } from '../stores/notificationStore'; // Import notification store

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: () => import('../views/HomeView.vue')
        },
        {
            path: '/explore',
            name: 'explore',
            component: () => import('../views/ExploreView.vue')
        },
        {
            path: '/upload',
            name: 'upload',
            component: () => import('../views/UploadView.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/wallpaper/:id',
            name: 'wallpaper-detail',
            component: () => import('../views/WallpaperDetailView.vue')
        },
        {
            path: '/admin',
            name: 'admin',
            component: () => import('../views/AdminView.vue'),
            meta: { requiresAuth: true, requiresAdmin: true }
        },
        {
            path: '/faq',
            name: 'faq',
            component: () => import('../views/FAQView.vue')
        },
        {
            path: '/terms',
            name: 'terms',
            component: () => import('../views/TermsView.vue')
        },
        {
            path: '/privacy',
            name: 'privacy',
            component: () => import('../views/PrivacyView.vue')
        },
        {
            path: '/profile',
            name: 'profile',
            component: () => import('../views/ProfileView.vue'),
            meta: { requiresAuth: true }
        }
    ]
});

router.beforeEach((to, from, next) => {
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
    // This line of code checks the meta information of the target route and all its parent routes to see if any of them have `requiresAuth: true` set.
    const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin);
    // Check if administrator privileges are required
    // If administrator privileges are required, check if the user is an administrator
    // If not an administrator, redirect to the homepage or display an error message

    if (requiresAuth) {
        const authStore = useAuthStore();
        if (!authStore.isAuthenticated) {
            // This is where you would trigger your login modal/dialog
            // For now, we'll just block navigation and log a message.
            // In a real app, you might do something like:
            // authStore.showLoginModal = true;
            // or redirect to a login page:
            // next({ name: 'login' });
            console.log('Redirecting to login because route requires auth.');
            // Or, if you have a notification system:
            const notificationStore = useNotificationStore();
            notificationStore.addNotification({ message: 'Please log in to access this page.', type: 'info' });
            return next(false); // Cancel the navigation
        }
    }

    if (requiresAdmin) {
        const userStore = useUserStore();
        const user = userStore.user;
        if (!user || user.role !== 'admin') {
            console.log('Redirecting because route requires admin privileges.');
            const notificationStore = useNotificationStore();
            notificationStore.addNotification({ message: 'You do not have permission to access this page.', type: 'error' });
            return next({ name: 'home' }); // Redirect to home or an 'unauthorized' page
        }
    }

    next(); // Proceed with the navigation
});

export default router;