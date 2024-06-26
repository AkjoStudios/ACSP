package com.akjostudios.acsp.common.api;

import com.akjostudios.acsp.common.dto.ExternalServiceResponse;
import com.akjostudios.acsp.common.model.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@RequiredArgsConstructor
@SuppressWarnings({"unused", "java:S1452"})
public class ExternalServiceClient {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final WebClient client;
    private final TokenProvider tokenProvider;

    public <T> Mono<T> get(String uri, Function<WebClient.RequestHeadersSpec<?>, Mono<T>> consumer) {
        return tokenProvider.getToken()
                .map(token -> client.get().uri(uri).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .flatMap(consumer);

    }

    public <T> Mono<T> post(String uri, Function<WebClient.RequestHeadersSpec<?>, Mono<T>> consumer) {
        return tokenProvider.getToken()
                .map(token -> client.post().uri(uri).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .flatMap(consumer);
    }

    public <T> Mono<T> put(String uri, Function<WebClient.RequestHeadersSpec<?>, Mono<T>> consumer) {
        return tokenProvider.getToken()
                .map(token -> client.put().uri(uri).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .flatMap(consumer);
    }

    public <T> Mono<T> delete(String uri, Function<WebClient.RequestHeadersSpec<?>, Mono<T>> consumer) {
        return tokenProvider.getToken()
                .map(token -> client.delete().uri(uri).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .flatMap(consumer);
    }

    public <T> Mono<T> patch(String uri, Function<WebClient.RequestHeadersSpec<?>, Mono<T>> consumer) {
        return tokenProvider.getToken()
                .map(token -> client.patch().uri(uri).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .flatMap(consumer);
    }

    public <T> Mono<T> head(String uri, Function<WebClient.RequestHeadersSpec<?>, Mono<T>> consumer) {
        return tokenProvider.getToken()
                .map(token -> client.head().uri(uri).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .flatMap(consumer);
    }

    public <T> Mono<T> options(String uri, Function<WebClient.RequestHeadersSpec<?>, Mono<T>> consumer) {
        return tokenProvider.getToken()
                .map(token -> client.options().uri(uri).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .flatMap(consumer);
    }

    public <T extends ExternalServiceResponse> Mono<T> exchangeGet(String uri, Class<T> responseType) {
        return tokenProvider.getToken()
                .map(token -> client.get().uri(uri).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .flatMap(request -> request.exchangeToMono(response -> response.bodyToMono(responseType)))
                .flatMap(response -> response.status() == ResponseStatus.SUCCESS
                        ? Mono.just(response)
                        : Mono.error(new RuntimeException(response.error()))
                );
    }

    public <T extends ExternalServiceResponse> Mono<T> exchangePost(String uri, Class<T> responseType) {
        return tokenProvider.getToken()
                .map(token -> client.post().uri(uri).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .flatMap(request -> request.exchangeToMono(response -> response.bodyToMono(responseType)))
                .flatMap(response -> response.status() == ResponseStatus.SUCCESS
                        ? Mono.just(response)
                        : Mono.error(new RuntimeException(response.error()))
                );
    }

    public <T extends ExternalServiceResponse, D> Mono<T> exchangePost(String uri, D body, Class<T> responseType) {
        return tokenProvider.getToken()
                .map(token -> client.post()
                        .uri(uri)
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                        .bodyValue(body)
                ).flatMap(request -> request.exchangeToMono(response -> response.bodyToMono(responseType)))
                .flatMap(response -> response.status() == ResponseStatus.SUCCESS
                        ? Mono.just(response)
                        : Mono.error(new RuntimeException(response.error()))
                );
    }

    public <T extends ExternalServiceResponse> Mono<T> exchangePut(String uri, Class<T> responseType) {
        return tokenProvider.getToken()
                .map(token -> client.put().uri(uri).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .flatMap(request -> request.exchangeToMono(response -> response.bodyToMono(responseType)))
                .flatMap(response -> response.status() == ResponseStatus.SUCCESS
                        ? Mono.just(response)
                        : Mono.error(new RuntimeException(response.error()))
                );
    }

    public <T extends ExternalServiceResponse, D> Mono<T> exchangePut(String uri, D body, Class<T> responseType) {
        return tokenProvider.getToken()
                .map(token -> client.put()
                        .uri(uri)
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                        .bodyValue(body)
                ).flatMap(request -> request.exchangeToMono(response -> response.bodyToMono(responseType)))
                .flatMap(response -> response.status() == ResponseStatus.SUCCESS
                        ? Mono.just(response)
                        : Mono.error(new RuntimeException(response.error()))
                );
    }

    public <T extends ExternalServiceResponse> Mono<T> exchangeDelete(String uri, Class<T> responseType) {
        return tokenProvider.getToken()
                .map(token -> client.delete().uri(uri).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .flatMap(request -> request.exchangeToMono(response -> response.bodyToMono(responseType)))
                .flatMap(response -> response.status() == ResponseStatus.SUCCESS
                        ? Mono.just(response)
                        : Mono.error(new RuntimeException(response.error()))
                );
    }

    public <T extends ExternalServiceResponse> Mono<T> exchangePatch(String uri, Class<T> responseType) {
        return tokenProvider.getToken()
                .map(token -> client.patch().uri(uri).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .flatMap(request -> request.exchangeToMono(response -> response.bodyToMono(responseType)))
                .flatMap(response -> response.status() == ResponseStatus.SUCCESS
                        ? Mono.just(response)
                        : Mono.error(new RuntimeException(response.error()))
                );
    }

    public <T extends ExternalServiceResponse> Mono<T> exchangeHead(String uri, Class<T> responseType) {
        return tokenProvider.getToken()
                .map(token -> client.head().uri(uri).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .flatMap(request -> request.exchangeToMono(response -> response.bodyToMono(responseType)))
                .flatMap(response -> response.status() == ResponseStatus.SUCCESS
                        ? Mono.just(response)
                        : Mono.error(new RuntimeException(response.error()))
                );
    }

    public <T extends ExternalServiceResponse> Mono<T> exchangeOptions(String uri, Class<T> responseType) {
        return tokenProvider.getToken()
                .map(token -> client.options().uri(uri).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .flatMap(request -> request.exchangeToMono(response -> response.bodyToMono(responseType)))
                .flatMap(response -> response.status() == ResponseStatus.SUCCESS
                        ? Mono.just(response)
                        : Mono.error(new RuntimeException(response.error()))
                );
    }
}