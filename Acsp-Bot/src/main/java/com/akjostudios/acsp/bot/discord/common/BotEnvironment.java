package com.akjostudios.acsp.bot.discord.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@SuppressWarnings("unused")
public enum BotEnvironment {
    UNKNOWN, LOCAL, DEV, PROD
}