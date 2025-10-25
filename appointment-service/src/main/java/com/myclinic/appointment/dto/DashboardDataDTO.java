package com.myclinic.appointment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDataDTO {
    // Overview stats
    private Integer completedAppointments;
    private Integer upcomingAppointments;
    private Integer totalCancelled;
    private Integer totalRevenue;

    // Chart data - dùng generic structure
    private List<ChartDataPointDTO> revenueTrends;
    private List<ChartDataPointDTO> statusDistribution;

    //Các thuộc tính khác bên thống kê doctor
    private Integer transferred;
    private Integer remaining;
    private Integer totalAppointments;
}