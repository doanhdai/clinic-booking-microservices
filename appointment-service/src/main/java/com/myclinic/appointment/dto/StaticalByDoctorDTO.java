package com.myclinic.appointment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaticalByDoctorDTO {
    private Integer id;
    private String avatar;
    private String fullName;
    private String specialty; // ví dụ 'TIM,NHI'
    private Integer totalAppointments; // tổng bản ghi appointment
    private Integer completedAppointments; // finished + transferred
    private Integer revenue;
    private Integer transferred;
    private Integer remaining; // tính trong service, không phải field declaration
    private String status; // "Đã chuyển" - "Chờ chuyển" - "—"
}