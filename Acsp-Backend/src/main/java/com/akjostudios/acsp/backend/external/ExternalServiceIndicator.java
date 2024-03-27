package com.akjostudios.acsp.backend.external;

import com.akjostudios.acsp.backend.external.model.supertokens.SupertokenApiVersions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("java:S6830")
public class ExternalServiceIndicator implements ReactiveHealthIndicator {
    private static final String SERVICE_DETAIL = "service";
    private static final String SUPERTOKENS_SERVICE = "supertokens";
    private static final String BOT_SERVICE = "bot";

    @Qualifier("client.service.supertokens")
    private final WebClient superTokensClient;

    @Qualifier("client.service.bot")
    private final WebClient botClient;

    @Override
    public Mono<Health> health() {
        return Flux.merge(
                checkSupertokensService(),
                checkBotService()
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

    private @NotNull Mono<Health> checkSupertokensService() {
        return superTokensClient.get().uri("/apiversion")
                .exchangeToMono(clientResponse -> {
                    Mono<Health.Builder> healthBuilderMono = Mono.just(
                            clientResponse.statusCode().is2xxSuccessful() ? Health.up() : Health.down()
                    ).map(builder -> builder
                            .withDetail(SERVICE_DETAIL, SUPERTOKENS_SERVICE)
                            .withDetail("code", clientResponse.statusCode().value())
                    );

                    if (!clientResponse.statusCode().is2xxSuccessful()) {
                        return healthBuilderMono.map(Health.Builder::build);
                    }

                    return clientResponse.bodyToMono(SupertokenApiVersions.class)
                            .map(SupertokenApiVersions::getLatestVersion)
                            .flatMap(version -> healthBuilderMono.map(builder -> builder
                                    .withDetail("version", version.toString())
                                    .build()
                            ));
                }).onErrorResume(ex -> Mono.just(Health.down()
                        .withDetail(SERVICE_DETAIL, SUPERTOKENS_SERVICE)
                        .withDetail("message", "Unable to connect to supertokens service!")
                        .withDetail("exception", ex.getLocalizedMessage())
                        .build()
                ));
    }

    private @NotNull Mono<Health> checkBotService() {
        return botClient.get().uri("/actuator/health/liveness")
                .exchangeToMono(clientResponse -> Mono.just(
                        clientResponse.statusCode().is2xxSuccessful() ? Health.up() : Health.down()
                ).map(builder -> builder
                        .withDetail(SERVICE_DETAIL, BOT_SERVICE)
                        .withDetail("code", clientResponse.statusCode().value())
                        .build()
                )).onErrorResume(ex -> Mono.just(Health.down()
                        .withDetail(SERVICE_DETAIL, BOT_SERVICE)
                        .withDetail("message", "Unable to connect to bot service!")
                        .withDetail("exception", ex.getLocalizedMessage())
                        .build()
                ));
    }
}