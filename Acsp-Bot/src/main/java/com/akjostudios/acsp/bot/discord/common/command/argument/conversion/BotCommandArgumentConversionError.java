package com.akjostudios.acsp.bot.discord.common.command.argument.conversion;

import com.akjostudios.acsp.bot.discord.common.command.argument.validation.BotCommandArgumentValidationError;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommand;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record BotCommandArgumentConversionError(
        @NotNull String name,
        @NotNull String value,
        @NotNull BotConfigCommand.Argument.Type type,
        @NotNull String jumpUrl
) {
    @Contract(" -> new")
    public BotCommandArgumentValidationError toValidationError() {
        return new BotCommandArgumentValidationError(
                name,
                "$error.command_argument_validation_report.reason.invalid_type$",
                List.of(
                        type.getTranslationKey(),
                        jumpUrl,
                        type.getTooltipKey()
                )
        );
    }
}