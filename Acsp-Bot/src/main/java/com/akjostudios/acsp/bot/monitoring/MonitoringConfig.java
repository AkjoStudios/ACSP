package com.akjostudios.acsp.bot.monitoring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class MonitoringConfig {
    private final HealthMetricExposer healthMetricExposer;

    @Bean
    public ApplicationListener<ContextRefreshedEvent> healthCheckSetupListener() {
        return event -> healthMetricExposer.registerHealthMetrics();
    }

    @Scheduled(initialDelay = 0, fixedRateString = "30000")
    public void healthCheckCacheJob() {
        healthMetricExposer.cacheHealths();
    }
}