package com.akjostudios.acsp.bot.discord.api;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.util.Version;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public interface AcspBot {
    @NotNull JDA.Status getStatus();
    @NotNull Version getVersion();
    void shutdown(@NotNull ConfigurableApplicationContext context);
}