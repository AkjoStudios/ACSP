package com.akjostudios.acsp.bot.web.external;

import com.akjostudios.acsp.common.model.supertokens.SupertokenApiVersions;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

@Component("externalServices")
@Slf4j
@SuppressWarnings("java:S6830")
public class ExternalServiceIndicator implements ReactiveHealthIndicator {
    private static final String SERVICE_DETAIL = "service";
    private static final String MESSAGE_DETAIL = "message";
    private static final String EXCEPTION_DETAIL = "exception";
    private static final String CODE_DETAIL = "code";
    private static final String VERSION_DETAIL = "version";

    private static final String SUPERTOKENS_SERVICE = "supertokens";
    private static final String BACKEND_SERVICE = "backend";

    private final ExternalServiceClient superTokensClient;
    private final ExternalServiceClient backendClient;

    @Autowired
    @Contract(pure = true)
    public ExternalServiceIndicator(
            @Qualifier("client.service.supertokens") ExternalServiceClient superTokensClient,
            @Qualifier("client.service.backend") ExternalServiceClient backendClient
    ) {
        this.superTokensClient = superTokensClient;
        this.backendClient = backendClient;
    }

    @Override
    public Mono<Health> health() {
        return Flux.merge(
                checkSupertokensService(),
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

    private @NotNull Mono<Health> checkSupertokensService() {
        return superTokensClient.get("/apiversion", request -> request
                .exchangeToMono(clientResponse -> {
                    Mono<Health.Builder> healthBuilderMono = Mono.just(
                            clientResponse.statusCode().is2xxSuccessful() ? Health.up() : Health.down()
                    ).map(builder -> builder
                            .withDetail(SERVICE_DETAIL, SUPERTOKENS_SERVICE)
                            .withDetail(CODE_DETAIL, clientResponse.statusCode().value())
                    );

                    if (!clientResponse.statusCode().is2xxSuccessful()) {
                        return healthBuilderMono.map(Health.Builder::build);
                    }

                    return clientResponse.bodyToMono(SupertokenApiVersions.class)
                            .map(SupertokenApiVersions::getLatestVersion)
                            .flatMap(version -> healthBuilderMono.map(builder -> builder
                                    .withDetail(VERSION_DETAIL, version.toString())
                                    .build()
                            ));
                }).onErrorResume(ex -> Mono.just(Health.down()
                        .withDetail(SERVICE_DETAIL, SUPERTOKENS_SERVICE)
                        .withDetail(MESSAGE_DETAIL, "Unable to connect to supertokens service!")
                        .withDetail(EXCEPTION_DETAIL, ex.getLocalizedMessage())
                        .build()
                )).timeout(Duration.ofSeconds(5), Mono.just(Health.down()
                        .withDetail(SERVICE_DETAIL, SUPERTOKENS_SERVICE)
                        .withDetail(MESSAGE_DETAIL, "Supertokens service connection timed out!")
                        .build()
                )));
    }

    private @NotNull Mono<Health> checkBackendService() {
        return backendClient.get("/actuator/health/liveness", request -> request
                .exchangeToMono(clientResponse -> Mono.just(
                        clientResponse.statusCode().is2xxSuccessful() ? Health.up() : Health.down()
                ).map(builder -> builder
                        .withDetail(SERVICE_DETAIL, BACKEND_SERVICE)
                        .withDetail(CODE_DETAIL, clientResponse.statusCode().value())
                        .build()
                )).onErrorResume(ex -> Mono.just(Health.down()
                        .withDetail(SERVICE_DETAIL, BACKEND_SERVICE)
                        .withDetail(MESSAGE_DETAIL, "Unable to connect to backend service!")
                        .withDetail(EXCEPTION_DETAIL, ex.getLocalizedMessage())
                        .build()
                )).timeout(Duration.ofSeconds(5), Mono.just(Health.down()
                        .withDetail(SERVICE_DETAIL, BACKEND_SERVICE)
                        .withDetail(MESSAGE_DETAIL, "Backend service connection timed out!")
                        .build()
                )));
    }
}