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

    List<AppointmentDTO> toDtoList(List<Appointment> entities);

    // DTO -> Entity
    @Mapping(target = "id", ignore = true)
    Appointment toEntity(AppointmentDTO dto);

    // ----------------------------
    // Partial update (PATCH)
    // ----------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Appointment target, AppointmentDTO patch);
}
