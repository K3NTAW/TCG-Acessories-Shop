# 🐳 How to Update Frontend in Docker Stack

When you make changes to the frontend and it's running in Docker, you need to rebuild the Docker image.

## Quick Update (Recommended)

```bash
cd docker

# Rebuild and restart just the frontend service
docker-compose build frontend
docker-compose up -d frontend
```

This will:
1. Rebuild the frontend Docker image with your latest changes
2. Restart the frontend container
3. Keep all other services running

## Full Stack Restart

If you want to restart everything:

```bash
cd docker

# Rebuild frontend and restart all services
docker-compose build frontend
docker-compose up -d
```

## Force Rebuild (No Cache)

If changes aren't showing up, force a rebuild without cache:

```bash
cd docker

# Force rebuild without using cache
docker-compose build --no-cache frontend
docker-compose up -d frontend
```

## Check if Update Worked

1. **Check container logs**:
   ```bash
   docker-compose logs frontend
   ```

2. **Verify container is running**:
   ```bash
   docker ps | grep frontend
   ```

3. **Hard refresh browser**:
   - `Ctrl+Shift+R` (Windows/Linux)
   - `Cmd+Shift+R` (Mac)

## Complete Clean Rebuild

If you're still seeing old content:

```bash
cd docker

# Stop and remove the frontend container
docker-compose stop frontend
docker-compose rm -f frontend

# Remove the old image
docker rmi tcg-shop-frontend 2>/dev/null || true

# Rebuild from scratch
docker-compose build --no-cache frontend
docker-compose up -d frontend
```

## Development Workflow Tips

### Option 1: Run Frontend Locally (Recommended for Development)

For faster development, run the frontend locally instead of in Docker:

```bash
# Stop Docker frontend
cd docker
docker-compose stop frontend

# Run frontend locally
cd ../frontend/tcg-shop-frontend
npm run dev
```

This gives you:
- ✅ Hot reload (instant updates)
- ✅ Faster iteration
- ✅ Better debugging

### Option 2: Use Volume Mounting (Advanced)

You can modify `docker-compose.yml` to mount the source code as a volume for live updates, but this requires a different Dockerfile setup.

## Troubleshooting

### Frontend still shows old content?

1. **Clear browser cache**: Hard refresh (`Ctrl+Shift+R`)
2. **Check if rebuild worked**: `docker-compose logs frontend`
3. **Verify image was rebuilt**: `docker images | grep frontend`
4. **Check container is new**: `docker ps --format "table {{.Names}}\t{{.CreatedAt}}" | grep frontend`

### Build fails?

1. **Check for syntax errors**: Run `npm run build` locally first
2. **Check Docker logs**: `docker-compose build frontend` (without `-d`)
3. **Verify dependencies**: Make sure `package.json` is correct

### Port already in use?

```bash
# Stop the container
docker-compose stop frontend

# Or if running locally, stop the local dev server
# Then restart Docker frontend
docker-compose up -d frontend
```

## Quick Reference Commands

```bash
# Rebuild frontend
docker-compose build frontend

# Restart frontend
docker-compose up -d frontend

# View logs
docker-compose logs -f frontend

# Stop frontend
docker-compose stop frontend

# Remove frontend container
docker-compose rm -f frontend

# Force rebuild
docker-compose build --no-cache frontend
```

