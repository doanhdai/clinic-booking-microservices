package com.myclinic.appointment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UpdateAppointmentRequest {
    // Status fields
    private String status;
    
    // Time fields
    private LocalDateTime newAppointmentStarttime;
    private LocalDateTime newAppointmentEndtime;
    // Validation method
    public boolean hasStatusUpdate() {
        return status != null && !status.trim().isEmpty();
    }
    
    public boolean hasTimeUpdate() {
        return newAppointmentStarttime != null;
    }
    
    public boolean hasAnyUpdate() {
        return hasStatusUpdate() || hasTimeUpdate();
    }
}
