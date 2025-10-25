package com.myclinic.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "birth")
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", columnDefinition = "ENUM('male', 'female', 'other') DEFAULT 'female'")
    private Gender gender = Gender.female;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "phone", nullable = false, unique = true, length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "ENUM('patient', 'doctor', 'admin') DEFAULT 'patient'")
    private Role role = Role.patient;

    @CreationTimestamp
    @Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private Integer status = 1;

    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "password", nullable = false, unique = true, length = 255)
    private String password;

    public enum Gender {
        male, female, other
    }

    public enum Role {
        patient, doctor, admin
    }
}
