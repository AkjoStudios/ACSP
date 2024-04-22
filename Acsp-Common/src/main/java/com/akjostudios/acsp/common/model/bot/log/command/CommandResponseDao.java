package com.akjostudios.acsp.common.model.bot.log.command;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;

public record CommandResponseDao(
        long responseId,
        long messageId,
        long channelId,
        @NotNull Instant finishedAt,
        @NotNull Instant createdAt,
        @NotNull List<CommandInteractionDao> interactions
) {}