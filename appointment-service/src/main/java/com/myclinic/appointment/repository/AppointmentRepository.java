package com.myclinic.appointment.repository;

import com.myclinic.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    // 🔹 Tìm lịch hẹn trong 1 khoảng thời gian
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.appointmentStarttime >= :start
          AND a.appointmentEndtime <= :end
        ORDER BY a.appointmentStarttime ASC
        """)
    List<Appointment> findAppointmentsByInRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 🔹 Kiểm tra trùng lịch (xem có cuộc hẹn nào của bác sĩ trong khung giờ đó không)
    @Query("""
        SELECT a FROM Appointment a
        WHERE a.doctorId = :doctorId
          AND a.status IN ('pending', 'paid')
          AND (
            (a.appointmentStarttime < :end AND a.appointmentEndtime > :start)
          )
        """)
    List<Appointment> findConflictingAppointments(
            @Param("doctorId") Integer doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
