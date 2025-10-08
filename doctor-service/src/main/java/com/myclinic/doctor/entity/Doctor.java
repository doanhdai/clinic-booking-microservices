package com.myclinic.doctor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    
    @Id
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "specialization")
    private String specialization;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "bank_number")
    private String bankNumber;
    
    @Column(name = "bank_name")
    private String bankName;
    
    @Column(name = "bank_id")
    private String bankId;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
