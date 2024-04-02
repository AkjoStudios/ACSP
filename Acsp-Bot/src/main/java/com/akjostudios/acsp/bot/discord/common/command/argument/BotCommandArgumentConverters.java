package com.akjostudios.acsp.bot.discord.common.command.argument;

import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommandArgumentType;
import com.github.tonivade.purefun.type.Try;
import com.github.tonivade.purefun.type.Validation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class BotCommandArgumentConverters {
    @Contract(pure = true)
    private BotCommandArgumentConverters() {}

    public static class StringConverter implements BotCommandArgumentConverter<String> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, String> convert(
                @NotNull String name,
                @NotNull String value,
                @NotNull String jumpUrl
        ) {
            return Validation.valid(value);
        }
    }

    public static class IntegerConverter implements BotCommandArgumentConverter<Integer> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, Integer> convert(
                @NotNull String name,
                @NotNull String value,
                @NotNull String jumpUrl
        ) {
            return Try.of(() -> Integer.parseInt(value))
                    .toEither()
                    .mapLeft(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.INTEGER,
                            jumpUrl
                    )).toValidation();
        }
    }

    @SuppressWarnings({"java:S1452", "unchecked"})
    public static <T> @NotNull BotCommandArgumentConverter<T> from(
            @NotNull BotConfigCommandArgumentType type
    ) {
        return (BotCommandArgumentConverter<T>) switch (type) {
            case STRING -> BotCommandArgumentConverterProvider.STRING.provide();
            case INTEGER -> BotCommandArgumentConverterProvider.INTEGER.provide();
            default -> throw new IllegalArgumentException("Unsupported argument type: " + type);
        };
    }
}