package com.akjostudios.acsp.bot.discord.common.command.argument.conversion;

import com.akjostudios.acsp.bot.discord.common.command.argument.validation.BotCommandArgumentValidationError;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommandArgumentType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record BotCommandArgumentConversionError(
        @NotNull String name,
        @NotNull String value,
        @NotNull BotConfigCommandArgumentType type,
        @NotNull String jumpUrl
) {
    @Contract(" -> new")
    public BotCommandArgumentValidationError toValidationError() {
        return new BotCommandArgumentValidationError(
                name,
                "$error.command_argument_parsing_report.reason.invalid_type$",
                List.of(
                        type.getTranslationKey(),
                        jumpUrl,
                        type.getTooltipKey()
                )
        );
    }
}