package com.myclinic.doctor.service;

import com.myclinic.doctor.dto.DoctorBookingPolicyDTO;
import com.myclinic.doctor.entity.DoctorBookingPolicy;
import com.myclinic.doctor.mapper.DoctorBookingPolicyMapper;
import com.myclinic.doctor.repository.DoctorBookingPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorBookingPolicyService {
    
    private final DoctorBookingPolicyRepository policyRepository;
    private final DoctorBookingPolicyMapper policyMapper;
    
    /**
     * Get all booking policies
     */
    public List<DoctorBookingPolicyDTO> getAllPolicies() {
        log.info("Fetching all booking policies");
        List<DoctorBookingPolicy> policies = policyRepository.findAll();
        return policyMapper.toDtoList(policies);
    }
    
    /**
     * Get booking policy by ID
     */
    public DoctorBookingPolicyDTO getPolicyById(Integer id) {
        log.info("Fetching booking policy by id: {}", id);
        Optional<DoctorBookingPolicy> policy = policyRepository.findById(id);
        return policy.map(policyMapper::toDto).orElse(null);
    }
    
    /**
     * Get booking policy by doctor ID
     */
    public DoctorBookingPolicyDTO getPolicyByDoctorId(Integer doctorId) {
        log.info("Fetching booking policy for doctor: {}", doctorId);
        Optional<DoctorBookingPolicy> policy = policyRepository.findByDoctorId(doctorId);
        return policy.map(policyMapper::toDto).orElse(null);
    }
    
    /**
     * Create a new booking policy
     */
    @Transactional
    public DoctorBookingPolicyDTO createPolicy(DoctorBookingPolicyDTO policyDTO) {
        log.info("Creating new booking policy for doctor: {}", policyDTO.getDoctorId());
        
        // Check if policy already exists for this doctor
        if (policyRepository.existsByDoctorId(policyDTO.getDoctorId())) {
            log.warn("Booking policy already exists for doctor: {}", policyDTO.getDoctorId());
            throw new IllegalArgumentException("Booking policy already exists for this doctor");
        }
        
        DoctorBookingPolicy policy = policyMapper.toEntity(policyDTO);
        DoctorBookingPolicy savedPolicy = policyRepository.save(policy);
        return policyMapper.toDto(savedPolicy);
    }
    
    /**
     * Update an existing booking policy
     */
    @Transactional
    public DoctorBookingPolicyDTO updatePolicy(Integer id, DoctorBookingPolicyDTO policyDTO) {
        log.info("Updating booking policy with id: {}", id);
        Optional<DoctorBookingPolicy> existingPolicy = policyRepository.findById(id);
        
        if (existingPolicy.isPresent()) {
            DoctorBookingPolicy policy = existingPolicy.get();
            policyMapper.updateEntity(policy, policyDTO);
            DoctorBookingPolicy updatedPolicy = policyRepository.save(policy);
            return policyMapper.toDto(updatedPolicy);
        }
        
        log.warn("Booking policy with id {} not found", id);
        return null;
    }
    
    /**
     * Update booking policy by doctor ID
     */
    @Transactional
    public DoctorBookingPolicyDTO updatePolicyByDoctorId(Integer doctorId, DoctorBookingPolicyDTO policyDTO) {
        log.info("Updating booking policy for doctor: {}", doctorId);
        Optional<DoctorBookingPolicy> existingPolicy = policyRepository.findByDoctorId(doctorId);
        
        if (existingPolicy.isPresent()) {
            DoctorBookingPolicy policy = existingPolicy.get();
            policyMapper.updateEntity(policy, policyDTO);
            DoctorBookingPolicy updatedPolicy = policyRepository.save(policy);
            return policyMapper.toDto(updatedPolicy);
        }
        
        log.warn("Booking policy for doctor {} not found", doctorId);
        return null;
    }
    
    /**
     * Delete a booking policy
     */
    @Transactional
    public boolean deletePolicy(Integer id) {
        log.info("Deleting booking policy with id: {}", id);
        if (policyRepository.existsById(id)) {
            policyRepository.deleteById(id);
            return true;
        }
        log.warn("Booking policy with id {} not found", id);
        return false;
    }
    
    /**
     * Delete booking policy by doctor ID
     */
    @Transactional
    public boolean deletePolicyByDoctorId(Integer doctorId) {
        log.info("Deleting booking policy for doctor: {}", doctorId);
        Optional<DoctorBookingPolicy> policy = policyRepository.findByDoctorId(doctorId);
        
        if (policy.isPresent()) {
            policyRepository.delete(policy.get());
            return true;
        }
        
        log.warn("Booking policy for doctor {} not found", doctorId);
        return false;
    }
}
