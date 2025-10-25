package com.auth.app.configuration;

import com.auth.app.auth.AuthRegister;
import com.auth.app.user.User;
import com.auth.app.user.dto.UserDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface MapperConfiguration {
    UserDto toUserDto(User user);

    User toUser(AuthRegister authRegister);
}
