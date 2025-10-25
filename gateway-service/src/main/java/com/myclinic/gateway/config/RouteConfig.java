package com.myclinic.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service routes
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .uri("lb://USER-SERVICE"))
                //login,register
                .route("user-service", r-> r
                        .path("/api/auth/**")
                        .uri("lb://USER-SERVICE")
                )
                // Doctor Service routes
                .route("doctor-service", r -> r
                        .path("/api/doctors/**")
                        .uri("lb://DOCTOR-SERVICE"))
                
                // Appointment Service routes
                .route("appointment-service", r -> r
                        .path("/api/appointments/**")
                        .uri("lb://APPOINTMENT-SERVICE"))
                
                // Health check route
                .route("health-check", r -> r
                        .path("/health")
                        .filters(f -> f.setPath("/actuator/health"))
                        .uri("lb://USER-SERVICE"))
                
                .build();
    }
}
