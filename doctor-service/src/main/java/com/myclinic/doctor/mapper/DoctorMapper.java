package com.myclinic.doctor.mapper;

import com.myclinic.doctor.dto.DoctorDTO;
import com.myclinic.doctor.dto.UserInfoDTO;
import com.myclinic.doctor.entity.Doctor;
import org.mapstruct.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    // Entity -> DTO (chỉ phần Doctor)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "birth",    ignore = true)
    @Mapping(target = "gender",   ignore = true)
    @Mapping(target = "email",    ignore = true)
    @Mapping(target = "phone",    ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status",   ignore = true)
    @Mapping(target = "avatar",   ignore = true)
    DoctorDTO toDto(Doctor entity);

    List<DoctorDTO> toDtoList(List<Doctor> entities);

    // Gộp Entity + UserInfo -> DTO đầy đủ
    @Mapping(target = "userId", source = "entity.userId")
    @Mapping(target = "specialization", source = "entity.specialization")
    @Mapping(target = "description", source = "entity.description")
    @Mapping(target = "address", source = "entity.address")
    @Mapping(target = "bankNumber", source = "entity.bankNumber")
    @Mapping(target = "bankName", source = "entity.bankName")
    @Mapping(target = "bankId", source = "entity.bankId")
    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "birth", source = "user.birth")
    @Mapping(target = "gender", source = "user.gender")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "createdAt", source = "user.createdAt")
    @Mapping(target = "updatedAt", source = "user.updatedAt")
    @Mapping(target = "status", source = "user.status")
    @Mapping(target = "avatar", source = "user.avatar")
    DoctorDTO toDto(Doctor entity, UserInfoDTO user);

    // DTO -> Entity
    Doctor toEntity(DoctorDTO dto);

    // Partial update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    void updateEntity(@MappingTarget Doctor target, DoctorDTO patch);
}
