package com.akjostudios.acsp.bot.discord.config.definition;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@SuppressWarnings("unused")
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
    COLOR,
    UNKNOWN;

    public @NotNull String getTranslationKey() {
        return "$command.arguments.type." + name().toLowerCase() + "$";
    }

    public @NotNull String getTooltipKey() {
        return "$command.arguments.type." + name().toLowerCase() + ".tooltip$";
    }
}