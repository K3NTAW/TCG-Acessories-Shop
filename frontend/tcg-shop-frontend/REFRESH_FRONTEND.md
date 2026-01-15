# 🔄 How to Refresh/Update the Frontend

If you're seeing old content after making changes, try these steps in order:

## Method 1: Restart Dev Server (Most Common Fix)

1. **Stop the current dev server** (Ctrl+C in the terminal where it's running)

2. **Clear Vite cache and restart**:
   ```bash
   cd frontend/tcg-shop-frontend
   rm -rf node_modules/.vite
   npm run dev
   ```

## Method 2: Hard Refresh Browser

While the dev server is running:

- **Chrome/Edge**: `Ctrl+Shift+R` (Windows/Linux) or `Cmd+Shift+R` (Mac)
- **Firefox**: `Ctrl+F5` (Windows/Linux) or `Cmd+Shift+R` (Mac)
- **Safari**: `Cmd+Option+R`

Or manually:
1. Open DevTools (F12)
2. Right-click the refresh button
3. Select "Empty Cache and Hard Reload"

## Method 3: Full Clean Restart

If the above doesn't work:

```bash
cd frontend/tcg-shop-frontend

# Stop dev server (Ctrl+C)

# Clear all caches
rm -rf node_modules/.vite
rm -rf dist
rm -rf .vite

# Restart dev server
npm run dev
```

## Method 4: If Using Docker

If you're running the frontend in Docker:

```bash
# Stop the container
docker-compose down frontend

# Rebuild the image
docker-compose build frontend

# Start it again
docker-compose up -d frontend
```

Or if using the Dockerfile directly:
```bash
cd frontend/tcg-shop-frontend
docker build -t tcg-shop-frontend .
docker run -p 3000:80 tcg-shop-frontend
```

## Method 5: Clear Browser Cache Completely

1. Open browser settings
2. Clear browsing data
3. Select "Cached images and files"
4. Clear data
5. Restart browser

## Method 6: Use Incognito/Private Window

Test in a private/incognito window to bypass cache:
- **Chrome**: `Ctrl+Shift+N` (Windows/Linux) or `Cmd+Shift+N` (Mac)
- **Firefox**: `Ctrl+Shift+P` (Windows/Linux) or `Cmd+Shift+P` (Mac)
- **Safari**: `Cmd+Shift+N`

## Quick Checklist

- ✅ Dev server is running (`npm run dev`)
- ✅ Browser cache cleared (hard refresh)
- ✅ Vite cache cleared (`rm -rf node_modules/.vite`)
- ✅ No build artifacts interfering (`rm -rf dist`)
- ✅ Check browser console for errors (F12)

## Still Not Working?

1. **Check if files are actually saved** - Make sure your changes are saved in the editor
2. **Check the terminal** - Look for any errors in the dev server output
3. **Check browser console** - Open DevTools (F12) and look for errors
4. **Verify the file path** - Make sure you're editing the right files in `src/`

## Verify Changes Are Loading

1. Open DevTools (F12)
2. Go to Network tab
3. Refresh the page
4. Check if your changed files are being loaded (look at timestamps)
5. Check the Sources tab to see if your code changes are there

