package com.myclinic.doctor.repository;

import com.myclinic.doctor.entity.DoctorRecurringSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRecurringScheduleRepository extends JpaRepository<DoctorRecurringSchedule, Integer> {
    
    List<DoctorRecurringSchedule> findByDoctorId(Integer doctorId);
    
    List<DoctorRecurringSchedule> findByDoctorIdAndStatus(Integer doctorId, DoctorRecurringSchedule.ScheduleStatus status);
    
    List<DoctorRecurringSchedule> findByDoctorIdAndWeekday(Integer doctorId, Byte weekday);
    
    @Query("SELECT s FROM DoctorRecurringSchedule s WHERE s.doctorId = :doctorId AND s.weekday = :weekday AND s.status = :status")
    List<DoctorRecurringSchedule> findByDoctorIdAndWeekdayAndStatus(
            @Param("doctorId") Integer doctorId, 
            @Param("weekday") Byte weekday,
            @Param("status") DoctorRecurringSchedule.ScheduleStatus status);
}
