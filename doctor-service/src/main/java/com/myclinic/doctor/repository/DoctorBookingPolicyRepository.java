package com.myclinic.doctor.repository;

import com.myclinic.doctor.entity.DoctorBookingPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorBookingPolicyRepository extends JpaRepository<DoctorBookingPolicy, Integer> {
    
    Optional<DoctorBookingPolicy> findByDoctorId(Integer doctorId);
    
    boolean existsByDoctorId(Integer doctorId);
}
