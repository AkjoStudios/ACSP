package com.akjostudios.acsp.bot.discord.common;

import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerEnvironment;
import lombok.Getter;
import org.jetbrains.annotations.Contract;

@Getter
@SuppressWarnings("unused")
public enum BotEnvironment {
    UNKNOWN, LOCAL, DEV, PROD;

    @Contract(pure = true)
    public BotConfigServerEnvironment toConfigEnvironment() {
        return switch(this) {
            case LOCAL, DEV -> BotConfigServerEnvironment.TEST;
            case PROD -> BotConfigServerEnvironment.PROD;
            case UNKNOWN -> throw new IllegalStateException("Cannot convert UNKNOWN to BotConfigServerEnvironment!");
        };
    }
}