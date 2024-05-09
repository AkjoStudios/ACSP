package com.akjostudios.acsp.bot.discord.command;

import com.akjostudios.acsp.bot.discord.common.command.BotCommand;
import com.akjostudios.acsp.bot.discord.common.command.BotCommandContext;
import com.akjostudios.acsp.bot.discord.common.command.BotCommandInteractionContext;
import com.akjostudios.acsp.bot.discord.common.component.BotButtonComponent;
import com.github.tonivade.purefun.type.Option;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HelpCommand implements BotCommand {
    @Override
    public void execute(@NotNull BotCommandContext ctx) {
        ctx.answer("Test", List.of(
                ctx.createActionRow(List.of(
                        ctx.createButton("test_button", "Test Button", BotButtonComponent.Style.PRIMARY, Option.none(), false)
                ))
        ));
    }

    @Override
    public void onInteraction(@NotNull BotCommandInteractionContext ctx) {
        ctx.reply(ctx.getArgumentMap().toString(), true);
    }

    @Override
    public String getName() { return "help"; }
}