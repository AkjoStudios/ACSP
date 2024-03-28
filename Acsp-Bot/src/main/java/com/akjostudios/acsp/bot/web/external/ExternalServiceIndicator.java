package com.akjostudios.acsp.bot.web.external;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@Component("externalServices")
@Slf4j
@SuppressWarnings("java:S6830")
public class ExternalServiceIndicator implements ReactiveHealthIndicator {
    private static final String SERVICE_DETAIL = "service";
    private static final String BACKEND_SERVICE = "backend";

    private final WebClient backendClient;

    @Autowired
    @Contract(pure = true)
    public ExternalServiceIndicator(
            @Qualifier("client.service.backend") WebClient backendClient
    ) {
        this.backendClient = backendClient;
    }

    @Override
    public Mono<Health> health() {
        return Flux.merge(
                checkBackendService()
        ).collectList().map(healths -> {
            Map<String, Object> details = healths.stream()
                    .collect(Collectors.toMap(
                            health -> health.getDetails().getOrDefault(SERVICE_DETAIL, "unknown").toString(),
                            health -> health.getDetails().entrySet().stream()
                                    .filter(entry -> !entry.getKey().equals(SERVICE_DETAIL))
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            Map.Entry::getValue,
                                            (existing, replacement) -> existing
                                    )),
                            (existing, replacement) -> existing
                    ));
           return healths.stream()
                   .anyMatch(health -> health.getStatus().equals(Status.DOWN))
                   ? Health.down().withDetails(details).build()
                   : Health.up().withDetails(details).build();
        });
    }

    private @NotNull Mono<Health> checkBackendService() {
        return backendClient.get().uri("/actuator/health/liveness")
                .exchangeToMono(clientResponse -> Mono.just(
                        clientResponse.statusCode().is2xxSuccessful() ? Health.up() : Health.down()
                ).map(builder -> builder
                        .withDetail(SERVICE_DETAIL, BACKEND_SERVICE)
                        .withDetail("code", clientResponse.statusCode().value())
                        .build()
                )).onErrorResume(ex -> Mono.just(Health.down()
                        .withDetail(SERVICE_DETAIL, BACKEND_SERVICE)
                        .withDetail("message", "Unable to connect to backend service!")
                        .withDetail("exception", ex.getLocalizedMessage())
                        .build()
                ));
    }
}