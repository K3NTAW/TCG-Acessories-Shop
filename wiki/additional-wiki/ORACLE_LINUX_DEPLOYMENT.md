# Oracle Linux Deployment Guide

Complete guide for deploying the TCG Shop microservices stack on **Oracle Linux** (the default OS for Oracle Cloud instances).

## 🔍 Identify Your OS

First, check which OS you're running:

```bash
cat /etc/os-release
```

Oracle Linux uses `yum` or `dnf` (not `apt`).

## 🚀 Installation Steps

### 1. Update System

```bash
# For Oracle Linux 7/8
sudo yum update -y

# For Oracle Linux 9+ (uses dnf)
sudo dnf update -y
```

### 2. Install Docker

#### Option A: Using Oracle Linux Package Manager

```bash
# Install required packages
sudo yum install -y yum-utils device-mapper-persistent-data lvm2

# Add Docker repository
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo

# Install Docker
sudo yum install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Start and enable Docker
sudo systemctl start docker
sudo systemctl enable docker

# Add user to docker group
sudo usermod -aG docker opc  # or your username
newgrp docker

# Verify installation
docker --version
docker compose version
```

#### Option B: Using Docker's Installation Script

```bash
# Download and run Docker installation script
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Start and enable Docker
sudo systemctl start docker
sudo systemctl enable docker

# Add user to docker group
sudo usermod -aG docker opc
newgrp docker

# Verify
docker --version
docker compose version
```

### 3. Install Git (if not installed)

```bash
# Oracle Linux 7/8
sudo yum install -y git

# Oracle Linux 9+
sudo dnf install -y git
```

### 4. Clone Repository

```bash
cd ~
git clone <your-repository-url> microservice-arbeit
cd microservice-arbeit
```

### 5. Build and Start Services

```bash
cd docker

# Build all images
docker compose build

# Start all services
docker compose up -d

# Check status
docker compose ps

# View logs
docker compose logs -f
```

## 🔧 Oracle Linux Specific Commands

### Package Management

```bash
# Search for package
yum search <package-name>

# Install package
sudo yum install -y <package-name>

# Remove package
sudo yum remove -y <package-name>

# List installed packages
yum list installed
```

### Service Management

```bash
# Start service
sudo systemctl start <service-name>

# Stop service
sudo systemctl stop <service-name>

# Enable service (start on boot)
sudo systemctl enable <service-name>

# Check status
sudo systemctl status <service-name>
```

### Firewall (firewalld)

```bash
# Check firewall status
sudo systemctl status firewalld

# Open ports
sudo firewall-cmd --permanent --add-port=3000/tcp
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --permanent --add-port=8761/tcp
sudo firewall-cmd --reload

# List open ports
sudo firewall-cmd --list-ports
```

## 🌐 Network Configuration

### Open Required Ports

```bash
# Open ports in firewalld
sudo firewall-cmd --permanent --add-port=3000/tcp  # Frontend
sudo firewall-cmd --permanent --add-port=8080/tcp  # API Gateway
sudo firewall-cmd --permanent --add-port=8761/tcp  # Eureka
sudo firewall-cmd --permanent --add-port=22/tcp    # SSH
sudo firewall-cmd --reload

# Verify
sudo firewall-cmd --list-all
```

**Also configure in OCI Security List:**
- OCI Console → Networking → Security Lists → Your VCN → Add Ingress Rules

## 🐛 Troubleshooting

### Issue: "yum: command not found"

**Solution**: You might be on Oracle Linux 9+ which uses `dnf`:

```bash
sudo dnf update -y
sudo dnf install -y docker-ce docker-ce-cli containerd.io
```

### Issue: "Cannot find Docker repository"

**Solution**: Add Docker repository manually:

```bash
sudo yum install -y yum-utils
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
```

### Issue: "Permission denied" with Docker

**Solution**: 
```bash
# Add user to docker group
sudo usermod -aG docker $USER
newgrp docker

# Or restart your SSH session
```

### Issue: "Port already in use"

**Solution**: Check what's using the port:

```bash
sudo netstat -tulpn | grep :8080
# Or
sudo ss -tulpn | grep :8080

# Kill the process if needed
sudo kill -9 <PID>
```

## 📋 Quick Reference

```bash
# Update system
sudo yum update -y  # or dnf update -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Start services
cd ~/microservice-arbeit/docker
docker compose up -d

# View logs
docker compose logs -f

# Stop services
docker compose down
```

## 🔒 Security

### SELinux (if enabled)

```bash
# Check SELinux status
getenforce

# If enforcing, you may need to adjust contexts
sudo setsebool -P container_manage_cgroup on
```

### Firewall Rules

```bash
# Allow Docker
sudo firewall-cmd --permanent --zone=public --add-masquerade
sudo firewall-cmd --reload
```

## 📚 Additional Resources

- [Oracle Linux Documentation](https://docs.oracle.com/en/operating-systems/oracle-linux/)
- [Docker on Oracle Linux](https://docs.oracle.com/en/operating-systems/oracle-linux/8/software/software-Docker.html)


