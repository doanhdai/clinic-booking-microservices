package com.myclinic.appointment.service;

import com.myclinic.appointment.client.DoctorClient;
import com.myclinic.appointment.dto.DashboardDataDTO;
import com.myclinic.appointment.dto.ChartDataPointDTO;
import com.myclinic.appointment.dto.DoctorDTO;
import com.myclinic.appointment.dto.StaticalByDoctorDTO;
import com.myclinic.appointment.entity.Appointment;
import com.myclinic.appointment.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppointmentStatisticsService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorClient doctorClient;

    public DashboardDataDTO getDashboardOverview(LocalDate fromDate, LocalDate toDate, String specialty) {
        // Lấy appointments filtered
        List<Appointment> appointments = getFilteredAppointments(fromDate, toDate, specialty);

        // Tính 4 ô stats
        Integer completedAppointments = (int) appointments.stream()
                .filter(a -> Appointment.Status.finished.equals(a.getStatus()) ||
                           Appointment.Status.transferred.equals(a.getStatus()))
                .count();

        Integer upcomingAppointments = (int) appointments.stream()
                .filter(a -> Appointment.Status.pending.equals(a.getStatus()) || 
                           Appointment.Status.paid.equals(a.getStatus()))
                .count();

        Integer totalCancelled = (int) appointments.stream()
                .filter(a -> Appointment.Status.cancelled.equals(a.getStatus()))
                .count();

        Integer totalRevenue = appointments.stream()
                .filter(a -> Appointment.Status.finished.equals(a.getStatus()) || 
                           Appointment.Status.transferred.equals(a.getStatus()))
                .mapToInt(Appointment::getBookingFee)
                .sum();

        // biểu đồ LineChart: Doanh thu theo ngày
        List<ChartDataPointDTO> revenueTrends = buildRevenueTrends(appointments);

        // biểu đồ PieChart: tỷ lệ trạng thái lịch hẹn
        List<ChartDataPointDTO> statusDistribution = buildStatusDistribution(appointments);

        return new DashboardDataDTO(completedAppointments, upcomingAppointments, totalCancelled,
                totalRevenue, revenueTrends, statusDistribution,0,0,0
        );
    }

    public List<StaticalByDoctorDTO> getDoctorStatistics(LocalDate fromDate, LocalDate toDate, String specialty, String doctorName) {
        // Lấy doctors từ doctor-service với filter
        List<DoctorDTO> doctors = doctorClient.getDoctorsWithFilters(specialty, doctorName);
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(23,59,59);

        // Map từng doctor thành StaticalByDoctorDTO
        return doctors.stream()
                .map(doctor -> {
                    Integer doctorId = doctor.getUserId();
                    List<Appointment> doctorAppointments = appointmentRepository
                            .findByDoctorIdAndAppointmentStarttimeBetween(doctorId, fromDateTime, toDateTime);

                    Integer totalAppointments = doctorAppointments.size();
                    Integer completedAppointments = (int) doctorAppointments.stream()
                            .filter(a -> Appointment.Status.finished.equals(a.getStatus()) || 
                                       Appointment.Status.transferred.equals(a.getStatus()))
                            .count();

                    Integer revenue = doctorAppointments.stream()
                            .filter(a -> Appointment.Status.finished.equals(a.getStatus()) || 
                                       Appointment.Status.transferred.equals(a.getStatus()))
                            .mapToInt(Appointment::getBookingFee)
                            .sum();

                    Integer transferred = doctorAppointments.stream()
                            .filter(a -> Appointment.Status.transferred.equals(a.getStatus()))
                            .mapToInt(Appointment::getBookingFee)
                            .sum();

                    Integer remaining = revenue - transferred;

                    String status = remaining.equals(0) && revenue > 0
                            ? "Đã chuyển"
                            : revenue > 0 ? "Chờ chuyển" : "—";

                    return new StaticalByDoctorDTO(
                            doctorId,
                            (String) doctor.getAvatar(),
                            (String) doctor.getFullName(),
                            (String) doctor.getSpecialization(),
                            totalAppointments,
                            completedAppointments,
                            revenue,
                            transferred,
                            remaining,
                            status
                    );
                })
                .collect(Collectors.toList());
    }

    private List<Appointment> getFilteredAppointments(LocalDate fromDate, LocalDate toDate, String specialty) {
        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTime = toDate.atTime(23,59,59);

        if (specialty == null || specialty.isBlank() || "all".equals(specialty)) {
            return appointmentRepository.findByAppointmentStarttimeBetween(fromDateTime, toDateTime);
        } else {
            // Gọi doctor-service để lấy doctor IDs theo specialty
            List<Integer> doctorIds = doctorClient.getDoctorIdsBySpecialty(specialty);
            return appointmentRepository.findByDoctorIdInAndAppointmentStarttimeBetween(doctorIds, fromDateTime, toDateTime);
        }
    }

    private List<ChartDataPointDTO> buildRevenueTrends(List<Appointment> appointments) {
        Map<String, Integer> revenueByDate = appointments.stream()
                .filter(a -> Appointment.Status.finished.equals(a.getStatus()))
                .collect(Collectors.groupingBy(
                        a -> a.getAppointmentStarttime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        Collectors.summingInt(Appointment::getBookingFee)
                ));

        return revenueByDate.entrySet().stream()
                .map(entry -> new ChartDataPointDTO(
                        entry.getKey(),           // date
                        entry.getValue(),         // revenue (Integer)
                        null                      // percentage not needed for line chart
                ))
                .sorted((a, b) -> a.getName().compareTo(b.getName()))
                .collect(Collectors.toList());
    }

    private List<ChartDataPointDTO> buildStatusDistribution(List<Appointment> appointments) {
        Long total = (long) appointments.size();
        if (total == 0) return List.of();

        Map<Appointment.Status, Long> statusCount = appointments.stream()
                .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()));

        Map<Appointment.Status, String> statusNames = Map.of(
                Appointment.Status.finished, "Hoàn thành",
                Appointment.Status.pending, "Đang chờ",
                Appointment.Status.cancelled, "Đã hủy",
                Appointment.Status.paid, "Đã thanh toán",
                Appointment.Status.transferred, "Đã chuyển"
        );

        return statusCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(entry -> new ChartDataPointDTO(
                        statusNames.getOrDefault(entry.getKey(), entry.getKey().toString()),  // Vietnamese name
                        entry.getValue(),                                          // count (Long)
                        entry.getValue() * 100.0 / total                         // percentage (Double)
                ))
                .collect(Collectors.toList());
    }

    //thống kê bên tài khoản doctor
    public DashboardDataDTO getDashboardOverviewByDoctor(LocalDate fromDate, LocalDate toDate, Integer doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentStarttimeBetween(
                doctorId, fromDate.atStartOfDay(), toDate.atTime(23,59,59));

        Integer completedAppointments = (int) appointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.finished || a.getStatus() == Appointment.Status.transferred)
                .count();

        Integer upcomingAppointments = (int) appointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.pending || a.getStatus() == Appointment.Status.paid)
                .count();

        Integer totalCancelled = (int) appointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.cancelled)
                .count();

        Integer totalRevenue = appointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.finished || a.getStatus() == Appointment.Status.transferred)
                .mapToInt(Appointment::getBookingFee)
                .sum();

        Integer transferred = appointments.stream()
                .filter(a -> a.getStatus() == Appointment.Status.transferred)
                .mapToInt(Appointment::getBookingFee)
                .sum();
        Integer totalAppointments = appointments.size();

        Integer remaining = totalRevenue - transferred;

        List<ChartDataPointDTO> revenueTrends = buildRevenueTrends(appointments);
        List<ChartDataPointDTO> statusDistribution = buildStatusDistribution(appointments);

        return new DashboardDataDTO(completedAppointments, upcomingAppointments, totalCancelled, totalRevenue,
                revenueTrends, statusDistribution, transferred, remaining, totalAppointments
        );
    }

}