package com.akjostudios.acsp.bot.discord.common.command;

import org.springframework.stereotype.Component;

@Component
public interface BotCommand {
    void execute(CommandContext ctx);
    String getName();
}