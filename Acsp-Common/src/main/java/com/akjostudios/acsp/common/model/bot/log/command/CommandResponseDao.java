package com.akjostudios.acsp.common.model.bot.log.command;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public record CommandResponseDao(
        long responseId,
        long messageId,
        long channelId,
        @NotNull Instant createdAt
) {}