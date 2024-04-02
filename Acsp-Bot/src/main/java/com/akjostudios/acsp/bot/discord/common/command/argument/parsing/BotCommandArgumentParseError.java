package com.akjostudios.acsp.bot.discord.common.command.argument.parsing;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record BotCommandArgumentParseError(
        @NotNull BotCommandArgumentParseErrorType errorType,
        @NotNull List<String> placeholders
) {}
