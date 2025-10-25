package com.auth.app.configuration;

import com.auth.app.tokens.TokenRepository;
import com.auth.app.utils.Helper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtServices jwtServices;
    private final Helper helper;
    private final TokenRepository tokenRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = extractToken(request);
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }
        try {

            var username = jwtServices.extractUserName(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var userDetails = userDetailsService.loadUserByUsername(username);
                var isTokenNotRevokedOrExpire = tokenRepository.findTokenByAccessToken(jwt)
                        .map(t -> !t.isExpired() && !t.isRevoked()).orElse(false);
                if (jwtServices.isTokenValid(jwt, userDetails) && isTokenNotRevokedOrExpire) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }

            }


        } catch (Exception ex) {
            log.error("Jwt Auth Filter Error: " + ex.getMessage(), ex);
            helper.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ex);
            return;
        }
        filterChain.doFilter(request, response);

    }

    public String extractToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7).trim();
        }
        return null;
    }
}
