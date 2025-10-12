package com.myclinic.doctor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "doctor_booking_policies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorBookingPolicy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "doctor_id", unique = true, nullable = false)
    private Integer doctorId;
    
    @Column(name = "booking_fee", nullable = false)
    private Integer bookingFee = 0;
    
    @Column(name = "cancel_policy_minutes", nullable = false)
    private Integer cancelPolicyMinutes = 0;
    
    @Column(name = "cancel_fee_percent", nullable = false)
    private Byte cancelFeePercent = 0;
    
    @Column(name = "default_duration_minutes", nullable = false)
    private Integer defaultDurationMinutes = 30;

    @Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

}
