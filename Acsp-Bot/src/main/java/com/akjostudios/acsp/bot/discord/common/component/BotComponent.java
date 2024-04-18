package com.akjostudios.acsp.bot.discord.common.component;

import com.akjostudios.acsp.bot.discord.config.definition.BotConfigComponent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class BotComponent {
    @NotNull private final BotConfigComponent.Type type;
    private final boolean isItem;

    protected BotComponent(
            @NotNull BotConfigComponent.Type type,
            boolean isItem
    ) {
        this.type = type;
        this.isItem = isItem;
    }
}