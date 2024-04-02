package com.akjostudios.acsp.bot.discord.common.command.argument;

public record BotCommandArgument<T>(
        String id,
        Class<T> type,
        T value
) {}