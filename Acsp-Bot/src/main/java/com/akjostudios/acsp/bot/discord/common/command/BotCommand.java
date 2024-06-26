package com.akjostudios.acsp.bot.discord.common.command;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public interface BotCommand {
    void init(@NotNull IBotCommandContext ctx);
    void execute(@NotNull BotCommandContext ctx);
    void onInteraction(@NotNull BotCommandInteractionContext ctx);
    String getName();
}