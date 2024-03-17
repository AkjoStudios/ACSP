package com.akjostudios.acsp.backend.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.ReactiveHealthContributorRegistry;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;

public class HealthMetricExportConfig {
    @Bean
    public ApplicationListener<ContextRefreshedEvent> healthMetricSetupListener(@NotNull ApplicationContext context) {
        return event -> {
            ReactiveHealthContributorRegistry healthRegistry = context.getBean(ReactiveHealthContributorRegistry.class);
            MeterRegistry meterRegistry = context.getBean(MeterRegistry.class);

            healthRegistry.stream().forEach(
                    healthContributor -> meterRegistry.gauge(
                            "health",
                            Tags.of("name", healthContributor.getName()),
                            healthRegistry,
                            health -> getStatusCode(
                                    ((HealthIndicator) health.getContributor(healthContributor.getName())).getHealth(false).getStatus()
                            )
                    )
            );
        };
    }

    private int getStatusCode(@NotNull Status status) {
        if (status.equals(Status.UP)) return 1;
        if (status.equals(Status.OUT_OF_SERVICE)) return -1;
        if (status.equals(Status.DOWN)) return 0;
        return -2;
    }
}