package com.akjostudios.acsp.common.model.bot.log.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record CommandExecutionDao(
        long executionId,
        long messageId,
        long channelId,
        long userId,
        @NotNull String commandName,
        @NotNull Map<String, String> commandArgs,
        @Nullable String subcommandName,
        @NotNull Map<String, Object> commandData,
        boolean finished,
        @NotNull Instant finishedAt,
        @NotNull Instant createdAt,
        @NotNull List<CommandResponseDao> responses
) {}