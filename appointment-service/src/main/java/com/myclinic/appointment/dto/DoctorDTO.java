package com.myclinic.appointment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private String address;
    private Double lat;  // vi do
    private Double lng;  // kinh do
}
