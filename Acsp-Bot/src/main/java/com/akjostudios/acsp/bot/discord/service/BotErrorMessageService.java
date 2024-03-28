package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.AcspBotApp;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessage;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class BotErrorMessageService {
    private final BotDefinitionService botDefinitionService;

    public Try<BotConfigMessage> getErrorMessage(
            String errorTitle,
            String errorDescription
    ) {
        return getErrorMessage(errorTitle, errorDescription, Option.none());
    }

    public Try<BotConfigMessage> getErrorMessage(
            String errorTitle,
            String errorDescription,
            Option<Locale> locale
    ) {
        return botDefinitionService.getMessageDefinition(
                "error", locale,
                errorTitle, errorDescription,
                AcspBotApp.BOT_NAME,
                AcspBotApp.DATE_TIME_FORMATTER.format(Instant.now())
        );
    }

    public Try<BotConfigMessage> getInternalErrorMessage(
            String errorMessage
    ) {
        return getInternalErrorMessage(errorMessage, Option.none());
    }

    public Try<BotConfigMessage> getInternalErrorMessage(
            String errorMessage,
            Option<Locale> locale
    ) {
        return botDefinitionService.getMessageDefinition(
                "internal-error", locale,
                errorMessage,
                AcspBotApp.BOT_NAME,
                AcspBotApp.DATE_TIME_FORMATTER.format(Instant.now())
        );
    }
}