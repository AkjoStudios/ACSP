package com.akjostudios.acsp.backend.external;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@RequiredArgsConstructor
@SuppressWarnings("java:S1452")
public class ExternalServiceClient {
    private final WebClient client;
    private final ExternalServiceTokenProvider tokenProvider;

    public <T> Mono<T> get(String uri, Function<WebClient.RequestHeadersSpec<?>, Mono<T>> consumer) {
        return tokenProvider.getToken()
                .map(token -> client.get().uri(uri).header("Authorization", "Bearer " + token))
                .flatMap(consumer)
                .onErrorResume(e -> Mono.empty());
    }
}