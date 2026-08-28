package com.app.gateway.configuration;

import com.app.gateway.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;


import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final UserRepository userRepository;
    @Value("${gateway.menu-url}")
    private String menuUrl;

    @Value("${gateway.order-url}")
    private String orderUrl;

    @Value("${gateway.payment-url}")
    private String paymentUrl;
    @Bean
    public RouterFunction<ServerResponse> gatewayRouterFunctions() {

        return route("menu")
                .route(
                        GatewayRequestPredicates.path("/api/v1/items/**"),
                        http()
                )
                .before(BeforeFilterFunctions.uri(menuUrl))
                .build()

                .and(route("orders")
                        .route(
                                GatewayRequestPredicates.path("/api/v1/orders/**"),
                                http()
                        )
                        .before(BeforeFilterFunctions.uri(orderUrl))
                        .build())

                .and(route("payments")
                        .route(
                                GatewayRequestPredicates.path("/api/v1/payments/**"),
                                http()
                        )
                        .before(BeforeFilterFunctions.uri(paymentUrl))
                        .build());
    }

    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }



    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("The Email not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("POST", "GET", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
