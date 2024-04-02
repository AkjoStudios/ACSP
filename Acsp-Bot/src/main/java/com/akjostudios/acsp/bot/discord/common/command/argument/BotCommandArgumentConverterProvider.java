package com.akjostudios.acsp.bot.discord.common.command.argument;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface BotCommandArgumentConverterProvider<T, C extends BotCommandArgumentConverter<T>> {
    @NotNull BotCommandArgumentConverterProvider<String, BotCommandArgumentConverters.StringConverter> STRING = BotCommandArgumentConverters.StringConverter::new;
    @NotNull BotCommandArgumentConverterProvider<Integer, BotCommandArgumentConverters.IntegerConverter> INTEGER = BotCommandArgumentConverters.IntegerConverter::new;

    @NotNull C provide();
}