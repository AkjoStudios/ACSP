package com.akjostudios.acsp.bot.discord.common.command.argument.validation;

import com.akjostudios.acsp.bot.discord.common.command.BotCommandContext;
import com.akjostudios.acsp.bot.discord.common.command.argument.BotCommandArgument;
import com.github.tonivade.purefun.type.Validation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface BotCommandArgumentValidator<T> {
    @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<T>> validate(
            @NotNull BotCommandContext ctx,
            @NotNull BotCommandArgument<T> argument,
            @NotNull Map<String, Object> validation
    );
}