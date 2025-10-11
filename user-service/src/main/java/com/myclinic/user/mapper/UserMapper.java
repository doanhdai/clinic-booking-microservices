package com.myclinic.user.mapper;

import com.myclinic.user.dto.UserInfoDTO;
import com.myclinic.user.entity.User;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserInfoDTO toDto(User user);

    List<UserInfoDTO> toDtoList(List<User> entities);

    // Partial update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget User target, UserInfoDTO patch);
}
