package com.akjostudios.acsp.common.model.bot.log.batch;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;

@SuppressWarnings("unused")
public record BatchExecutionDao(
        long batchId,
        boolean finished,
        @Nullable Instant finishedAt,
        @NotNull Instant createdAt,
        @NotNull List<BatchResponseDao> responses
) {}