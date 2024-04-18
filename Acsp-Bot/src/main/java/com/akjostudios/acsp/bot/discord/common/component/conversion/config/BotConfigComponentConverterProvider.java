package com.akjostudios.acsp.bot.discord.common.component.conversion.config;

import com.akjostudios.acsp.bot.discord.common.component.BotActionRowComponent;
import com.akjostudios.acsp.bot.discord.common.component.BotButtonComponent;
import com.akjostudios.acsp.bot.discord.common.component.BotComponent;
import com.akjostudios.acsp.bot.discord.common.component.conversion.BotComponentConverters;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface BotConfigComponentConverterProvider<T extends BotComponent, C extends BotConfigComponentConverter<T>> {
    @NotNull BotConfigComponentConverterProvider<BotActionRowComponent, BotComponentConverters.BotActionRowComponentConverter> ACTION_ROW = BotComponentConverters.BotActionRowComponentConverter::new;
    @NotNull BotConfigComponentConverterProvider<BotButtonComponent, BotComponentConverters.BotButtonComponentConverter> BUTTON = BotComponentConverters.BotButtonComponentConverter::new;

    @NotNull C provide();
}