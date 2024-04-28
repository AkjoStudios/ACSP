package com.akjostudios.acsp.bot.discord.common.component;


import com.akjostudios.acsp.bot.discord.config.definition.BotConfigComponent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class BotActionRowComponent extends BotComponent {
    @NotNull private final List<? extends BotComponent> components;

    public BotActionRowComponent(
            @NotNull List<? extends BotComponent> components
    ) {
        super(BotConfigComponent.Type.ACTION_ROW, false);
        this.components = components;
    }
}