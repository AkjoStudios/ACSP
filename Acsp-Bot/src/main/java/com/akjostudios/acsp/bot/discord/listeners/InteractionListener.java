package com.akjostudios.acsp.bot.discord.listeners;

import com.akjostudios.acsp.bot.discord.common.BotEventType;
import com.akjostudios.acsp.bot.discord.common.interaction.InteractionHandler;
import com.akjostudios.acsp.bot.discord.common.listener.BotListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InteractionListener implements BotListener<GenericComponentInteractionCreateEvent> {
    private final List<InteractionHandler> interactionHandlers;

    @Override
    public void onEvent(@NotNull BotEventType type, @NotNull GenericComponentInteractionCreateEvent event) {
        log.info("Interaction event received: {}", event);
    }
}