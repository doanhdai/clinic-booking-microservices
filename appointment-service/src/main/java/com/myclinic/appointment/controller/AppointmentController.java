package com.myclinic.appointment.controller;

import com.myclinic.appointment.dto.AppointmentDTO;
import com.myclinic.appointment.dto.UpdateAppointmentRequest;
import com.myclinic.appointment.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {

    private final AppointmentService appointmentService;

    //Lấy tất cả các cuộc hẹn
    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        log.info("GET /api/appointments - Fetching all appointments");
        List<AppointmentDTO> list = appointmentService.getAllAppointments();
        return ResponseEntity.ok(list);
    }

    //Lấy danh sách cuộc hẹn của 1 bác sĩ
    @GetMapping("/docters/{doctorId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDoctor(@PathVariable("doctorId") Integer doctorId) {
        log.info("GET /api/appointments/docters/{} - Fetching appointments for doctor", doctorId);
        List<AppointmentDTO> list = appointmentService.getAppointmentsByDoctor(doctorId);
        return ResponseEntity.ok(list);
    }
    

    //Lấy chi tiết 1 cuộc hẹn theo ID
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable("id") Integer id) {
        log.info("GET /api/appointments/{} - Fetching appointment", id);
        AppointmentDTO dto = appointmentService.getAppointmentById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    //Tạo mới cuộc hẹn
    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentDTO dto) {
        log.info("POST /api/appointments - Creating new appointment for doctor {} / patient {}",
                dto.getDoctorId(), dto.getPatientId());
        try {
            AppointmentDTO saved = appointmentService.createAppointment(dto);
            return ResponseEntity.ok(saved);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Đã xảy ra lỗi hệ thống");
        }
    }

    //Thay đổi trạng thái cuộc hẹn
    @PatchMapping("/{id}")
    public ResponseEntity<AppointmentDTO> updateStatusAppointmentById(@PathVariable("id") Integer id, @RequestBody String status) {
        AppointmentDTO dto = appointmentService.updateStatus(id,status);
        return ResponseEntity.ok(dto);
    }

    //Lấy danh sách lịch hẹn của 1 bệnh nhân
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByPatient(@PathVariable("patientId") Integer patientId) {
        log.info("GET /api/appointments/patient/{} - Fetching appointments for patient", patientId);
        List<AppointmentDTO> list = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(list);
    }

    //Lấy danh sách cuộc hẹn trong khoảng thời gian
    @GetMapping("/search")
    public ResponseEntity<List<AppointmentDTO>> findAppointmentsByInRange(
            @RequestParam(value = "start", required = true) LocalDateTime start,
            @RequestParam(value = "end", required = true) LocalDateTime end
    ) {
        List<AppointmentDTO> list = appointmentService.findAppointmentsByInRange(start, end);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/update-appointment/{appointmentId}")
    public ResponseEntity<AppointmentDTO> updateAppointmentByAdmin(
            @PathVariable Integer appointmentId,
            @RequestBody @Valid UpdateAppointmentRequest request) {
        AppointmentDTO updatedAppointment = appointmentService.updateAppointment(appointmentId, request);
        return ResponseEntity.ok(updatedAppointment);
    }
}
