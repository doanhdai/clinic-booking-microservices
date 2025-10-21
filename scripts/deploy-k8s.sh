#!/bin/bash

# Build and Deploy Clinic Booking Microservices to Kubernetes
# This script builds Docker images and deploys them to Kubernetes

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
REGISTRY="clinic-booking"
NAMESPACE="clinic-booking"

echo -e "${GREEN}🚀 Starting Clinic Booking Microservices Deployment${NC}"

# Function to print colored output
print_status() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    print_error "kubectl is not installed or not in PATH"
    exit 1
fi

# Check if docker is available
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed or not in PATH"
    exit 1
fi

print_status "Prerequisites check passed"

# Build Docker images
echo -e "${YELLOW}📦 Building Docker images...${NC}"

services=("discovery-service" "config-service" "user-service" "doctor-service" "appointment-service" "gateway-service")

for service in "${services[@]}"; do
    echo -e "${YELLOW}Building $service...${NC}"
    docker build -t $REGISTRY/$service:latest ./$service/
    print_status "Built $service"
done

print_status "All Docker images built successfully"

# Create namespace
echo -e "${YELLOW}🏗️  Creating Kubernetes namespace...${NC}"
kubectl apply -f k8s/namespace.yaml
print_status "Namespace created"

# Optional: Deploy MySQL if manifests exist (skip silently if removed)
if [ -f k8s/mysql.yaml ]; then
  echo -e "${YELLOW}🗄️  Deploying MySQL...${NC}"
  [ -f k8s/mysql-configmap.yaml ] && kubectl apply -f k8s/mysql-configmap.yaml || true
  [ -f k8s/mysql-init-configmap.yaml ] && kubectl apply -f k8s/mysql-init-configmap.yaml || true
  [ -f k8s/mysql-pvc.yaml ] && kubectl apply -f k8s/mysql-pvc.yaml || true
  kubectl apply -f k8s/mysql.yaml
  echo -e "${YELLOW}⏳ Waiting for MySQL to be ready...${NC}"
  kubectl wait --for=condition=ready pod -l app=mysql -n $NAMESPACE --timeout=300s || print_warning "MySQL wait skipped/failed"
fi

# Deploy Discovery Service
echo -e "${YELLOW}🔍 Deploying Discovery Service...${NC}"
kubectl apply -f k8s/discovery-service.yaml

echo -e "${YELLOW}⏳ Waiting for Discovery Service to be ready...${NC}"
kubectl wait --for=condition=ready pod -l app=discovery-service -n $NAMESPACE --timeout=300s
print_status "Discovery Service is ready"

# Deploy Config Service
echo -e "${YELLOW}⚙️  Deploying Config Service...${NC}"
kubectl apply -f k8s/config-service.yaml

echo -e "${YELLOW}⏳ Waiting for Config Service to be ready...${NC}"
kubectl wait --for=condition=ready pod -l app=config-service -n $NAMESPACE --timeout=300s
print_status "Config Service is ready"

# Deploy Microservices
echo -e "${YELLOW}🏥 Deploying Microservices...${NC}"
kubectl apply -f k8s/user-service.yaml
kubectl apply -f k8s/doctor-service.yaml
kubectl apply -f k8s/appointment-service.yaml
kubectl apply -f k8s/gateway-service.yaml

echo -e "${YELLOW}⏳ Waiting for microservices to be ready...${NC}"
kubectl wait --for=condition=ready pod -l app=user-service -n $NAMESPACE --timeout=300s || print_warning "User wait skipped/failed"
kubectl wait --for=condition=ready pod -l app=doctor-service -n $NAMESPACE --timeout=300s || print_warning "Doctor wait skipped/failed"
kubectl wait --for=condition=ready pod -l app=appointment-service -n $NAMESPACE --timeout=300s || print_warning "Appointment wait skipped/failed"
kubectl wait --for=condition=ready pod -l app=gateway-service -n $NAMESPACE --timeout=300s || print_warning "Gateway wait skipped/failed"

print_status "All services applied"

echo -e "${YELLOW}📊 Pods:${NC}"
kubectl get pods -n $NAMESPACE

echo -e "${YELLOW}🔗 Services:${NC}"
kubectl get svc -n $NAMESPACE

print_status "Deployment script completed"
