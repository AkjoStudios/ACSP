package com.akjostudios.acsp.common.model.bot.log.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;

public record CommandExecutionDao(
        long executionId,
        long messageId,
        long channelId,
        @NotNull String commandName,
        @Nullable String subcommandName,
        @NotNull Instant finishedAt,
        @NotNull Instant createdAt,
        @NotNull List<CommandResponseDao> responses
) {}