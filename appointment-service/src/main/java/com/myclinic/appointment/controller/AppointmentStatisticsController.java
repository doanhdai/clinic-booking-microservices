package com.myclinic.appointment.controller;

import com.myclinic.appointment.dto.DashboardDataDTO;
import com.myclinic.appointment.dto.StaticalByDoctorDTO;
import com.myclinic.appointment.service.AppointmentStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments/statistics")
//@CrossOrigin(origins = "*")
public class AppointmentStatisticsController {

    @Autowired
    private AppointmentStatisticsService statisticsService;

    // Tab 1: Dashboard Overview
    @GetMapping("/dashboard-overview")
    public ResponseEntity<DashboardDataDTO> getDashboardOverview(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String specialty) {

        DashboardDataDTO result = statisticsService.getDashboardOverview(fromDate, toDate, specialty);
        return ResponseEntity.ok(result);
    }

    // Tab 2: Doctor Statistics
    @GetMapping("/doctor-statistics")
    public ResponseEntity<List<StaticalByDoctorDTO>> getDoctorStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String doctorName) {

        List<StaticalByDoctorDTO> result = statisticsService.getDoctorStatistics(fromDate, toDate, specialty, doctorName);
        return ResponseEntity.ok(result);
    }

    //thống kế bên tài khoản doctor
    @GetMapping("/my-dashboard")
    public ResponseEntity<DashboardDataDTO> getMyDashboard(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam @DateTimeFormat Integer doctorId
//            @RequestHeader("Authorization") String token // JWT token
    ) {
//        Integer doctorId = statisticsService.getDoctorIdFromToken(token); // lấy doctorId từ token
        DashboardDataDTO result = statisticsService.getDashboardOverviewByDoctor(fromDate, toDate, doctorId);
        return ResponseEntity.ok(result);
    }

}