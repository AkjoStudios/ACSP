package com.akjostudios.acsp.common.model.bot.log.batch;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

@SuppressWarnings("unused")
public record BatchInteractionDao(
        long interactionId,
        @NotNull String interactionName,
        boolean finished,
        @Nullable Instant finishedAt,
        @NotNull Instant createdAt
) {}