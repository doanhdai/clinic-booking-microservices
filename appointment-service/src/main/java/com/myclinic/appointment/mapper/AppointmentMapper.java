package com.myclinic.appointment.mapper;

import com.myclinic.appointment.dto.AppointmentDTO;
import com.myclinic.appointment.dto.UserInfoDTO;
import com.myclinic.appointment.entity.Appointment;
import com.myclinic.appointment.dto.DoctorDTO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    // ----------------------------
    // Entity -> DTO cơ bản
    // ----------------------------
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    AppointmentDTO toDto(Appointment entity);

    // Entity -> DTO với thông tin doctor và patient
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "patientId", source = "entity.patientId")
    @Mapping(target = "doctorId", source = "entity.doctorId")
    @Mapping(target = "appointmentStarttime", source = "entity.appointmentStarttime")
    @Mapping(target = "appointmentEndtime", source = "entity.appointmentEndtime")
    @Mapping(target = "status", source = "entity.status")
    @Mapping(target = "bookingFee", source = "entity.bookingFee")
    @Mapping(target = "cancelPolicyMinutes", source = "entity.cancelPolicyMinutes")
    @Mapping(target = "cancelFeePercent", source = "entity.cancelFeePercent")
    @Mapping(target = "createdAt", source = "entity.createdAt")
    @Mapping(target = "updatedAt", source = "entity.updatedAt")
    @Mapping(target = "doctor", source = "doctorDTO")
    @Mapping(target = "patient", source = "patientDTO")
    AppointmentDTO toDto(Appointment entity, DoctorDTO doctorDTO, UserInfoDTO patientDTO);

    List<AppointmentDTO> toDtoList(List<Appointment> entities);

    @Mapping(target = "id", ignore = true)
    Appointment toEntity(AppointmentDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Appointment target, AppointmentDTO patch);
}
