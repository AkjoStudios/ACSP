package com.akjostudios.acsp.bot.web.external;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
@EnableWebFlux
@SuppressWarnings("java:S6830")
public class ExternalServiceClientConfig {
    private final ExternalServiceProperties externalServiceProperties;

    @Bean("client.httpconnector")
    @Scope("prototype")
    public @NotNull ReactorClientHttpConnector httpConnector() {
        return new ReactorClientHttpConnector(
                HttpClient.create().followRedirect(true)
        );
    }

    @Bean("client.service.supertokens")
    public @NotNull WebClient supertokensClient() {
        return WebClient.builder()
                .baseUrl(externalServiceProperties.getSupertokensUrl())
                .defaultHeader("api-key", externalServiceProperties.getAuthApiKey())
                .clientConnector(httpConnector())
                .build();
    }

    @Bean("client.service.backend")
    public @NotNull WebClient backendClient() {
        return WebClient.builder()
                .baseUrl(externalServiceProperties.getBackendUrl())
                .clientConnector(httpConnector())
                .build();
    }
}