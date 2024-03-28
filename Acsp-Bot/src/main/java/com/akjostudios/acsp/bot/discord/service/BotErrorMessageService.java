package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessage;
import com.github.tonivade.purefun.type.Option;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class BotErrorMessageService {
    private final BotConfigDefinitionService botDefinitionService;
    private final BotStringsService botStringsService;

    public @NotNull Option<BotConfigMessage> getErrorMessage(
            @NotNull String titleLabel,
            @NotNull String descriptionLabel,
            @NotNull List<String> titlePlaceholders,
            @NotNull List<String> descriptionPlaceholders
    ) {
        return getErrorMessage(titleLabel, descriptionLabel, titlePlaceholders, descriptionPlaceholders, Option.none());
    }

    public @NotNull Option<BotConfigMessage> getErrorMessage(
            @NotNull String titleLabel,
            @NotNull String descriptionLabel,
            @NotNull List<String> titlePlaceholders,
            @NotNull List<String> descriptionPlaceholders,
            @NotNull Option<Locale> locale
    ) {
        return botDefinitionService.getMessageDefinition(
                "error", locale,
                botStringsService.getString(titleLabel, locale, titlePlaceholders.toArray(String[]::new)).getOrElse("Unknown Error"),
                botStringsService.getString(descriptionLabel, locale, descriptionPlaceholders.toArray(String[]::new)).getOrElse("An unknown error occurred."),
                "ACSP Bot", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault()).format(Instant.now())
        );
    }

    public @NotNull Option<BotConfigMessage> getInternalErrorMessage(
            @NotNull String message
    ) {
        return getInternalErrorMessage(message, Option.none());
    }

    public @NotNull Option<BotConfigMessage> getInternalErrorMessage(
            @NotNull String message,
            @NotNull Option<Locale> locale
    ) {
        return botDefinitionService.getMessageDefinition(
                "internal-error", locale,
                message, "ACSP Bot", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault()).format(Instant.now())
        );
    }
}