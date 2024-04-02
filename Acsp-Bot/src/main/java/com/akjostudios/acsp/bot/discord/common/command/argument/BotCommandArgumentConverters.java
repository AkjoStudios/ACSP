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
        public @NotNull Validation<BotConfigCommandArgumentType, String> convert(@NotNull String value) {
            return Validation.valid(value);
        }
    }

    public static class IntegerConverter implements BotCommandArgumentConverter<Integer> {
        @Override
        public @NotNull Validation<BotConfigCommandArgumentType, Integer> convert(@NotNull String value) {
            return Try.of(() -> Integer.parseInt(value))
                    .toEither()
                    .mapLeft(throwable -> BotConfigCommandArgumentType.INTEGER)
                    .toValidation();
        }
    }

    @SuppressWarnings("java:S1452")
    public static @NotNull BotCommandArgumentConverterProvider<?, ?> from(
            @NotNull BotConfigCommandArgumentType type
    ) {
        return switch (type) {
            case STRING -> BotCommandArgumentConverterProvider.STRING;
            case INTEGER -> BotCommandArgumentConverterProvider.INTEGER;
            default -> throw new IllegalArgumentException("Unsupported argument type: " + type);
        };
    }
}