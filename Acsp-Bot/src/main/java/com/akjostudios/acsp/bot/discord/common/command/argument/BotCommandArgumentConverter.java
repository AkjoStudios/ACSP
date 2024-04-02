package com.akjostudios.acsp.bot.discord.common.command.argument;

import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommandArgumentType;
import com.github.tonivade.purefun.type.Validation;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface BotCommandArgumentConverter<T> {
    @NotNull Validation<BotConfigCommandArgumentType, T> convert(@NotNull String value);
}