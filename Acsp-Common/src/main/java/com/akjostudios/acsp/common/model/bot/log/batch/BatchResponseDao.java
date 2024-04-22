package com.akjostudios.acsp.common.model.bot.log.batch;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;

@SuppressWarnings("unused")
public record BatchResponseDao(
        long responseId,
        long messageId,
        long channelId,
        @NotNull Instant finishedAt,
        @NotNull Instant createdAt,
        @NotNull List<BatchInteractionDao> interactions
) {}