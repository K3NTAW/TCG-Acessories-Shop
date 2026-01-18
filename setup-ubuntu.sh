#!/bin/bash

# TCG Shop - Ubuntu Deployment Setup Script
# This script automates the installation of Docker and Docker Compose on Ubuntu

set -e

echo "🚀 TCG Shop - Ubuntu Deployment Setup"
echo "======================================"
echo ""

# Check if running as root
if [ "$EUID" -eq 0 ]; then 
   echo "❌ Please do not run this script as root"
   echo "   Run as a regular user (sudo will be used when needed)"
   exit 1
fi

# Check Ubuntu version
if [ ! -f /etc/os-release ]; then
    echo "❌ Cannot detect Ubuntu version"
    exit 1
fi

. /etc/os-release
if [ "$ID" != "ubuntu" ]; then
    echo "⚠️  This script is designed for Ubuntu. Proceed anyway? (y/n)"
    read -r response
    if [ "$response" != "y" ]; then
        exit 1
    fi
fi

echo "📦 Step 1: Updating system packages..."
sudo apt update && sudo apt upgrade -y

echo ""
echo "🐳 Step 2: Installing Docker..."
# Remove old versions
sudo apt remove -y docker docker-engine docker.io containerd runc 2>/dev/null || true

# Install prerequisites
sudo apt install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    git

# Add Docker's official GPG key
sudo mkdir -p /etc/apt/keyrings
if [ ! -f /etc/apt/keyrings/docker.gpg ]; then
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    sudo chmod a+r /etc/apt/keyrings/docker.gpg
fi

# Set up repository
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker Engine
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Add user to docker group
echo ""
echo "👤 Step 3: Adding user to docker group..."
sudo usermod -aG docker "$USER"

echo ""
echo "✅ Docker installation complete!"
echo ""
echo "📋 Verification:"
docker --version
docker compose version

echo ""
echo "⚠️  IMPORTANT: You need to log out and log back in for group changes to take effect."
echo "   Or run: newgrp docker"
echo ""
echo "📝 Next steps:"
echo "   1. Log out and log back in (or run: newgrp docker)"
echo "   2. Navigate to the project directory: cd docker"
echo "   3. Build and start services: docker compose build && docker compose up -d"
echo ""
echo "🎉 Setup complete!"

