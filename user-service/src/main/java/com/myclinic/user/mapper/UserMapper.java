package com.myclinic.user.mapper;

import com.myclinic.user.dto.UserInfoDTO;
import com.myclinic.user.dto.UserAccountDTO;
import com.myclinic.user.entity.User;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "lat", ignore = true)
    @Mapping(target = "lng", ignore = true)
    UserInfoDTO toDto(User user);

    // Map to UserAccountDTO (không bao gồm password)
    UserAccountDTO toAccountDto(User user);

    @AfterMapping
    default void mapLocation(User user, @MappingTarget UserInfoDTO dto) {
        if (user.getLocation() != null) {
            dto.setLat(user.getLocation().getY());
            dto.setLng(user.getLocation().getX());
        }
    }

    List<UserInfoDTO> toDtoList(List<User> entities);
    List<UserAccountDTO> toAccountDtoList(List<User> entities);

    // Partial update
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget User target, UserInfoDTO patch);
}
