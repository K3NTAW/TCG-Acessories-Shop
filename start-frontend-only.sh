#!/bin/bash
echo "🚀 Starting Frontend Only..."
echo ""
echo "This will start just the frontend (UI only, no backend)"
echo ""
cd frontend/tcg-shop-frontend
if [ ! -d "node_modules" ]; then
    echo "📦 Installing dependencies..."
    npm install
fi
echo ""
echo "✅ Starting development server..."
echo "🌐 Frontend will be available at: http://localhost:3000"
echo ""
npm run dev
