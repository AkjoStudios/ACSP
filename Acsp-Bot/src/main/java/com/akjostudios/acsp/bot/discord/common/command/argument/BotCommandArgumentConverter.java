package com.akjostudios.acsp.bot.discord.common.command.argument;

import com.github.tonivade.purefun.type.Validation;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface BotCommandArgumentConverter<T> {
    @NotNull Validation<BotCommandArgumentConversionError, T> convert(
            @NotNull String name,
            @NotNull String value,
            @NotNull String jumpUrl
    );
}