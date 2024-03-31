package com.akjostudios.acsp.bot.discord.command;

import com.akjostudios.acsp.bot.discord.common.command.BotCommand;
import com.akjostudios.acsp.bot.discord.common.command.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class BeginCommand implements BotCommand {
    @Override
    public void execute(@NotNull CommandContext ctx) {
        ctx.answer("Hello, World!");
    }

    @Override
    public String getName() { return "begin"; }
}