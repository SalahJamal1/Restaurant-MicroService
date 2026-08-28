package com.app.gateway.auth;

import com.app.gateway.configuration.JwtServices;
import com.app.gateway.configuration.MapperConfiguration;
import com.app.gateway.tokens.Token;
import com.app.gateway.tokens.TokenRepository;
import com.app.gateway.user.Role;
import com.app.gateway.user.User;
import com.app.gateway.user.UserRepository;
import com.app.gateway.utils.Helper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MapperConfiguration mapperConfiguration;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServices jwtServices;
    private final AuthenticationManager authenticationManager;


    public AuthResponse register(AuthRegister authRegister, HttpServletRequest request, HttpServletResponse response) {

        if (userRepository.findByEmail(authRegister.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
        authRegister.setRole(Role.ROLE_USER);
        authRegister.setPassword(passwordEncoder.encode(authRegister.getPassword()));
        var user = mapperConfiguration.toUser(authRegister);
        try {
            System.out.println(user);

            userRepository.save(user);
            return getAuthResponse(request, response, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    public AuthResponse login(AuthLogin authLogin, HttpServletRequest request, HttpServletResponse response) {
        try {
            var user = userRepository.findByEmail(authLogin.getEmail()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authLogin.getEmail(),
                            authLogin.getPassword()
                    )
            );
            return getAuthResponse(request, response, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }

    public AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String jwt = Helper.getJwtFromRequest(request);
        if (jwt == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you are not logged in");

        }
        var username = jwtServices.extractUserName(jwt);
        if (username != null) {
            var user = userRepository.findByEmail(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            if (jwtServices.isTokenValid(jwt, user)) {
                return getAuthResponse(request, response, user);
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
    }


    public void setCookie(HttpServletResponse response, String token) {
        Cookie cookie = Helper.buildCookie("jwt", token, 7);
        response.addCookie(cookie);
    }

    private AuthResponse getAuthResponse(HttpServletRequest request, HttpServletResponse response, User user) {
        var deviceId = Helper.getOrCreateDeviceId(request, response);
        revokeAllUserTokens(user.getId(), deviceId);

        var accessToken = jwtServices.generateAccessToken(user);
        var refreshToken = jwtServices.generateRefreshToken(user);

        saveUserToken(deviceId, refreshToken, accessToken, user);
        setCookie(response, refreshToken);
        var userDto = mapperConfiguration.toUserDto(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userDto)
                .build();
    }

    public void revokeAllUserTokens(Integer userId, String deviceId) {
        tokenRepository.UpdateAllValidTokenByUserAndDeviceId(userId, deviceId);

    }

    private void saveUserToken(String deviceId, String refresh_token, String access_token, User user) {
        var token = Token.builder()
                .user(user)
                .refreshToken(refresh_token)
                .accessToken(access_token)
                .deviceId(deviceId)
                .build();
        tokenRepository.save(token);
    }
}
