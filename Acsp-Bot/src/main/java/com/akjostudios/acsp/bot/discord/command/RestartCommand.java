package com.akjostudios.acsp.bot.discord.command;

import com.akjostudios.acsp.bot.discord.common.command.BotCommand;
import com.akjostudios.acsp.bot.discord.common.command.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class RestartCommand implements BotCommand {
    @Override
    public void execute(@NotNull CommandContext ctx) { /* Unimplemented */ }

    @Override
    public String getName() { return "restart"; }
}