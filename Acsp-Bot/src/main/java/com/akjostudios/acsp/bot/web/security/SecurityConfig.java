package com.akjostudios.acsp.bot.web.security;

import com.akjostudios.acsp.bot.web.error.WebErrorHandler;
import com.akjostudios.acsp.bot.web.external.ExternalServiceProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.web.server.DelegatingServerAuthenticationEntryPoint;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpBasicServerAuthenticationEntryPoint;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
    private static final String ACTUATOR_ROLE = "ACTUATOR";
    private static final String PROMETHEUS_ROLE = "PROMETHEUS";

    private final SecurityProperties securityProperties;
    private final ExternalServiceProperties externalServiceProperties;

    private final ObjectMapper objectMapper;

    @Bean
    @Order(1)
    public @NotNull SecurityWebFilterChain mainFilterChain(
            @NotNull ServerHttpSecurity http,
            @NotNull @Qualifier("jwtAuthenticationManager") ReactiveAuthenticationManager authenticationManager
    ) {
        return defaultSecurity(http)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/**").authenticated()
                        .anyExchange().permitAll()
                ).oauth2ResourceServer(
                        oauthSpec -> oauthSpec.jwt(jwt -> jwt.authenticationManager(authenticationManager))
                ).exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint((exchange, e) -> handleException(exchange, e, HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((exchange, e) -> handleException(exchange, e, HttpStatus.FORBIDDEN))
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
                        .matchers(EndpointRequest.to(PrometheusScrapeEndpoint.class)).hasRole(PROMETHEUS_ROLE)
                        .matchers(EndpointRequest.to(InfoEndpoint.class)).hasRole(ACTUATOR_ROLE)
                        .pathMatchers("/actuator").hasRole(ACTUATOR_ROLE)
                        .anyExchange().permitAll()
                ).httpBasic(httpBasic -> httpBasic
                        .authenticationManager(authenticationManager)
                ).exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint((exchange1, ex1) -> new DelegatingServerAuthenticationEntryPoint(
                                List.of(new DelegatingServerAuthenticationEntryPoint.DelegateEntry(
                                        ServerWebExchangeMatchers.anyExchange(),
                                        new HttpBasicServerAuthenticationEntryPoint()
                                ), new DelegatingServerAuthenticationEntryPoint.DelegateEntry(
                                        ServerWebExchangeMatchers.anyExchange(),
                                        (exchange2, ex2) -> handleException(exchange2, ex2, HttpStatus.UNAUTHORIZED)
                                ))
                        ).commence(exchange1, ex1)).accessDeniedHandler(this::handleReauthentication)
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

                            corsConfig.addAllowedOrigin(externalServiceProperties.getSupertokensUrl());
                            corsConfig.addAllowedOrigin(externalServiceProperties.getBackendUrl());

                            corsConfig.addAllowedHeader("*");
                            corsConfig.addAllowedMethod("*");
                            return corsConfig;
                        })
                ).csrf(ServerHttpSecurity.CsrfSpec::disable);  // We use JWTs, so CSRF is not needed
    }

    private @NotNull Mono<Void> handleException(
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

    private @NotNull Mono<Void> handleReauthentication(
            @NotNull ServerWebExchange exchange,
            @NotNull Throwable ex
    ) {
        log.info("Prompting user for reauthentication at {}", exchange.getRequest().getURI());
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().set("WWW-Authenticate", "Basic realm=\"Realm\"");
        return exchange.getResponse().setComplete();
    }

    @Bean("jwtAuthenticationManager")
    @Primary
    public @NotNull ReactiveAuthenticationManager jwtAuthenticationManager(@NotNull ReactiveJwtDecoder jwtDecoder) {
        JwtReactiveAuthenticationManager authManager = new JwtReactiveAuthenticationManager(jwtDecoder);
        authManager.setJwtAuthenticationConverter(jwt -> {
            if (jwt.hasClaim("service")) {
                return Mono.just(new JwtAuthenticationToken(jwt, List.of(
                        new SimpleGrantedAuthority("SERVICE_" + jwt.getClaimAsString("service").toUpperCase())
                )));
            }
            if (jwt.hasClaim("roles")) {
                return Mono.just(new JwtAuthenticationToken(jwt, jwt.getClaimAsStringList("roles").stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        .toList()
                ));
            }
            return Mono.just(new JwtAuthenticationToken(jwt));
        });
        return authManager;
    }

    @Bean("basicAuthenticationManager")
    public @NotNull ReactiveAuthenticationManager basicAuthenticationManager(
            @NotNull @Qualifier("actuatorUserDetailsService")
            ReactiveUserDetailsService actuatorUserDetailsService
    ) { return new UserDetailsRepositoryReactiveAuthenticationManager(
            actuatorUserDetailsService
    ); }

    @Bean("actuatorUserDetailsService")
    public @NotNull ReactiveUserDetailsService userDetailsService(
            @NotNull PasswordEncoder passwordEncoder
    ) {
        return new MapReactiveUserDetailsService(List.of(
                User.withUsername(securityProperties.getPrometheus().getUsername())
                        .password(passwordEncoder.encode(securityProperties.getPrometheus().getPassword()))
                        .roles(PROMETHEUS_ROLE)
                        .build(),
                User.withUsername(securityProperties.getActuator().getUsername())
                        .password(passwordEncoder.encode(securityProperties.getActuator().getPassword()))
                        .roles(ACTUATOR_ROLE)
                        .build()
        ));
    }

    @Bean
    public @NotNull PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}