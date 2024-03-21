package com.akjostudios.acsp.backend.security;

import com.akjostudios.acsp.backend.error.WebErrorHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
    private final SecurityProperties securityProperties;
    private final ObjectMapper objectMapper;

    @Bean
    @Order(1)
    public @NotNull SecurityWebFilterChain mainFilterChain(
            @NotNull ServerHttpSecurity http
    ) {
        return defaultSecurity(http)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/**").authenticated()
                        .pathMatchers("/actuator/prometheus").hasRole("PROMETHEUS")
                        .anyExchange().permitAll()
                ).build();
    }

    private @NotNull ServerHttpSecurity defaultSecurity(
            @NotNull ServerHttpSecurity http
    ) {
        return http.formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .cors(corsSpec -> corsSpec
                        .configurationSource(request -> {
                            CorsConfiguration corsConfig = new CorsConfiguration();
                            corsConfig.addAllowedHeader("*");
                            corsConfig.addAllowedMethod("*");
                            return corsConfig;
                        })
                ).exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint((exchange, e) -> handleException(exchange, e, HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((exchange, e) -> handleException(exchange, e, HttpStatus.FORBIDDEN))
                );
    }

    private Mono<Void> handleException(
            @NotNull ServerWebExchange exchange,
            @NotNull Throwable ex,
            @NotNull HttpStatus status
    ) {
        log.error("Security exception to {} with status {}: {}", exchange.getRequest().getPath(), status, ex.getMessage());
        exchange.getResponse().setStatusCode(status);
        try {
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(
                            objectMapper.writeValueAsBytes(new WebErrorHandler.ErrorResponse(
                                    StringUtils.replaceChars(ex.getMessage(), '"', '\''),
                                    status.value(),
                                    exchange.getRequest().getPath().value()
                            ))
                    ))
            );
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return new UserDetailsRepositoryReactiveAuthenticationManager(
                new MapReactiveUserDetailsService(
                        User.withUsername(securityProperties.getPrometheus().getUsername())
                                .password(passwordEncoder().encode(securityProperties.getPrometheus().getPassword()))
                                .roles("PROMETHEUS")
                                .build()
                )
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}