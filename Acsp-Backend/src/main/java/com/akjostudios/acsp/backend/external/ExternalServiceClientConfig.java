package com.akjostudios.acsp.backend.external;

import com.akjostudios.acsp.common.api.ExternalServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
@SuppressWarnings("java:S6830")
public class ExternalServiceClientConfig {
    private final ExternalServiceTokenProvider tokenProvider;

    @Bean("client.service.supertokens")
    public ExternalServiceClient supertokensClient(
            @Qualifier("client.web.supertokens") WebClient client
    ) {
        return new ExternalServiceClient(client, tokenProvider);
    }

    @Bean("client.service.bot")
    public ExternalServiceClient botClient(
            @Qualifier("client.web.bot") WebClient client
    ) {
        return new ExternalServiceClient(client, tokenProvider);
    }
}