package com.akjostudios.acsp.bot.discord.config.definition;

import lombok.Getter;

@Getter
public enum BotConfigCommandArgumentType {
    STRING,
    BYTE,
    SHORT,
    INTEGER,
    LONG,
    FLOAT,
    DOUBLE,
    BOOLEAN,
    USER,
    MEMBER,
    ROLE,
    CHANNEL,
    TEXT_CHANNEL,
    VOICE_CHANNEL,
    CATEGORY,
    EMOTE,
    MESSAGE,
    TIME,
    DURATION,
    URL,
    COLOR
}