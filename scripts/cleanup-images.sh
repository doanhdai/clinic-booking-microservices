#!/bin/bash
# Cleanup old/unused Docker images
# This script removes dangling and old images

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

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

echo -e "${GREEN}🧹 Cleaning up Docker images${NC}"

# Remove dangling images (images with <none> tag)
print_warning "Removing dangling images..."
docker image prune -f

# Remove unused images
if [ "$1" == "--all" ]; then
    print_warning "Removing all unused images..."
    docker image prune -a -f
fi

# Show remaining images
echo -e "${YELLOW}📋 Remaining clinic-booking images:${NC}"
docker images | grep clinic-booking

print_status "Cleanup completed"

