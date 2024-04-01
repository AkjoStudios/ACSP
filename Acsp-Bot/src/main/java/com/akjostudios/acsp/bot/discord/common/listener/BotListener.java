package com.akjostudios.acsp.bot.discord.common.listener;

import com.akjostudios.acsp.bot.discord.common.BotEventType;
import net.dv8tion.jda.api.events.GenericEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@FunctionalInterface
public interface BotListener<T extends GenericEvent> {
    void onEvent(@NotNull BotEventType type, @NotNull T event);
}