package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.discord.config.BotLocalizationConfig;
import com.github.tonivade.purefun.type.Option;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class BotStringsService {
    private static final Pattern LABEL_PATTERN = Pattern.compile("\\$(.*)\\$");

    private final ResourceBundleMessageSource messageSource;

    public @NotNull Option<String> getString(
            String message,
            @NotNull Option<Locale> locale,
            String@NotNull... placeholders
    ) {
        if (message == null) { return Option.none(); }
        return replacePlaceholder(replaceLabel(message, locale), replacePlaceholderLabel(locale, placeholders));
    }

    public @NotNull String[] replacePlaceholderLabel(
            @NotNull Option<Locale> locale,
            String@NotNull... placeholders
    ) {
        String[] result = new String[placeholders.length];
        for (int i = 0; i < placeholders.length; i++) {
            result[i] = replaceLabel(placeholders[i], locale);
        }
        return result;
    }

    public @NotNull String replaceLabel(
            @NotNull String message,
            @NotNull Option<Locale> locale
    ) {
        Matcher matcher = LABEL_PATTERN.matcher(message);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;
        while (matcher.find()) {
            String label = matcher.group(1);
            String replacement = messageSource.getMessage(label, null, locale.getOrElse(BotLocalizationConfig.DEFAULT_LOCALE));
            result.append(message, lastEnd, matcher.start()).append(replacement);
            lastEnd = matcher.end();
        }
        result.append(message.substring(lastEnd));
        return result.toString();
    }

    public @NotNull Option<String> replacePlaceholder(
            @Nullable String message,
            String@NotNull... placeholders
    ) {
        if (message == null) { return Option.none(); }
        StringBuilder result = new StringBuilder(message);
        for (int i = 0; i < placeholders.length; i++) {
            int placeholderIndex = result.indexOf("$" + i);
            if (placeholderIndex != -1) {
                result.replace(placeholderIndex, placeholderIndex + 2, placeholders[i]);
            }
        }
        return Option.some(result.toString());
    }
}