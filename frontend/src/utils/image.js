export const getImageUrl = (path) => {
  if (!path) return '';

  // Check if the path is already a full URL
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path;
  }

  // Ensure the path does not start with a slash to avoid double slashes
  const cleanPath = path.startsWith('/') ? path.slice(1) : path;
  return `${import.meta.env.VITE_API_BASE_URL}/${cleanPath}`;
};