package com.akjostudios.acsp.bot.discord.common.component.conversion.discord;

import com.akjostudios.acsp.bot.discord.common.component.BotButtonComponent;
import com.akjostudios.acsp.bot.discord.common.component.BotComponent;
import com.akjostudios.acsp.bot.discord.common.component.conversion.BotComponentConverters;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
@SuppressWarnings("java:S119")
public interface BotDiscordComponentConverterProvider<IC extends ItemComponent, BC extends BotComponent, C extends BotDiscordComponentConverter<IC, BC>> {
    @NotNull BotDiscordComponentConverterProvider<Button, BotButtonComponent, BotComponentConverters.BotButtonComponentConverter> BUTTON = BotComponentConverters.BotButtonComponentConverter::new;

    @NotNull C provide();
}
