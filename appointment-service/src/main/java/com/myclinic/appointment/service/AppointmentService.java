package com.myclinic.appointment.service;

import com.myclinic.appointment.client.DoctorClient;
import com.myclinic.appointment.client.UserClient;
import com.myclinic.appointment.dto.AppointmentDTO;
import com.myclinic.appointment.dto.DoctorDTO;
import com.myclinic.appointment.dto.UserInfoDTO;
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

    /**
     * Lấy tất cả appointment
     */
    public List<AppointmentDTO> getAllAppointments() {
        log.info("Fetching all appointments");
        List<Appointment> appointments = appointmentRepository.findAll();
        return enrichAppointments(appointments);
    }

    /**
     * Lấy appointment theo ID
     */
    public AppointmentDTO getAppointmentById(Integer id) {
        log.info("Fetching appointment with id {}", id);
        Optional<Appointment> opt = appointmentRepository.findById(id);
        if (opt.isEmpty()) return null;
        return enrichAppointment(opt.get());
    }

    /**
     * Kiểm tra trùng lịch
     */
    public boolean findConflictingAppointments(AppointmentDTO dto) {
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
                dto.getDoctorId(),
                dto.getAppointmentStarttime(),
                dto.getAppointmentEndtime()
        );
        return !conflicts.isEmpty();
    }

    /**
     * Tạo mới appointment
     */
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

    /**
     * Cập nhật trạng thái appointment
     */
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

    /**
     * 🔹 Lấy danh sách cuộc hẹn trong khoảng thời gian cụ thể
     */
    public List<AppointmentDTO> findAppointmentsByInRange(LocalDateTime start, LocalDateTime end) {
        List<Appointment> appointments = appointmentRepository.findAppointmentsByInRange(start, end);

        if (appointments.isEmpty())
            return List.of();

        // enrich doctor + patient info
        return enrichAppointments(appointments);
    }

    /**
     * 🔹 Lấy danh sách cuộc hẹn của 1 bệnh nhân
     */
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

    /**
     * GHÉP thêm thông tin bác sĩ và bệnh nhân vào AppointmentDTO
     */
    private AppointmentDTO enrichAppointment(Appointment entity) {
        AppointmentDTO dto = appointmentMapper.toDto(entity);

        try {
            DoctorDTO doctor = doctorClient.getDoctorById(entity.getDoctorId());
            dto.setDoctor(doctor);
        } catch (Exception e) {
            log.warn("Cannot fetch doctor info for id={}", entity.getDoctorId());
        }

        try {
            UserInfoDTO patient = userClient.getUserById(entity.getPatientId());
            dto.setPatient(patient);
        } catch (Exception e) {
            log.warn("Cannot fetch patient info for id={}", entity.getPatientId());
        }

        return dto;
    }

    private List<AppointmentDTO> enrichAppointments(List<Appointment> entities) {
        return entities.stream().map(this::enrichAppointment).collect(Collectors.toList());
    }
}
