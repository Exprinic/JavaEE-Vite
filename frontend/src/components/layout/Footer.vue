<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-section about">
        <h2 class="logo-text">EAZ</h2>
        <p>ExtraOrdinary Artistic Zone - a curated collection of stunning wallpapers from around the world.</p>
      </div>
      <div class="footer-section links">
        <h2>Quick Links</h2>
        <ul>
          <li>
            <router-link to="/explore">Explore</router-link>
          </li>
          <li>
            <router-link to="/upload">Upload</router-link>
          </li>
          <li>
            <router-link to="/faq">FAQ</router-link>
          </li>
        </ul>
      </div>
      <div class="footer-section links">
        <h2>Legal</h2>
        <ul>
          <li>
            <router-link to="/terms">Terms of Service</router-link>
          </li>
          <li>
            <router-link to="/privacy">Privacy Policy</router-link>
          </li>
        </ul>
      </div>
      <div class="footer-section social">
        <h2>Follow Us</h2>
        <div class="social-icons">
          <a href="#"><i class="fab fa-facebook"></i></a>
          <a href="#"><i class="fab fa-twitter"></i></a>
          <a href="#"><i class="fab fa-instagram"></i></a>
        </div>
      </div>
    </div>
    <div class="footer-bottom">
      &copy; {{ new Date().getFullYear() }} EAZ. All rights reserved.
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.footer {
  background: var(--nav-bg-color);
  backdrop-filter: blur(var(--bg-overlay-blur));
  -webkit-backdrop-filter: blur(var(--bg-overlay-blur));
  color: var(--nav-text-color);
  padding: 2rem 0;
  border-top: 1px solid var(--border-color);
}

.footer-content {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

.footer-section {
  flex: 1;
  min-width: 200px;
  margin: 1rem;
}

.footer-section p {
  margin-bottom: 1rem;
  color: var(--text-secondary);
}

.footer-section h2 {
  color: var(--text-primary);
  font-size: 1.2rem;
  margin-bottom: 1rem;
}

.footer-section ul {
  list-style: none;
  padding: 0;
}

.footer-section ul li a {
  color: var(--text-secondary);
  text-decoration: none;
  margin-bottom: 0.5rem;
  display: block;
  transition: color 0.3s ease;
}

.footer-section ul li a:hover {
  color: var(--accent-primary);
}

.social-icons a {
  margin-right: 1rem;
  color: var(--text-secondary);
  font-size: 1.5rem;
}

.social-icons a:hover {
  color: var(--accent-primary);
}

.footer-bottom {
  text-align: center;
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid var(--border-color);
  color: var(--text-secondary);
}
</style>