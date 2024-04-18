package com.akjostudios.acsp.bot.discord.common.component.conversion.discord;

import com.akjostudios.acsp.bot.discord.common.component.BotComponent;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
@SuppressWarnings("java:S119")
public interface BotDiscordComponentConverter<IC extends ItemComponent, BC extends BotComponent> {
    @NotNull IC convert(
            @NotNull BC component
    );
}