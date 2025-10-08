package com.myclinic.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    
    private Integer userId;
    private String specialization;
    private String description;
    private String bankNumber;
    private String bankName;
    private String bankId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Thông tin user từ user-service (sẽ được populate qua Feign client)
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String avatar;
    private String gender;
    private String birth;
}
