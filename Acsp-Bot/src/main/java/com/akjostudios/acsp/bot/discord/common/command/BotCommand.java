package com.akjostudios.acsp.bot.discord.common.command;

import com.akjostudios.acsp.bot.discord.common.interaction.InteractionHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public interface BotCommand extends InteractionHandler {
    void execute(@NotNull BotCommandContext ctx);
    String getName();
}