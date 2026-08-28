package com.app.gateway.configuration;

import com.app.gateway.auth.AuthRegister;
import com.app.gateway.user.User;
import com.app.gateway.user.dto.UserDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface MapperConfiguration {
    UserDto toUserDto(User user);

    User toUser(AuthRegister authRegister);
}
