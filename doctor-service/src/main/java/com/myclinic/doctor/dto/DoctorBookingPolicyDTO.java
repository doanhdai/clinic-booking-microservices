package com.myclinic.doctor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorBookingPolicyDTO {
    
    private Integer id;
    private Integer doctorId;
    private Integer bookingFee;
    private Integer cancelPolicyMinutes;
    private Byte cancelFeePercent;
    private Integer defaultDurationMinutes;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
