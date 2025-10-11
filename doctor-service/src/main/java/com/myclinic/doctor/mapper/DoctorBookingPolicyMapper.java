package com.myclinic.doctor.mapper;

import com.myclinic.doctor.dto.DoctorBookingPolicyDTO;
import com.myclinic.doctor.entity.DoctorBookingPolicy;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DoctorBookingPolicyMapper {
    
    // Entity -> DTO
    DoctorBookingPolicyDTO toDto(DoctorBookingPolicy entity);
    
    List<DoctorBookingPolicyDTO> toDtoList(List<DoctorBookingPolicy> entities);
    
    // DTO -> Entity
    DoctorBookingPolicy toEntity(DoctorBookingPolicyDTO dto);
    
    // Partial update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "doctorId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(@MappingTarget DoctorBookingPolicy target, DoctorBookingPolicyDTO patch);
}
