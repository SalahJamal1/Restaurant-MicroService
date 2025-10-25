package com.auth.app.auth;

import com.auth.app.configuration.MapperConfiguration;
import com.auth.app.user.User;
import com.auth.app.user.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    private final MapperConfiguration mapperConfiguration;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> Login(@RequestBody AuthLogin authLogin, HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(service.login(authLogin, request, response));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> Register(@Valid @RequestBody AuthRegister authRegister, HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(service.register(authRegister, request, response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> Refresh_Token(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(service.refreshToken(request, response));
    }

    @GetMapping("/user-token")
    public ResponseEntity<UserDto> getUserFromToken(@AuthenticationPrincipal User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not logged in");
        }
        var userDto = mapperConfiguration.toUserDto(user);
        return ResponseEntity.ok(userDto);
    }
}
