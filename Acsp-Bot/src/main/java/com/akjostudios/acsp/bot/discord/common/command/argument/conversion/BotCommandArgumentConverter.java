package com.akjostudios.acsp.bot.discord.common.command.argument.conversion;

import com.akjostudios.acsp.bot.discord.common.command.BotCommandContext;
import com.github.tonivade.purefun.type.Validation;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface BotCommandArgumentConverter<T> {
    @NotNull Validation<BotCommandArgumentConversionError, T> convert(
            @NotNull BotCommandContext ctx,
            @NotNull String name,
            @NotNull String value
    );
}