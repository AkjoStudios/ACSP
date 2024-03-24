package com.akjostudios.acsp.bot.discord.internal;

import com.akjostudios.acsp.bot.discord.api.AcspBot;
import com.akjostudios.acsp.bot.discord.impl.AcspBotImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotHealthIndicator implements ReactiveHealthIndicator {
    private final AcspBot acspBot;

    @Override
    public Mono<Health> health() {
        Map<String, Object> details = Map.of(
                "status", acspBot.getStatus().toString(),
                "version", acspBot.getVersion().toString(),
                "environment", AcspBotImpl.getEnvironment()
        );

        return acspBot.getStatus().equals(JDA.Status.CONNECTED)
                ? Mono.just(Health.up().withDetails(details).build())
                : Mono.just(Health.down().withDetails(details).build());
    }
}