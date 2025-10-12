package com.myclinic.doctor.controller;

import com.myclinic.doctor.dto.DoctorBookingPolicyDTO;
import com.myclinic.doctor.service.DoctorBookingPolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/doctors/booking-policies")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DoctorBookingPolicyController {
    
    private final DoctorBookingPolicyService policyService;
    
    /**
     * GET /api/booking-policies - Get all booking policies
     */
    @GetMapping
    public ResponseEntity<List<DoctorBookingPolicyDTO>> getAllPolicies() {
        log.info("GET /api/booking-policies - Fetching all booking policies");
        List<DoctorBookingPolicyDTO> policies = policyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }
    
    /**
     * GET /api/booking-policies/{id} - Get booking policy by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorBookingPolicyDTO> getPolicyById(@PathVariable Integer id) {
        log.info("GET /api/booking-policies/{} - Fetching booking policy by id", id);
        DoctorBookingPolicyDTO policy = policyService.getPolicyById(id);
        if (policy == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(policy);
    }
    
    /**
     * GET /api/booking-policies/doctor/{doctorId} - Get booking policy by doctor ID
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<DoctorBookingPolicyDTO> getPolicyByDoctorId(@PathVariable Integer doctorId) {
        log.info("GET /api/booking-policies/doctor/{} - Fetching booking policy", doctorId);
        DoctorBookingPolicyDTO policy = policyService.getPolicyByDoctorId(doctorId);
        if (policy == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(policy);
    }
    
    /**
     * POST /api/booking-policies - Create a new booking policy
     */
    @PostMapping
    public ResponseEntity<?> createPolicy(@RequestBody DoctorBookingPolicyDTO policyDTO) {
        log.info("POST /api/booking-policies - Creating new booking policy");
        try {
            DoctorBookingPolicyDTO createdPolicy = policyService.createPolicy(policyDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPolicy);
        } catch (IllegalArgumentException e) {
            log.error("Error creating policy: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating policy: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * PUT /api/booking-policies/{id} - Update a booking policy by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<DoctorBookingPolicyDTO> updatePolicy(
            @PathVariable Integer id,
            @RequestBody DoctorBookingPolicyDTO policyDTO) {
        log.info("PUT /api/booking-policies/{} - Updating booking policy", id);
        DoctorBookingPolicyDTO updatedPolicy = policyService.updatePolicy(id, policyDTO);
        
        if (updatedPolicy == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(updatedPolicy);
    }
    
    /**
     * PUT /api/booking-policies/doctor/{doctorId} - Update booking policy by doctor ID
     */
    @PutMapping("/doctor/{doctorId}")
    public ResponseEntity<DoctorBookingPolicyDTO> updatePolicyByDoctorId(
            @PathVariable Integer doctorId,
            @RequestBody DoctorBookingPolicyDTO policyDTO) {
        log.info("PUT /api/booking-policies/doctor/{} - Updating booking policy", doctorId);
        DoctorBookingPolicyDTO updatedPolicy = policyService.updatePolicyByDoctorId(doctorId, policyDTO);
        
        if (updatedPolicy == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(updatedPolicy);
    }
    
    /**
     * DELETE /api/booking-policies/{id} - Delete a booking policy by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable Integer id) {
        log.info("DELETE /api/booking-policies/{} - Deleting booking policy", id);
        boolean deleted = policyService.deletePolicy(id);
        
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * DELETE /api/booking-policies/doctor/{doctorId} - Delete booking policy by doctor ID
     */
    @DeleteMapping("/doctor/{doctorId}")
    public ResponseEntity<Void> deletePolicyByDoctorId(@PathVariable Integer doctorId) {
        log.info("DELETE /api/booking-policies/doctor/{} - Deleting booking policy", doctorId);
        boolean deleted = policyService.deletePolicyByDoctorId(doctorId);
        
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.noContent().build();
    }
}
