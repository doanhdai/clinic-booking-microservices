package com.myclinic.doctor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.myclinic.doctor.entity.DoctorRecurringSchedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRecurringScheduleDTO {
    
    private Integer id;
    private Integer doctorId;
    private Byte weekday;
    
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;
    
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private DoctorRecurringSchedule.ScheduleStatus status;
}
