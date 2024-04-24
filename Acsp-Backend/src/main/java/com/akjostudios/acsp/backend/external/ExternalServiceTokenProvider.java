package com.akjostudios.acsp.backend.external;

import com.akjostudios.acsp.common.model.supertokens.SupertokenJwtRequest;
import com.akjostudios.acsp.common.model.supertokens.SupertokenJwtResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
public class ExternalServiceTokenProvider {
    private final AtomicReference<String> currentToken = new AtomicReference<>();
    private final WebClient tokenClient;
    private final ExternalServiceProperties properties;

    private final Object refreshLock = new Object();
    private Disposable scheduledTask;

    @Value("${RAILWAY_PUBLIC_DOMAIN}")
    private String issuerUrl;

    @Autowired
    public ExternalServiceTokenProvider(
            @NotNull @Qualifier("client.web.supertokens") WebClient tokenClient,
            @NotNull ExternalServiceProperties properties
    ) {
        this.tokenClient = tokenClient;
        this.properties = properties;
    }

    public Mono<String> getToken() {
        return Mono.defer(() -> {
            String token = currentToken.get();
            if (token == null) {
                synchronized (refreshLock) {
                    token = currentToken.get();
                    if (token == null) {
                        return refreshToken();
                    }
                }
            }

            return Mono.just(token);
        });
    }

    private @NotNull Mono<String> refreshToken() {
        return tokenClient.post()
                .uri(properties.getSupertokensUrl() + "/appid-public/recipe/jwt")
                .bodyValue(new SupertokenJwtRequest(
                        Map.of("service", "backend"),
                        "RS256",
                        issuerUrl,
                        properties.getTokenRefreshInterval()
                )).retrieve()
                .bodyToMono(SupertokenJwtResponse.class)
                .flatMap(response -> {
                    if (!Objects.equals(response.getStatus(), "OK")) {
                        return Mono.error(new RuntimeException("Failed to refresh token!"));
                    }

                    synchronized (refreshLock) {
                        currentToken.set(response.getJwt());
                        scheduleTokenRefresh();
                    }

                    return Mono.just(currentToken.get());
                }).retryWhen(Retry.backoff(3, Duration.ofSeconds(10))
                        .filter(RuntimeException.class::isInstance)
                ).onErrorResume(ex -> {
                    log.error("Failed to refresh token from Supertokens!", ex);
                    return Mono.error(ex);
                });
    }

    private void scheduleTokenRefresh() {
        if (scheduledTask != null && !scheduledTask.isDisposed()) {
            scheduledTask.dispose();
        }
        scheduledTask = Mono.delay(Duration.ofSeconds(properties.getTokenRefreshInterval()))
                .subscribe(__ -> refreshToken().subscribe());
    }
}