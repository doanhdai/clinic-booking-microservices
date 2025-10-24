package com.myclinic.doctor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DoctorDTO {
    
    private Integer userId;
    private String specialization;
    private String description;
    private String address;
    private String bankNumber;
    private String bankName;
    private String bankId;
    
    // Thông tin user từ user-service (sẽ được enrich qua Feign client)
    private String fullName;
    private LocalDate birth;
    private String gender;
    private String email;
    private String phone;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    private Integer status;
    private String avatar;
}
