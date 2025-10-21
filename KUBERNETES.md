# Clinic Booking Microservices - Kubernetes Deployment

Hệ thống Clinic Booking Microservices được containerized và deploy trên Kubernetes với đầy đủ các tính năng:

## 🏗️ **Kiến trúc Kubernetes**

```
┌─────────────────────────────────────────────────────────────┐
│                    Kubernetes Cluster                       │
├─────────────────────────────────────────────────────────────┤
│  Namespace: clinic-booking                                  │
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │   Gateway   │  │   Config    │  │ Discovery   │         │
│  │  Service    │  │  Service    │  │  Service    │         │
│  │  (LoadBal)  │  │             │  │             │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │    User     │  │   Doctor    │  │Appointment  │         │
│  │  Service    │  │  Service    │  │  Service    │         │
│  │             │  │             │  │             │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
│                                                             │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                    MySQL Database                       │ │
│  │              (Persistent Volume)                       │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 **Quick Start**

### **1. Prerequisites**
- Docker installed and running
- Kubernetes cluster (minikube, kind, or cloud provider)
- kubectl configured
- Maven (for building)

### **2. Build Docker Images**
```bash
# Make scripts executable
chmod +x scripts/*.sh

# Build all Docker images
./scripts/build-images.sh
```

### **3. Deploy to Kubernetes**
```bash
# Deploy all services to Kubernetes
./scripts/deploy-k8s.sh
```

### **4. Access Services**
```bash
# Get Gateway Service URL
kubectl get service gateway-service -n clinic-booking

# Port forward for local access
kubectl port-forward service/gateway-service 8080:8080 -n clinic-booking
```

## 📁 **File Structure**

```
k8s/
├── namespace.yaml                 # Kubernetes namespace
├── mysql-configmap.yaml          # MySQL configuration
├── mysql-init-configmap.yaml     # MySQL initialization script
├── mysql-pvc.yaml               # MySQL persistent volume claim
├── mysql.yaml                   # MySQL deployment and service
├── discovery-service.yaml       # Discovery service deployment
├── config-service.yaml          # Config service deployment
├── user-service.yaml            # User service deployment
├── doctor-service.yaml          # Doctor service deployment
├── appointment-service.yaml     # Appointment service deployment
└── gateway-service.yaml         # Gateway service deployment

scripts/
├── build-images.sh             # Build Docker images
├── deploy-k8s.sh               # Deploy to Kubernetes
└── cleanup-k8s.sh              # Cleanup Kubernetes resources

docker-compose.yml              # Local development with Docker Compose
```

## 🔧 **Configuration**

### **Environment Variables**
Các service sử dụng environment variables để cấu hình:

- `SPRING_PROFILES_ACTIVE=k8s` - Profile cho Kubernetes
- `SPRING_DATASOURCE_URL` - Database connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE` - Eureka server URL

### **ConfigMaps**
- `mysql-config` - MySQL configuration
- `mysql-init-script` - MySQL initialization script

### **PersistentVolumes**
- `mysql-pvc` - MySQL data persistence (10Gi)

## 📊 **Monitoring & Health Checks**

Tất cả services đều có health checks:
- **Liveness Probe**: Kiểm tra service có hoạt động không
- **Readiness Probe**: Kiểm tra service có sẵn sàng nhận traffic không

### **Health Check Endpoints**
- Discovery Service: `http://discovery-service:8761/actuator/health`
- Config Service: `http://config-service:8888/actuator/health`
- User Service: `http://user-service:8081/actuator/health`
- Doctor Service: `http://doctor-service:8083/actuator/health`
- Appointment Service: `http://appointment-service:8082/actuator/health`
- Gateway Service: `http://gateway-service:8080/actuator/health`

## 🔄 **Scaling**

### **Horizontal Pod Autoscaler (HPA)**
```bash
# Scale User Service
kubectl scale deployment user-service --replicas=3 -n clinic-booking

# Scale Doctor Service
kubectl scale deployment doctor-service --replicas=3 -n clinic-booking
```

### **Resource Limits**
Mỗi service có resource limits:
- **Requests**: CPU 200m, Memory 512Mi
- **Limits**: CPU 500m, Memory 1Gi

## 🗄️ **Database**

### **MySQL Configuration**
- **Image**: mysql:8.0
- **Storage**: 10Gi PersistentVolume
- **Databases**: user_db, doctor_db, appointment_db
- **User**: clinic_user / clinic_password

### **Data Persistence**
```bash
# Backup MySQL data
kubectl exec -it deployment/mysql -n clinic-booking -- mysqldump -u root -prootpassword --all-databases > backup.sql

# Restore MySQL data
kubectl exec -i deployment/mysql -n clinic-booking -- mysql -u root -prootpassword < backup.sql
```

## 🧹 **Cleanup**

### **Remove All Resources**
```bash
# Cleanup all Kubernetes resources
./scripts/cleanup-k8s.sh
```

### **Manual Cleanup**
```bash
# Delete namespace (removes all resources)
kubectl delete namespace clinic-booking

# Remove Docker images
docker rmi clinic-booking/*:latest
```

## 🐛 **Troubleshooting**

### **Check Pod Status**
```bash
kubectl get pods -n clinic-booking
kubectl describe pod <pod-name> -n clinic-booking
```

### **Check Service Status**
```bash
kubectl get services -n clinic-booking
kubectl describe service <service-name> -n clinic-booking
```

### **View Logs**
```bash
# View logs for specific service
kubectl logs -f deployment/user-service -n clinic-booking

# View logs for all pods
kubectl logs -f -l app=user-service -n clinic-booking
```

### **Common Issues**

1. **Pod not starting**: Check resource limits and health checks
2. **Database connection failed**: Verify MySQL is running and accessible
3. **Service discovery issues**: Check Eureka service registration
4. **Config not loading**: Verify Config Service is accessible

## 🔐 **Security**

### **Network Policies**
```yaml
# Example NetworkPolicy to restrict traffic
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: clinic-network-policy
  namespace: clinic-booking
spec:
  podSelector: {}
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - namespaceSelector:
        matchLabels:
          name: clinic-booking
```

### **Secrets Management**
```bash
# Create secret for database password
kubectl create secret generic mysql-secret \
  --from-literal=password=clinic_password \
  -n clinic-booking
```

## 📈 **Performance Optimization**

### **Resource Optimization**
- Adjust CPU/Memory requests based on actual usage
- Use node affinity for better resource utilization
- Implement pod disruption budgets for high availability

### **Database Optimization**
- Use connection pooling
- Implement read replicas for read-heavy workloads
- Monitor database performance metrics

## 🎯 **Next Steps**

1. **Implement CI/CD**: GitHub Actions hoặc GitLab CI
2. **Add Monitoring**: Prometheus + Grafana
3. **Implement Logging**: ELK Stack hoặc Fluentd
4. **Add Security**: Istio Service Mesh
5. **Implement Backup**: Database backup strategies
6. **Add Testing**: Integration tests với Kubernetes
