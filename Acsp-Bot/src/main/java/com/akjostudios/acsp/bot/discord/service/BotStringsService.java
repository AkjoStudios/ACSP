package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.discord.config.BotLocalizationConfig;
import com.github.tonivade.purefun.type.Option;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class BotStringsService {
    private final ResourceBundleMessageSource messageSource;

    public @NotNull Option<String> getString(
            @NotNull String message,
            @NotNull Option<Locale> locale,
            String@NotNull... placeholders
    ) {
        return replace(messageSource.getMessage(
                message, null,
                locale.getOrElse(BotLocalizationConfig.DEFAULT_LOCALE)
        ), placeholders);
    }

    public @NotNull Option<String> replace(
            @Nullable String message,
            String@NotNull... placeholders
    ) {
        if (message == null) { return Option.none(); }
        String result = message;
        for (int i = 0; i < placeholders.length; i++) {
            result = result.replace("$" + i, placeholders[i]);
        }
        return Option.some(result);
    }
}