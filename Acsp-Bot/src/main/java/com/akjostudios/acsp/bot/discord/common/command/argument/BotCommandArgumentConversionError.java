package com.akjostudios.acsp.bot.discord.common.command.argument;

import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommandArgumentType;
import com.akjostudios.acsp.bot.discord.service.BotCommandArgumentService;
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
    public BotCommandArgumentService.@NotNull ArgumentValidationError toValidationError() {
        return new BotCommandArgumentService.ArgumentValidationError(
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