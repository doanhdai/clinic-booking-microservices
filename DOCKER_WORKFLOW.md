# 🐳 Docker Workflow Guide

## 📋 Tổng quan

Dự án này sử dụng Docker để containerize các microservices. Tất cả images đều có naming convention nhất quán: `clinic-booking-microservices/service-name:latest`

## 🚀 Quy trình làm việc

### 1️⃣ **Lần đầu tiên build hệ thống**

```bash
# Build tất cả images
./scripts/build-images.sh

# Start tất cả services
docker-compose up -d
```

### 2️⃣ **Khi thay đổi code của 1 service cụ thể**

```bash
# Ví dụ: Thay đổi code trong user-service

# Rebuild chỉ user-service
./scripts/rebuild-service.sh user-service

# Hoặc rebuild manual
docker build -f ./user-service/Dockerfile.user -t clinic-booking-microservices/user-service:latest ./user-service/

# Restart service
docker-compose restart user-service
```

### 3️⃣ **Khi thay đổi code nhiều services**

```bash
# Rebuild tất cả
./scripts/rebuild-service.sh

# Restart tất cả
docker-compose restart
```

### 4️⃣ **Cleanup images cũ/không dùng**

```bash
# Xóa dangling images (<none>)
./scripts/cleanup-images.sh

# Xóa tất cả unused images
./scripts/cleanup-images.sh --all
```

## 📝 **Naming Convention**

### **Images:**
- Format: `clinic-booking-microservices/service-name:latest`
- Ví dụ:
  - `clinic-booking-microservices/user-service:latest`
  - `clinic-booking-microservices/doctor-service:latest`
  - `clinic-booking-microservices/appointment-service:latest`

### **Containers:**
- Format: `clinic-service-type`
- Ví dụ:
  - `clinic-user`
  - `clinic-doctor`
  - `clinic-appointment`

### **Dockerfiles:**
- Format: `Dockerfile.service-type`
- Vị trí: `service-name/Dockerfile.service-type`
- Ví dụ:
  - `user-service/Dockerfile.user`
  - `doctor-service/Dockerfile.doctor`

## 🔧 **Commands Cheat Sheet**

### **Build & Deploy**
```bash
# Build all images
./scripts/build-images.sh

# Build specific service
docker build -f ./user-service/Dockerfile.user -t clinic-booking-microservices/user-service:latest ./user-service/

# Start all services
docker-compose up -d

# Start specific service
docker-compose up -d user-service

# Rebuild and restart specific service
./scripts/rebuild-service.sh user-service
```

### **Management**
```bash
# View running containers
docker ps

# View all containers
docker ps -a

# View logs
docker logs clinic-user
docker logs clinic-user --tail 50
docker logs clinic-user -f  # follow

# Stop all services
docker-compose down

# Stop specific service
docker-compose stop user-service

# Restart all services
docker-compose restart

# Restart specific service
docker-compose restart user-service
```

### **Cleanup**
```bash
# Remove dangling images
docker image prune -f

# Remove all unused images
docker image prune -a -f

# Remove all stopped containers
docker container prune -f

# Full cleanup (images, containers, volumes, networks)
docker system prune -a --volumes -f

# Use cleanup script
./scripts/cleanup-images.sh
./scripts/cleanup-images.sh --all
```

### **Debugging**
```bash
# Enter container shell
docker exec -it clinic-user /bin/bash

# View container details
docker inspect clinic-user

# View resource usage
docker stats

# View networks
docker network ls
docker network inspect clinic-booking-microservices_clinic-network
```

## ⚡ **Tối ưu hóa**

### **1. Không tạo image trùng lặp:**
- `docker-compose.yml` sử dụng `image:` thay vì `build:`
- Chỉ build images qua script `build-images.sh` hoặc `rebuild-service.sh`
- Docker Compose chỉ sử dụng images đã build sẵn

### **2. Cache hiệu quả:**
- Multi-stage builds trong Dockerfile
- Maven dependencies được cache ở layer riêng
- Chỉ rebuild khi có thay đổi code

### **3. Rebuild nhanh:**
- Sử dụng `rebuild-service.sh service-name` để rebuild 1 service cụ thể
- Không cần rebuild tất cả khi chỉ thay đổi 1 service

## 🔍 **Troubleshooting**

### **Vấn đề: Images bị trùng lặp**
```bash
# Kiểm tra images
docker images | grep clinic-booking

# Xóa image cũ
docker rmi image-id

# Cleanup
./scripts/cleanup-images.sh --all
```

### **Vấn đề: Container không start**
```bash
# Xem logs
docker logs clinic-user

# Xem chi tiết
docker inspect clinic-user

# Force restart
docker-compose down
docker-compose up -d
```

### **Vấn đề: Port conflict**
```bash
# Kiểm tra port đang sử dụng
netstat -ano | findstr :8081

# Stop container đang dùng port
docker stop clinic-user
```

## 📊 **Health Check**

```bash
# Check service health
curl http://localhost:8761  # Discovery
curl http://localhost:8888/actuator/health  # Config
curl http://localhost:8081/actuator/health  # User
curl http://localhost:8083/actuator/health  # Doctor
curl http://localhost:8082/actuator/health  # Appointment
curl http://localhost:8080/actuator/health  # Gateway
```

## 🎯 **Best Practices**

1. **Luôn build qua script** để đảm bảo naming convention nhất quán
2. **Cleanup định kỳ** để tránh disk space đầy
3. **Chỉ rebuild service thay đổi** để tiết kiệm thời gian
4. **Kiểm tra logs** trước khi restart service
5. **Sử dụng `docker-compose restart`** thay vì `down` + `up` để giữ nguyên config

## 🔗 **Related Files**

- `docker-compose.yml` - Docker Compose configuration
- `scripts/build-images.sh` - Build all images
- `scripts/rebuild-service.sh` - Rebuild specific service
- `scripts/cleanup-images.sh` - Cleanup unused images
- `Dockerfile.*` - Individual service Dockerfiles


