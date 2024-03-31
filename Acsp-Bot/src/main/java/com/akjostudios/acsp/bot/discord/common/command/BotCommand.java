package com.akjostudios.acsp.bot.discord.common.command;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public interface BotCommand {
    void execute(@NotNull CommandContext ctx);
    String getName();
}