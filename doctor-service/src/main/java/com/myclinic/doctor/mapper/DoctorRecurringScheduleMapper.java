package com.myclinic.doctor.mapper;

import com.myclinic.doctor.dto.DoctorRecurringScheduleDTO;
import com.myclinic.doctor.entity.DoctorRecurringSchedule;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DoctorRecurringScheduleMapper {
    
    // Entity -> DTO
    DoctorRecurringScheduleDTO toDto(DoctorRecurringSchedule entity);
    
    List<DoctorRecurringScheduleDTO> toDtoList(List<DoctorRecurringSchedule> entities);
    
    // DTO -> Entity
    DoctorRecurringSchedule toEntity(DoctorRecurringScheduleDTO dto);
    
    // Partial update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget DoctorRecurringSchedule target, DoctorRecurringScheduleDTO patch);
}
