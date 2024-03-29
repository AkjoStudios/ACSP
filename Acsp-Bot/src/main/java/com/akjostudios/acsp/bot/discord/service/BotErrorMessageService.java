package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.AcspBotApp;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessage;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class BotErrorMessageService {
    private final BotDefinitionService botDefinitionService;

    public @NotNull Try<BotConfigMessage> getErrorMessage(
            @NotNull String errorTitle,
            @NotNull String errorDescription
    ) {
        return getErrorMessage(errorTitle, errorDescription, List.of(), Option.none());
    }

    public @NotNull Try<BotConfigMessage> getErrorMessage(
            @NotNull String errorTitle,
            @NotNull String errorDescription,
            @NotNull Option<Locale> locale
    ) {
        return getErrorMessage(errorTitle, errorDescription, List.of(), locale);
    }

    public @NotNull Try<BotConfigMessage> getErrorMessage(
            @NotNull String errorTitle,
            @NotNull String errorDescription,
            @NotNull List<String> placeholders
    ) {
        return getErrorMessage(errorTitle, errorDescription, placeholders, Option.none());
    }

    public @NotNull Try<BotConfigMessage> getErrorMessage(
            @NotNull String errorTitle,
            @NotNull String errorDescription,
            @NotNull List<String> placeholders,
            @NotNull Option<Locale> locale
    ) {
        return botDefinitionService.getMessageDefinition(
                "error", locale,
                placeholders,
                errorTitle, errorDescription,
                AcspBotApp.BOT_NAME,
                AcspBotApp.DATE_TIME_FORMATTER.format(Instant.now())
        );
    }

    public @NotNull Try<BotConfigMessage> getInternalErrorMessage(
            @NotNull String errorMessage
    ) {
        return getInternalErrorMessage(errorMessage, Option.none());
    }

    public @NotNull Try<BotConfigMessage> getInternalErrorMessage(
            @NotNull String errorMessage,
            @NotNull Option<Locale> locale
    ) {
        return botDefinitionService.getMessageDefinition(
                "internal-error", locale,
                errorMessage,
                AcspBotApp.BOT_NAME,
                AcspBotApp.DATE_TIME_FORMATTER.format(Instant.now())
        );
    }
}