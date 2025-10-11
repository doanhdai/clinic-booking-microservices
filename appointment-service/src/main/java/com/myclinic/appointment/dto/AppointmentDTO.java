package com.myclinic.appointment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Appointment entity
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {

    private Integer id;
    private Integer patientId;
    private Integer doctorId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentStarttime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentEndtime;

    private String status; // pending, paid, cancelled, finished, transferred

    private Integer bookingFee;
    private Integer cancelPolicyMinutes;
    private Integer cancelFeePercent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // Nếu muốn trả về thêm thông tin bác sĩ hoặc bệnh nhân
    private DoctorDTO doctor;
    private UserInfoDTO patient;
}
