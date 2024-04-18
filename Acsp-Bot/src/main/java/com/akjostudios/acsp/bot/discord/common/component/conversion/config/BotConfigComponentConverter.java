package com.akjostudios.acsp.bot.discord.common.component.conversion.config;

import com.akjostudios.acsp.bot.discord.common.component.BotComponent;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigComponent;
import com.akjostudios.acsp.bot.discord.service.BotStringsService;
import com.github.tonivade.purefun.type.Option;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

@FunctionalInterface
public interface BotConfigComponentConverter<T extends BotComponent> {
    @NotNull Option<T> convert(
            @NotNull BotStringsService stringsService,
            @NotNull BotConfigComponent component,
            @NotNull Option<Locale> locale,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    );
}