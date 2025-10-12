package com.myclinic.doctor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

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
}
