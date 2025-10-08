package com.myclinic.doctor.repository;

import com.myclinic.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    
    @Query("SELECT d FROM Doctor d WHERE d.specialization LIKE %:specialization%")
    List<Doctor> findBySpecializationContaining(@Param("specialization") String specialization);
    
    @Query("SELECT d FROM Doctor d WHERE d.description LIKE %:keyword%")
    List<Doctor> findByDescriptionContaining(@Param("keyword") String keyword);
    
    @Query("SELECT d FROM Doctor d WHERE d.specialization LIKE %:specialization% AND d.description LIKE %:keyword%")
    List<Doctor> findBySpecializationAndDescriptionContaining(@Param("specialization") String specialization, 
                                                             @Param("keyword") String keyword);
}
