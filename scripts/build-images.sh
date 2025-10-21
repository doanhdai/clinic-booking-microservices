# Build Docker Images for Clinic Booking Microservices
# This script builds Docker images for all microservices

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
REGISTRY="clinic-booking-microservices"

echo -e "${GREEN}🐳 Building Docker Images for Clinic Booking Microservices${NC}"

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

print_status "Docker prerequisites check passed"

# Build Docker images
echo -e "${YELLOW}📦 Building Docker images...${NC}"

services=("discovery-service" "config-service" "user-service" "doctor-service" "appointment-service" "gateway-service")
dockerfiles=("Dockerfile.discovery" "Dockerfile.config" "Dockerfile.user" "Dockerfile.doctor" "Dockerfile.appointment" "Dockerfile.gateway")

for i in "${!services[@]}"; do
    service="${services[$i]}"
    dockerfile="${dockerfiles[$i]}"
    
    echo -e "${YELLOW}Building $service with $dockerfile...${NC}"
    
    # Check if service directory exists
    if [ ! -d "./$service" ]; then
        print_error "Service directory $service not found"
        continue
    fi
    
    # Check if Dockerfile exists
    if [ ! -f "./$service/$dockerfile" ]; then
        print_error "Dockerfile $dockerfile not found in $service"
        continue
    fi
    
    # Build the image
    docker build -f ./$service/$dockerfile -t $REGISTRY/$service:latest ./$service/
    
    if [ $? -eq 0 ]; then
        print_status "Successfully built $service"
    else
        print_error "Failed to build $service"
        exit 1
    fi
done

print_status "All Docker images built successfully"

# List built images
echo -e "${YELLOW}📋 Built Docker images:${NC}"
docker images | grep $REGISTRY

print_status "Build script completed"
echo -e "${GREEN}🎉 All Docker images are ready for deployment${NC}"
