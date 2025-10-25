package com.auth.app.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class Helper {
    private final ObjectMapper mapper;

    public static String getJwtFromRequest(HttpServletRequest request) {

        var cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwt")) return cookie.getValue().trim();
            }
        }
        return null;
    }

    public static String getDeviceId(HttpServletRequest request) {
        var auth = request.getHeader("deviceId");
        if (auth != null && !auth.isEmpty()) {
            return auth;

        }
        var cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("deviceId".equals(cookie.getName())) return cookie.getValue();
            }
        }
        return null;
    }

    public static String getOrCreateDeviceId(HttpServletRequest request, HttpServletResponse response) {

        var deviceId = getDeviceId(request);
        if (deviceId != null) {
            return deviceId;
        }

        var newDeviceId = UUID.randomUUID().toString();
        Cookie cookie = buildCookie("deviceId", newDeviceId, 365);
        response.addCookie(cookie);
        return newDeviceId;
    }

    public static Cookie buildCookie(String name, String value, int maxAge) {
        var cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge * 24 * 60 * 60);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "None");
        return cookie;
    }

    public void sendErrorResponse(HttpServletResponse response, int status, Exception ex) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON.toString());
        response.setStatus(status);
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("status", status);
        res.put("message", ex.getMessage());
        res.put("timestamp", System.currentTimeMillis());
        mapper.writeValue(response.getWriter(), res);
    }
}
