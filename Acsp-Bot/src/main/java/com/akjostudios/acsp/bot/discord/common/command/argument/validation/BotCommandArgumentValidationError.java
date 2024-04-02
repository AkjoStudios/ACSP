package com.akjostudios.acsp.bot.discord.common.command.argument.validation;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record BotCommandArgumentValidationError(
        @NotNull String argumentName,
        @NotNull String reasonKey,
        @NotNull List<String> reasonPlaceholders
) {}