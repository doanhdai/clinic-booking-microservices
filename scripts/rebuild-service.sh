#!/bin/bash
# Rebuild specific service or all services
# Usage: ./rebuild-service.sh [service-name]
# Example: ./rebuild-service.sh user-service
# Or: ./rebuild-service.sh (rebuild all)

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
REGISTRY="clinic-booking-microservices"

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

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Check if docker is available
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed or not in PATH"
    exit 1
fi

# Check if Docker daemon is running
if ! docker info &> /dev/null; then
    print_error "Docker daemon is not running"
    exit 1
fi

# Service configuration
declare -A services
services=(
    ["discovery-service"]="Dockerfile.discovery"
    ["config-service"]="Dockerfile.config"
    ["user-service"]="Dockerfile.user"
    ["doctor-service"]="Dockerfile.doctor"
    ["appointment-service"]="Dockerfile.appointment"
    ["gateway-service"]="Dockerfile.gateway"
)

# Function to rebuild a specific service
rebuild_service() {
    local service=$1
    local dockerfile=${services[$service]}
    
    if [ -z "$dockerfile" ]; then
        print_error "Unknown service: $service"
        return 1
    fi
    
    print_info "Rebuilding $service..."
    
    # Check if service directory exists
    if [ ! -d "./$service" ]; then
        print_error "Service directory $service not found"
        return 1
    fi
    
    # Check if Dockerfile exists
    if [ ! -f "./$service/$dockerfile" ]; then
        print_error "Dockerfile $dockerfile not found in $service"
        return 1
    fi
    
    # Build the image (overwrite existing)
    docker build -f ./$service/$dockerfile -t $REGISTRY/$service:latest ./$service/
    
    if [ $? -eq 0 ]; then
        print_status "Successfully rebuilt $service"
        
        # Restart the container if it's running
        if docker ps -a --format '{{.Names}}' | grep -q "clinic-${service#*-}"; then
            container_name="clinic-${service#*-}"
            print_info "Restarting container $container_name..."
            docker-compose restart ${service}
            print_status "Container $container_name restarted"
        fi
    else
        print_error "Failed to rebuild $service"
        return 1
    fi
}

# Main script
echo -e "${GREEN}🔧 Service Rebuild Script${NC}"

if [ -z "$1" ]; then
    # Rebuild all services
    print_info "Rebuilding all services..."
    for service in "${!services[@]}"; do
        rebuild_service "$service"
    done
    print_status "All services rebuilt successfully"
else
    # Rebuild specific service
    rebuild_service "$1"
fi

# Show current images
echo -e "${YELLOW}📋 Current Docker images:${NC}"
docker images | grep $REGISTRY | head -10

print_status "Rebuild completed"

