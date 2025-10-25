package com.myclinic.appointment.service;

import com.myclinic.appointment.client.DoctorClient;
import com.myclinic.appointment.client.UserClient;
import com.myclinic.appointment.dto.AppointmentDTO;
import com.myclinic.appointment.dto.DoctorDTO;
import com.myclinic.appointment.dto.UserInfoDTO;
import com.myclinic.appointment.dto.UpdateAppointmentRequest;
import com.myclinic.appointment.entity.Appointment;
import com.myclinic.appointment.mapper.AppointmentMapper;
import com.myclinic.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    @PersistenceContext
    private EntityManager entityManager;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final DoctorClient doctorClient;
    private final UserClient userClient;

    //Lấy tất cả appointment
    public List<AppointmentDTO> getAllAppointments() {
        log.info("Fetching all appointments");
        List<Appointment> appointments = appointmentRepository.findAll();
        return enrichAppointments(appointments);
    }

    //Lấy danh sách cuộc hẹn của 1 bác sĩ
    public List<AppointmentDTO> getAppointmentsByDoctor(Integer doctorId) {
        log.info("Fetching appointments for doctor {}", doctorId);
        List<Appointment> appointments = appointmentRepository.findAppointmentsByDoctorId(doctorId);
        return enrichAppointments(appointments);
    }

    //Lấy appointment theo ID
    public AppointmentDTO getAppointmentById(Integer id) {
        log.info("Fetching appointment with id {}", id);
        Optional<Appointment> opt = appointmentRepository.findById(id);
        if (opt.isEmpty()) return null;
        return enrichAppointment(opt.get());
    }

    //Kiểm tra trùng lịch
    public boolean findConflictingAppointments(AppointmentDTO dto) {
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
                dto.getDoctorId(),
                dto.getAppointmentStarttime(),
                dto.getAppointmentEndtime()
        );
        return !conflicts.isEmpty();
    }

    //Tạo mới appointment
    @Transactional
    public AppointmentDTO createAppointment(AppointmentDTO dto) {
        boolean existAppointment = findConflictingAppointments(dto);
        if (existAppointment) {
            throw new IllegalStateException("Trùng lịch");
        }
        Appointment entity = appointmentMapper.toEntity(dto);
        Appointment saved = appointmentRepository.save(entity);
        entityManager.refresh(saved);
        return enrichAppointment(saved);
    }

    //Cập nhật trạng thái appointment
    @Transactional
    public AppointmentDTO updateStatus(Integer id, String newStatus) {
        log.info("Updating status of appointment {} -> {}", id, newStatus);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + id));

        appointment.setStatus(Appointment.Status.valueOf(newStatus));
        appointment.setUpdatedAt(LocalDateTime.now());

        Appointment updated = appointmentRepository.save(appointment);
        entityManager.refresh(updated);
        return enrichAppointment(updated);
    }

    //Lấy danh sách cuộc hẹn trong khoảng thời gian cụ thể
    public List<AppointmentDTO> findAppointmentsByInRange(LocalDateTime start, LocalDateTime end) {
        List<Appointment> appointments = appointmentRepository.findAppointmentsByInRange(start, end);

        if (appointments.isEmpty())
            return List.of();

        // enrich doctor + patient info
        return enrichAppointments(appointments);
    }

    //Lấy danh sách cuộc hẹn của 1 bệnh nhân
    public List<AppointmentDTO> getAppointmentsByPatient(Integer patientId) {
        log.info("Fetching appointments for patient {}", patientId);

        List<Appointment> appointments = appointmentRepository.findAll()
                .stream()
                .filter(a -> a.getPatientId().equals(patientId))
                .toList();

        if (appointments.isEmpty()) {
            log.info("No appointments found for patient {}", patientId);
            return List.of();
        }

        return enrichAppointments(appointments);
    }

    //Ghép thêm thông tin bác sĩ và bệnh nhân vào AppointmentDTO
    private AppointmentDTO enrichAppointment(Appointment entity) {
        DoctorDTO doctor = null;
        UserInfoDTO patient = null;

        try {
            doctor = doctorClient.getDoctorById(entity.getDoctorId());
        } catch (Exception e) {
            log.warn("Cannot fetch doctor info for id={}", entity.getDoctorId());
        }

        try {
            patient = userClient.getUserById(entity.getPatientId());
        } catch (Exception e) {
            log.warn("Cannot fetch patient info for id={}", entity.getPatientId());
        }

        return appointmentMapper.toDto(entity, doctor, patient);
    }

    //Ghép thêm thông tin bác sĩ và bệnh nhân vào List<AppointmentDTO>
    private List<AppointmentDTO> enrichAppointments(List<Appointment> entities) {
        return entities.stream().map(this::enrichAppointment).collect(Collectors.toList());
    }
 
    @Transactional
    public AppointmentDTO updateAppointment(Integer appointmentId, UpdateAppointmentRequest request) {
        // Validate request has at least one update
        if (!request.hasAnyUpdate()) {
            throw new RuntimeException("At least one field must be provided for update");
        }
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + appointmentId));
        
        // Update status if provided
        if (request.hasStatusUpdate()) {
            updateAppointmentStatus(appointment, request);
        }
        
        // Update time if provided
        if (request.hasTimeUpdate()) {
            updateAppointmentTime(appointment, request);
        }
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        entityManager.refresh(savedAppointment);
        
        return enrichAppointment(savedAppointment);
    }
    
    private boolean updateAppointmentStatus(Appointment appointment, UpdateAppointmentRequest request) {
        // Validate status
        List<String> validStatuses = Arrays.asList("pending", "paid", "cancelled", "finished", "transferred");
        if (!validStatuses.contains(request.getStatus())) {
            throw new RuntimeException("Invalid status: " + request.getStatus());
        }
        
        appointment.setStatus(Appointment.Status.valueOf(request.getStatus()));
        
        // Set end time if finishing appointment
        if ("finished".equals(request.getStatus()) && appointment.getAppointmentEndtime() == null) {
            appointment.setAppointmentEndtime(LocalDateTime.now());
        }
        
        return true;
    }
    
    private boolean updateAppointmentTime(Appointment appointment, UpdateAppointmentRequest request) {
        // Validate new time is in the future
        if (request.getNewAppointmentStarttime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("New appointment time must be in the future");
        }
        
        appointment.setAppointmentStarttime(request.getNewAppointmentStarttime());
        
        if (request.getNewAppointmentEndtime() != null) {
            appointment.setAppointmentEndtime(request.getNewAppointmentEndtime());
        } else {
            // Calculate end time based on default duration (30 minutes)
            appointment.setAppointmentEndtime(request.getNewAppointmentStarttime().plusMinutes(30));
        }
        
        // Update status to pending if it was cancelled
        if (appointment.getStatus() == Appointment.Status.cancelled) {
            appointment.setStatus(Appointment.Status.pending);
        }
        
        return true;
    }
}