package com.myclinic.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GatewayController {
    
    @GetMapping("/gateway/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "API Gateway");
        health.put("timestamp", LocalDateTime.now());
        health.put("message", "Gateway is running");
        return health;
    }
    
    @GetMapping("/gateway/info")
    public Map<String, Object> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Clinic Booking API Gateway");
        info.put("version", "1.0.0");
        info.put("description", "API Gateway for Clinic Booking Microservices");
        info.put("routes", new String[]{
            "/api/users/** -> USER-SERVICE",
            "/api/doctors/** -> DOCTOR-SERVICE", 
            "/api/appointments/** -> APPOINTMENT-SERVICE"
        });
        return info;
    }
}
