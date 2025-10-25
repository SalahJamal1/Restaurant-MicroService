package com.auth.app.configuration;

import com.auth.app.tokens.TokenRepository;
import com.auth.app.utils.Helper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenRepository tokenRepository;
    private final Helper helper;

    @SneakyThrows
    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        var deviceId = Helper.getDeviceId(request);
        var jwt = Helper.getJwtFromRequest(request);
        if (deviceId == null || jwt == null) {

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "you aren't logged in");
        }
        try {

            var token = tokenRepository.findTokenByRefreshToken(jwt)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "token: you aren't logged in"));

            var user = token.getUser();
            tokenRepository.UpdateAllValidTokenByUserAndDeviceId(user.getId(), deviceId);
            var cookie = Helper.buildCookie("jwt", null, 0);
            response.addCookie(cookie);
        } catch (Exception e) {
            helper.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, e);

        }


    }
}
