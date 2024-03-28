package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.discord.common.BotEnvironment;
import com.akjostudios.acsp.bot.discord.config.BotConfigProperties;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServer;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannel;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerRole;
import com.akjostudios.acsp.bot.discord.impl.AcspBotImpl;
import com.github.tonivade.purefun.type.Option;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class BotConfigLayoutService {
    private final BotConfigProperties properties;

    public @NotNull Option<BotConfigServer> getServerLayout() {
        BotEnvironment environment = AcspBotImpl.getEnvironment().getOrElseThrow();

        return Option.from(properties.getLayout().entrySet().stream()
                .filter(entry -> entry.getKey().equals(environment.toConfigEnvironment()))
                .map(Map.Entry::getValue)
                .findFirst());
    }

    public @NotNull Option<Long> getRoleId(
            @NotNull BotConfigServer server,
            @NotNull BotConfigServerRole role
    ) {
        return Option.from(server.getRoles().entrySet().stream()
                .filter(entry -> entry.getKey().equals(role))
                .map(Map.Entry::getValue)
                .findFirst());
    }

    public @NotNull Option<Long> getChannelId(
            @NotNull BotConfigServer server,
            @NotNull BotConfigServerChannel channel
    ) {
        return Option.from(server.getChannels().entrySet().stream()
                .filter(entry -> entry.getKey().equals(channel))
                .map(Map.Entry::getValue)
                .findFirst());
    }
}