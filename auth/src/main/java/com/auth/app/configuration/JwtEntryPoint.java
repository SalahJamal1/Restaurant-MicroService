package com.auth.app.configuration;

import com.auth.app.utils.Helper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtEntryPoint implements AuthenticationEntryPoint {
    private final Helper helper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        log.error("Jwt Entry Point Error: " + authException.getMessage(), authException);
        helper.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, authException);

    }
}
