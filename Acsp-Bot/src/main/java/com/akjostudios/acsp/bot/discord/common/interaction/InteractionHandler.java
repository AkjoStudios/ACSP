package com.akjostudios.acsp.bot.discord.common.interaction;

import org.springframework.stereotype.Component;

@Component
public interface InteractionHandler {
    void handleInteraction(String interactionId);
}