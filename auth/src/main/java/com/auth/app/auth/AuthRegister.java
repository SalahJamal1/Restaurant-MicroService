package com.auth.app.auth;

import com.auth.app.user.Role;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRegister {
    @Email(message = "Please enter the valid Email")
    @NotNull(message = "The Email is required")
    private String email;
    @NotNull(message = "The Password is required")
    private String password;
    @NotNull(message = "The First Name is required")
    private String firstName;
    @NotNull(message = "The Last Name is required")
    private String lastName;
    @NotNull(message = "The Address is required")
    private String address;
    @NotNull(message = "The Phone is required")
    private String phone;
    private Role role;
    @NotNull(message = "Please Confirm The Password")
    private String confirmPassword;

    @AssertTrue(message = "The password doesn't match")
    public boolean isPasswordsMatch() {
        return password != null && password.equals(confirmPassword);

    }

}
