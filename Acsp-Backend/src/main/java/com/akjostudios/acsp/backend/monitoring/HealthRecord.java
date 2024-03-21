package com.akjostudios.acsp.backend.monitoring;

import org.springframework.boot.actuate.health.Health;

import java.time.Duration;
import java.time.Instant;

public record HealthRecord(
        Health health,
        Instant expirationEnd,
        Duration executionTime
) {}