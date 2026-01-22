# Oracle Cloud Infrastructure (OCI) Connection Guide

Complete guide for connecting to Oracle Cloud instances and deploying the TCG Shop microservices stack.

## 🔐 Connection Methods

### Method 1: SSH Key Authentication (Recommended)

#### Step 1: Generate SSH Key Pair (if you don't have one)

```bash
# Generate SSH key pair
ssh-keygen -t rsa -b 4096 -C "your-email@example.com" -f ~/.ssh/oracle_cloud_key

# This creates:
# - ~/.ssh/oracle_cloud_key (private key)
# - ~/.ssh/oracle_cloud_key.pub (public key)
```

#### Step 2: Add Public Key to OCI Instance

1. **Via OCI Console:**
   - Go to **Compute** → **Instances**
   - Select your instance
   - Click **Edit** → **Show Advanced Options**
   - Under **SSH Keys**, paste your public key (`~/.ssh/oracle_cloud_key.pub`)
   - Save changes

2. **Via Cloud Shell:**
   ```bash
   # Copy your public key
   cat ~/.ssh/oracle_cloud_key.pub
   
   # Then paste it in OCI Console as above
   ```

#### Step 3: Connect via SSH

```bash
# Get your instance's public IP from OCI Console
# Then connect:
ssh -i ~/.ssh/oracle_cloud_key opc@144.24.247.60

# Or if using ubuntu user:
ssh -i ~/.ssh/oracle_cloud_key ubuntu@<your-instance-public-ip>
```

### Method 2: Cloud Shell Connection

If you're already in Oracle Cloud Shell:

```bash
# Connect to your instance from Cloud Shell
ssh opc@<your-instance-private-ip>

# Or use the instance OCID
oci compute instance get --instance-id <instance-ocid>
```

### Method 3: Using OCI CLI

```bash
# Install OCI CLI (if not installed)
bash -c "$(curl -L https://raw.githubusercontent.com/oracle/oci-cli/master/scripts/install/install.sh)"

# Configure OCI CLI
oci setup config

# Connect via OCI CLI
oci compute instance get --instance-id <instance-ocid>
```

## 🖥️ Instance Setup After Connection

Once connected to your Oracle Cloud instance:

### 1. Update System

```bash
# For Oracle Linux / RHEL
sudo yum update -y

# For Ubuntu
sudo apt update && sudo apt upgrade -y
```

### 2. Install Docker (Oracle Linux)

**First, check your OS:**
```bash
cat /etc/os-release
```

**For Oracle Linux (uses yum/dnf, NOT apt):**

```bash
# Update system
sudo yum update -y  # or dnf update -y for OL9+

# Install Docker using official script (recommended)
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

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

**Alternative: Manual installation**
```bash
# Install prerequisites
sudo yum install -y yum-utils device-mapper-persistent-data lvm2

# Add Docker repository
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo

# Install Docker
sudo yum install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Start and enable
sudo systemctl start docker
sudo systemctl enable docker
```

> **Note:** See [ORACLE_LINUX_DEPLOYMENT.md](./ORACLE_LINUX_DEPLOYMENT.md) for complete Oracle Linux deployment guide.

### 3. Install Docker (Ubuntu - if your instance is Ubuntu)

```bash
# Use the setup script or manual installation
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
newgrp docker
```

### 4. Clone and Deploy

```bash
# Clone repository
git clone <your-repository-url> microservice-arbeit
cd microservice-arbeit/docker

# Build and start services
docker compose build
docker compose up -d
```

## 🔧 Troubleshooting Connection Issues

### Issue: "Permission denied (publickey)"

**Solutions:**

1. **Check SSH key permissions:**
   ```bash
   chmod 600 ~/.ssh/oracle_cloud_key
   chmod 644 ~/.ssh/oracle_cloud_key.pub
   ```

2. **Verify public key is added to instance:**
   - Check OCI Console → Instance → SSH Keys
   - Ensure your public key is listed

3. **Try with verbose output:**
   ```bash
   ssh -v -i ~/.ssh/oracle_cloud_key opc@<instance-ip>
   ```

### Issue: "Connection timeout"

**Solutions:**

1. **Check Security List (Firewall):**
   - OCI Console → Networking → Security Lists
   - Ensure SSH (port 22) is allowed from your IP
   - Add ingress rule:
     - Source: `0.0.0.0/0` (or your IP)
     - Destination Port: `22`
     - Protocol: `TCP`

2. **Check Network Security Groups (if used):**
   - Ensure SSH port 22 is open

3. **Verify instance is running:**
   ```bash
   # From OCI Console or CLI
   oci compute instance get --instance-id <instance-ocid>
   ```

### Issue: "Host key verification failed"

**Solution:**
```bash
# Remove old host key
ssh-keygen -R <instance-ip>

# Or add to known_hosts manually
ssh-keyscan <instance-ip> >> ~/.ssh/known_hosts
```

## 🌐 Network Configuration

### Opening Ports for Application

After connecting, you need to open ports in OCI Security List:

1. **Go to:** OCI Console → Networking → Virtual Cloud Networks → Your VCN → Security Lists

2. **Add Ingress Rules:**

| Port | Protocol | Source | Description |
|------|----------|--------|-------------|
| 22 | TCP | Your IP / 0.0.0.0/0 | SSH |
| 3000 | TCP | 0.0.0.0/0 | Frontend |
| 8080 | TCP | 0.0.0.0/0 | API Gateway |
| 8761 | TCP | 0.0.0.0/0 | Eureka (optional) |

3. **Or use OCI CLI:**
```bash
oci network security-list update \
  --security-list-id <security-list-id> \
  --ingress-security-rules file://ingress-rules.json
```

### Example ingress-rules.json:
```json
[
  {
    "protocol": "6",
    "source": "0.0.0.0/0",
    "tcpOptions": {
      "destinationPortRange": {
        "min": 3000,
        "max": 3000
      }
    }
  },
  {
    "protocol": "6",
    "source": "0.0.0.0/0",
    "tcpOptions": {
      "destinationPortRange": {
        "min": 8080,
        "max": 8080
      }
    }
  }
]
```

## 📋 Quick Reference

### Get Instance Information

```bash
# Get public IP
oci compute instance list-vnics --instance-id <instance-ocid>

# Get instance details
oci compute instance get --instance-id <instance-ocid>
```

### Common Commands

```bash
# Connect
ssh -i ~/.ssh/oracle_cloud_key opc@<instance-ip>

# Check if Docker is installed
docker --version

# Check running containers
docker ps

# View logs
docker compose logs -f
```

## 🔒 Security Best Practices

1. **Use SSH keys, not passwords**
2. **Restrict SSH access** to your IP only (not 0.0.0.0/0)
3. **Use Network Security Groups** for additional security
4. **Keep system updated**: `sudo yum update` or `sudo apt update`
5. **Use firewall** (firewalld/ufw) on the instance
6. **Regular backups** of important data

## 🚀 Deployment Checklist

- [ ] Instance created and running
- [ ] SSH key added to instance
- [ ] Can connect via SSH
- [ ] Docker installed
- [ ] Security List rules configured
- [ ] Repository cloned
- [ ] Services built and started
- [ ] Ports accessible from internet
- [ ] Application tested

## 📚 Additional Resources

- [OCI Documentation](https://docs.oracle.com/en-us/iaas/Content/home.htm)
- [OCI CLI Documentation](https://docs.oracle.com/en-us/iaas/Content/API/Concepts/cli.htm)
- [Oracle Linux Documentation](https://docs.oracle.com/en/operating-systems/oracle-linux/)

