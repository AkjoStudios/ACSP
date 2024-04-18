package com.akjostudios.acsp.bot.discord.common.component.conversion.discord;

import com.akjostudios.acsp.bot.discord.config.definition.BotConfigComponent;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
@SuppressWarnings("unused")
public enum DiscordComponentType {
    BUTTON;

    @Contract(pure = true)
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public static DiscordComponentType fromConfigType(
            @NotNull BotConfigComponent.Type type
    ) {
        return switch (type) {
            case BUTTON -> BUTTON;
            default -> throw new IllegalArgumentException(
                    "Cannot convert config component type " + type + " to a discord component type!"
            );
        };
    }
}