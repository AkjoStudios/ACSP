package com.akjostudios.acsp.bot.discord.command;

import com.akjostudios.acsp.bot.discord.common.command.BotCommand;
import com.akjostudios.acsp.bot.discord.common.command.BotCommandContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements BotCommand {
    @Override
    public void execute(@NotNull BotCommandContext ctx) { /* Unimplemented */ }

    @Override
    public String getName() { return "help"; }
}