package com.myclinic.appointment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "patient_id", nullable = false)
    private Integer patientId;

    @Column(name = "doctor_id", nullable = false)
    private Integer doctorId;

    @Column(name = "appointment_starttime", nullable = false)
    private LocalDateTime appointmentStarttime;

    @Column(name = "appointment_endtime")
    private LocalDateTime appointmentEndtime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('pending', 'paid', 'cancelled', 'finished', 'transferred') DEFAULT 'pending'")
    private Status status = Status.pending;

    @Column(name = "booking_fee", columnDefinition = "INT DEFAULT 0")
    private Integer bookingFee = 0;

    @Column(name = "cancel_policy_minutes", columnDefinition = "INT DEFAULT 0")
    private Integer cancelPolicyMinutes = 0;

    @Column(name = "cancel_fee_percent", columnDefinition = "TINYINT DEFAULT 0")
    private Integer cancelFeePercent = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    // ENUM cho cột status
    public enum Status {
        pending,
        paid,
        cancelled,
        finished,
        transferred
    }
}
