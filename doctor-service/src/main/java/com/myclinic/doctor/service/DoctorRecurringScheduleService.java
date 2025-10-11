package com.myclinic.doctor.service;

import com.myclinic.doctor.dto.DoctorRecurringScheduleDTO;
import com.myclinic.doctor.entity.DoctorRecurringSchedule;
import com.myclinic.doctor.mapper.DoctorRecurringScheduleMapper;
import com.myclinic.doctor.repository.DoctorRecurringScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorRecurringScheduleService {

    private final DoctorRecurringScheduleRepository scheduleRepository;
    private final DoctorRecurringScheduleMapper scheduleMapper;
    
    /**
     * Get all schedules
     */
    public List<DoctorRecurringScheduleDTO> getAllSchedules() {
        log.info("Fetching all recurring schedules");
        List<DoctorRecurringSchedule> schedules = scheduleRepository.findAll();
        return scheduleMapper.toDtoList(schedules);
    }
    
    /**
     * Get schedule by ID
     */
    public DoctorRecurringScheduleDTO getScheduleById(Integer id) {
        log.info("Fetching schedule by id: {}", id);
        Optional<DoctorRecurringSchedule> schedule = scheduleRepository.findById(id);
        return schedule.map(scheduleMapper::toDto).orElse(null);
    }
    
    /**
     * Get all schedules for a specific doctor
     */
    public List<DoctorRecurringScheduleDTO> getSchedulesByDoctorId(Integer doctorId) {
        log.info("Fetching schedules for doctor: {}", doctorId);
        List<DoctorRecurringSchedule> schedules = scheduleRepository.findByDoctorId(doctorId);
        return scheduleMapper.toDtoList(schedules);
    }
    
    /**
     * Get active schedules for a specific doctor
     */
    public List<DoctorRecurringScheduleDTO> getActiveSchedulesByDoctorId(Integer doctorId) {
        log.info("Fetching active schedules for doctor: {}", doctorId);
        List<DoctorRecurringSchedule> schedules = scheduleRepository.findByDoctorIdAndStatus(
                doctorId, DoctorRecurringSchedule.ScheduleStatus.active);
        return scheduleMapper.toDtoList(schedules);
    }
    
    /**
     * Get schedules for a specific doctor and weekday
     */
    public List<DoctorRecurringScheduleDTO> getSchedulesByDoctorIdAndWeekday(Integer doctorId, Byte weekday) {
        log.info("Fetching schedules for doctor: {} on weekday: {}", doctorId, weekday);
        List<DoctorRecurringSchedule> schedules = scheduleRepository.findByDoctorIdAndWeekday(doctorId, weekday);
        return scheduleMapper.toDtoList(schedules);
    }
    
    /**
     * Get active schedules for a specific doctor and weekday
     */
    public List<DoctorRecurringScheduleDTO> getActiveSchedulesByDoctorIdAndWeekday(Integer doctorId, Byte weekday) {
        log.info("Fetching active schedules for doctor: {} on weekday: {}", doctorId, weekday);
        List<DoctorRecurringSchedule> schedules = scheduleRepository.findByDoctorIdAndWeekdayAndStatus(
                doctorId, weekday, DoctorRecurringSchedule.ScheduleStatus.active);
        return scheduleMapper.toDtoList(schedules);
    }
    
    /**
     * Create a new schedule
     */
    @Transactional
    public DoctorRecurringScheduleDTO createSchedule(DoctorRecurringScheduleDTO scheduleDTO) {
        log.info("Creating new schedule for doctor: {}", scheduleDTO.getDoctorId());
        DoctorRecurringSchedule schedule = scheduleMapper.toEntity(scheduleDTO);
        DoctorRecurringSchedule savedSchedule = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(savedSchedule);
    }
    
    /**
     * Update an existing schedule
     */
    @Transactional
    public DoctorRecurringScheduleDTO updateSchedule(Integer id, DoctorRecurringScheduleDTO scheduleDTO) {
        log.info("Updating schedule with id: {}", id);
        Optional<DoctorRecurringSchedule> existingSchedule = scheduleRepository.findById(id);
        
        if (existingSchedule.isPresent()) {
            DoctorRecurringSchedule schedule = existingSchedule.get();
            scheduleMapper.updateEntity(schedule, scheduleDTO);
            DoctorRecurringSchedule updatedSchedule = scheduleRepository.save(schedule);
            return scheduleMapper.toDto(updatedSchedule);
        }
        
        log.warn("Schedule with id {} not found", id);
        return null;
    }
    
    /**
     * Delete a schedule
     */
    @Transactional
    public boolean deleteSchedule(Integer id) {
        log.info("Deleting schedule with id: {}", id);
        if (scheduleRepository.existsById(id)) {
            scheduleRepository.deleteById(id);
            return true;
        }
        log.warn("Schedule with id {} not found", id);
        return false;
    }
    
    /**
     * Delete all schedules for a specific doctor
     */
    @Transactional
    public void deleteSchedulesByDoctorId(Integer doctorId) {
        log.info("Deleting all schedules for doctor: {}", doctorId);
        List<DoctorRecurringSchedule> schedules = scheduleRepository.findByDoctorId(doctorId);
        scheduleRepository.deleteAll(schedules);
    }
}
