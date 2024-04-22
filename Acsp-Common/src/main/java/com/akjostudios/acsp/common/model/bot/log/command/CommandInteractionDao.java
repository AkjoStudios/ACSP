package com.akjostudios.acsp.common.model.bot.log.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

@SuppressWarnings("unused")
public record CommandInteractionDao(
        long interactionId,
        @NotNull String interactionName,
        boolean finished,
        @Nullable Instant finishedAt,
        @NotNull Instant createdAt
) {}