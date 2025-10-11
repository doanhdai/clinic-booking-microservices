package com.myclinic.doctor.controller;

import com.myclinic.doctor.dto.DoctorRecurringScheduleDTO;
import com.myclinic.doctor.service.DoctorRecurringScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors/doctor-schedules")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DoctorRecurringScheduleController {
    
    private final DoctorRecurringScheduleService scheduleService;
    
    /**
     * GET /api/doctor-schedules - Get all schedules
     */
    @GetMapping
    public ResponseEntity<List<DoctorRecurringScheduleDTO>> getAllSchedules() {
        log.info("GET /api/doctor-schedules - Fetching all schedules");
        List<DoctorRecurringScheduleDTO> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * GET /api/doctor-schedules/{id} - Get schedule by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorRecurringScheduleDTO> getScheduleById(@PathVariable Integer id) {
        log.info("GET /api/doctor-schedules/{} - Fetching schedule by id", id);
        DoctorRecurringScheduleDTO schedule = scheduleService.getScheduleById(id);
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(schedule);
    }
    
    /**
     * GET /api/doctor-schedules/doctor/{doctorId} - Get all schedules for a doctor
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorRecurringScheduleDTO>> getSchedulesByDoctorId(
            @PathVariable Integer doctorId,
            @RequestParam(required = false) Boolean activeOnly) {
        log.info("GET /api/doctor-schedules/doctor/{} - Fetching schedules", doctorId);
        
        List<DoctorRecurringScheduleDTO> schedules;
        if (activeOnly != null && activeOnly) {
            schedules = scheduleService.getActiveSchedulesByDoctorId(doctorId);
        } else {
            schedules = scheduleService.getSchedulesByDoctorId(doctorId);
        }
        
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * GET /api/doctor-schedules/doctor/{doctorId}/weekday/{weekday} - Get schedules by doctor and weekday
     */
    @GetMapping("/doctor/{doctorId}/weekday/{weekday}")
    public ResponseEntity<List<DoctorRecurringScheduleDTO>> getSchedulesByDoctorIdAndWeekday(
            @PathVariable Integer doctorId,
            @PathVariable Byte weekday,
            @RequestParam(required = false) Boolean activeOnly) {
        log.info("GET /api/doctor-schedules/doctor/{}/weekday/{} - Fetching schedules", doctorId, weekday);
        
        List<DoctorRecurringScheduleDTO> schedules;
        if (activeOnly != null && activeOnly) {
            schedules = scheduleService.getActiveSchedulesByDoctorIdAndWeekday(doctorId, weekday);
        } else {
            schedules = scheduleService.getSchedulesByDoctorIdAndWeekday(doctorId, weekday);
        }
        
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * POST /api/doctor-schedules - Create a new schedule
     */
    @PostMapping
    public ResponseEntity<DoctorRecurringScheduleDTO> createSchedule(
            @RequestBody DoctorRecurringScheduleDTO scheduleDTO) {
        log.info("POST /api/doctor-schedules - Creating new schedule");
        try {
            DoctorRecurringScheduleDTO createdSchedule = scheduleService.createSchedule(scheduleDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
        } catch (Exception e) {
            log.error("Error creating schedule: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * PUT /api/doctor-schedules/{id} - Update a schedule
     */
    @PutMapping("/{id}")
    public ResponseEntity<DoctorRecurringScheduleDTO> updateSchedule(
            @PathVariable Integer id,
            @RequestBody DoctorRecurringScheduleDTO scheduleDTO) {
        log.info("PUT /api/doctor-schedules/{} - Updating schedule", id);
        DoctorRecurringScheduleDTO updatedSchedule = scheduleService.updateSchedule(id, scheduleDTO);
        
        if (updatedSchedule == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(updatedSchedule);
    }
    
    /**
     * DELETE /api/doctor-schedules/{id} - Delete a schedule
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
        log.info("DELETE /api/doctor-schedules/{} - Deleting schedule", id);
        boolean deleted = scheduleService.deleteSchedule(id);
        
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.noContent().build();
    }
    
    /**
     * DELETE /api/doctor-schedules/doctor/{doctorId} - Delete all schedules for a doctor
     */
    @DeleteMapping("/doctor/{doctorId}")
    public ResponseEntity<Void> deleteSchedulesByDoctorId(@PathVariable Integer doctorId) {
        log.info("DELETE /api/doctor-schedules/doctor/{} - Deleting all schedules", doctorId);
        scheduleService.deleteSchedulesByDoctorId(doctorId);
        return ResponseEntity.noContent().build();
    }
}
