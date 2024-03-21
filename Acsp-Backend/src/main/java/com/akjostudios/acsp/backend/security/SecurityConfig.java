package com.akjostudios.acsp.backend.security;

import com.akjostudios.acsp.backend.error.WebErrorHandler;
import com.akjostudios.acsp.backend.external.ExternalServiceProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
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
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
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
    private final ExternalServiceProperties externalServiceProperties;

    private final ObjectMapper objectMapper;

    @Bean
    @Order(1)
    public @NotNull SecurityWebFilterChain mainFilterChain(
            @NotNull ServerHttpSecurity http
    ) {
        return defaultSecurity(http)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/**").authenticated()
                        .anyExchange().permitAll()
                ).httpBasic(ServerHttpSecurity.HttpBasicSpec::disable).build();
    }

    @Bean
    @Order(0)
    public @NotNull SecurityWebFilterChain actuatorFilterChain(
            @NotNull ServerHttpSecurity http,
            @NotNull @Qualifier("basicAuthenticationManager") ReactiveAuthenticationManager authenticationManager
    ) {
        return defaultSecurity(http)
                .securityMatcher(new PathPatternParserServerWebExchangeMatcher("/actuator/**"))
                .authorizeExchange(exchanges -> exchanges
                        .matchers(EndpointRequest.to(PrometheusScrapeEndpoint.class)).hasRole("PROMETHEUS")
                        .anyExchange().permitAll()
                ).httpBasic(httpBasic -> httpBasic
                        .authenticationManager(authenticationManager)
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

                            corsConfig.addAllowedOrigin(externalServiceProperties.getAuthUrl());

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

    @Bean("basicAuthenticationManager")
    public @NotNull ReactiveAuthenticationManager basicAuthenticationManager(
            @NotNull @Qualifier("prometheusUserDetailsService") ReactiveUserDetailsService userDetailsService
    ) { return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService); }

    @Bean("prometheusUserDetailsService")
    public @NotNull ReactiveUserDetailsService userDetailsService(
            @NotNull PasswordEncoder passwordEncoder
    ) {
        return new MapReactiveUserDetailsService(User
                .withUsername(securityProperties.getPrometheus().getUsername())
                .password(passwordEncoder.encode(securityProperties.getPrometheus().getPassword()))
                .roles("PROMETHEUS")
                .build());
    }

    @Bean
    public @NotNull PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}