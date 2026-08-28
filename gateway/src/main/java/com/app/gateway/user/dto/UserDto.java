package com.app.gateway.user.dto;

import com.app.gateway.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private String address;
    private String phone;
}
