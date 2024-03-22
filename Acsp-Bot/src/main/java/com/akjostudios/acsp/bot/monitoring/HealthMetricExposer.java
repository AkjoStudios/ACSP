package com.akjostudios.acsp.bot.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthContributorRegistry;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.concurrent.Queues;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class HealthMetricExposer {
    private static final String HEALTH_METRIC_NAME = "health";
    private static final String HEALTH_EXECUTION_TIME_METRIC_NAME = "health_execution_time";
    private static final String METRIC_TAG_CONTRIBUTOR = "contributor";
    private static final String METRIC_TAG_APP_NAME = "app_name";
    private static final String METRIC_TAG_APP_VERSION = "app_version";

    private final ReactiveHealthContributorRegistry healthContributorRegistry;
    private final MeterRegistry meterRegistry;
    private final BuildProperties buildProperties;

    private ConcurrentHashMap<String, HealthRecord> healthStatusCache;
    private Scheduler healthCheckScheduler;

    @PostConstruct
    public void initialize() {
        healthStatusCache = new ConcurrentHashMap<>();
        healthCheckScheduler = Schedulers.newParallel(
                "health_monitoring", 64, true
        );
    }

    public void cacheHealths() {
        log.debug("(Re)caching health checks...");
        Flux.fromStream(
                healthContributorRegistry.stream().filter(Objects::nonNull)
        ).flatMap(healthContributor -> {
            Instant start = Instant.now();
            return Mono.defer(
                    () -> ((ReactiveHealthIndicator) healthContributor.getContributor()).getHealth(false)
            ).subscribeOn(healthCheckScheduler)
                    .timeout(Duration.ofMillis(60000))
                    .onErrorResume(ex -> Mono.just(Health.down().build()))
                    .map(health -> new HealthRecord(
                            health,
                            start.plusMillis(60000),
                            Duration.between(start, Instant.now())
                    )).doOnNext(healthRecord -> {
                        healthStatusCache.compute(
                                healthContributor.getName(),
                                (key, existingRecord) -> {
                                    if (existingRecord == null || existingRecord.expirationEnd().isBefore(Instant.now())) {
                                        return healthRecord;
                                    }
                                    return existingRecord;
                                }
                        );
                        meterRegistry.timer(HEALTH_EXECUTION_TIME_METRIC_NAME, List.of(
                                Tag.of(METRIC_TAG_CONTRIBUTOR, healthContributor.getName()),
                                Tag.of(METRIC_TAG_APP_NAME, buildProperties.getName()),
                                Tag.of(METRIC_TAG_APP_VERSION, buildProperties.getVersion())
                        )).record(healthRecord.executionTime());
                    }).doOnSuccess(healthRecord -> log.debug(
                            "Health check '{}' cached with status '{}' which took {}ms and will expire at {}.",
                            healthContributor.getName(),
                            healthRecord.health().getStatus().getCode(),
                            healthRecord.executionTime().toMillis(),
                            ZonedDateTime.ofInstant(healthRecord.expirationEnd(), ZoneId.systemDefault())
                    )).map(health -> healthContributor.getName());
        }, Queues.SMALL_BUFFER_SIZE).collectList().subscribe();
    }

    @SuppressWarnings("java:S3864")
    public void registerHealthMetrics() {
        List<String> healthContributors = healthContributorRegistry.stream()
                .filter(Objects::nonNull)
                .peek(healthContributor -> log.debug(
                        "Registering health metric for contributor '{}'.",
                        healthContributor.getName()
                )).map(healthContributor -> {
                    meterRegistry.gauge(HEALTH_METRIC_NAME, List.of(
                            Tag.of(METRIC_TAG_CONTRIBUTOR, healthContributor.getName()),
                            Tag.of(METRIC_TAG_APP_NAME, buildProperties.getName()),
                            Tag.of(METRIC_TAG_APP_VERSION, buildProperties.getVersion())
                    ), this, healthMetricExposer -> healthMetricExposer.getHealthStatusValue(
                            healthContributor.getName()
                    ));
                    meterRegistry.timer(HEALTH_EXECUTION_TIME_METRIC_NAME, List.of(
                            Tag.of(METRIC_TAG_CONTRIBUTOR, healthContributor.getName()),
                            Tag.of(METRIC_TAG_APP_NAME, buildProperties.getName()),
                            Tag.of(METRIC_TAG_APP_VERSION, buildProperties.getVersion())
                    ));
                    return healthContributor.getName();
                }).toList();
        log.info("Registered health metrics for contributors: {}", healthContributors);
    }

    public double getHealthStatusValue(String healthCheckName) {
        HealthRecord healthRecord = healthStatusCache.get(healthCheckName);
        if (healthRecord == null) {
            return HealthStatus.UNKNOWN.value();
        }

        log.debug(
                "Returning cached health status value '{}' for health check '{}'. Is expired: {}",
                healthRecord.health().getStatus().getCode(),
                healthCheckName,
                healthRecord.expirationEnd().isBefore(Instant.now())
        );
        return HealthStatus.fromRecord(healthRecord).value();
    }

    @RequiredArgsConstructor
    @Getter
    @Accessors(fluent = true)
    private enum HealthStatus {
        UP(1), OUT_OF_SERVICE(0.5), DOWN(0), UNKNOWN(-1);

        private final double value;

        public static HealthStatus fromRecord(HealthRecord healthRecord) {
            if (Status.UP.equals(healthRecord.health().getStatus())) { return UP; }
            if (Status.OUT_OF_SERVICE.equals(healthRecord.health().getStatus())) { return OUT_OF_SERVICE; }
            if (Status.DOWN.equals(healthRecord.health().getStatus())) { return DOWN; }
            return UNKNOWN;
        }
    }
}