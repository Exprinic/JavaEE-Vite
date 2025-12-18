export const getImageUrl = (path) => {
  if (!path) return '';

  // Check if the path is already a full URL
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path;
  }

  // For paths that start with "/wallpapers/", they should be served from the public directory
  // In Vite, files in the public directory are served at the root level
  if (path.startsWith('/wallpapers/')) {
    // Return path as-is since Vite serves public directory files from root
    return path;
  }

  // For other relative paths, construct the full URL
  if (path.startsWith('/')) {
    // Use VITE_API_BASE_URL if available, otherwise default to localhost
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
    return `${baseUrl}${path}`;
  } else {
    // Use VITE_API_BASE_URL if available, otherwise default to localhost
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
    return `${baseUrl}/${path}`;
  }
};