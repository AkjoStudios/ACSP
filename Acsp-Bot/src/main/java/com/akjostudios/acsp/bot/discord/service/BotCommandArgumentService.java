package com.akjostudios.acsp.bot.discord.service;


import com.akjostudios.acsp.bot.AcspBotApp;
import com.akjostudios.acsp.bot.discord.common.command.BotCommandArgument;
import com.akjostudios.acsp.bot.discord.common.command.CommandContext;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessage;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import com.github.tonivade.purefun.type.Validation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class BotCommandArgumentService {
    private final BotErrorMessageService errorMessageService;
    private final DiscordMessageService discordMessageService;
    private final BotDefinitionService botDefinitionService;
    private final BotStringsService botStringsService;

    public @NotNull Validation<ArgumentParseError, Map<String, String>> parseArguments(
            @NotNull CommandContext ctx,
            @NotNull List<String> arguments
    ) {

        return Validation.valid(Map.of());
    }

    @SuppressWarnings("java:S1452")
    public @NotNull List<BotCommandArgument<?>> convertArguments(
            @NotNull CommandContext ctx,
            @NotNull Map<String, String> arguments
    ) {
        return List.of();
    }

    @SuppressWarnings("java:S1452")
    public @NotNull List<Validation<ArgumentValidationError, BotCommandArgument<?>>> validateArguments(
            @NotNull CommandContext ctx,
            @NotNull List<BotCommandArgument<?>> arguments
    ) {
        return List.of();
    }

    public @NotNull Try<BotConfigMessage> getValidationReport(
            @NotNull CommandContext ctx,
            @NotNull List<Validation<ArgumentValidationError, BotCommandArgument<?>>> validations,
            @NotNull Option<Locale> locale
    ) {
        return discordMessageService.injectFields(
                botDefinitionService.getMessageDefinition(
                        "argument-validation-report",
                        locale,
                        ctx.getName(),
                        AcspBotApp.BOT_NAME,
                        AcspBotApp.DATE_TIME_FORMATTER.format(Instant.now())
                ), Option.none(), validations.stream()
                        .filter(Validation::isInvalid)
                        .map(Validation::getError)
                        .map(error -> botDefinitionService.getFieldDefinition(
                                "argument-validation.error",
                                locale,
                                error.argumentName(),
                                botStringsService.getString(
                                        error.reasonKey(),
                                        locale,
                                        List.of(),
                                        error.reasonPlaceholders().toArray(String[]::new)
                                ).getOrElse("Unknown error!")
                        )).toList()
        );
    }

    public @NotNull Try<BotConfigMessage> getCommandErrorMessage(
            @NotNull ArgumentParseError error
    ) {
        return getCommandErrorMessage(error.errorType().getCommandKey(), error.placeholders());
    }

    public @NotNull Try<BotConfigMessage> getSubcommandErrorMessage(
            @NotNull ArgumentParseError error
    ) {
        return getSubcommandErrorMessage(error.errorType().getSubcommandKey(), error.placeholders());
    }

    private @NotNull Try<BotConfigMessage> getCommandErrorMessage(
           @NotNull String key,
           @NotNull List<String> placeholders
    ) {
        return errorMessageService.getErrorMessage(
                "$error.command." + key + ".title$",
                "$error.command." + key + ".description$",
                placeholders
        );
    }

    private @NotNull Try<BotConfigMessage> getSubcommandErrorMessage(
            @NotNull String key,
            @NotNull List<String> placeholders
    ) {
        return errorMessageService.getErrorMessage(
                "$error.subcommand." + key + ".title$",
                "$error.subcommand." + key + ".description$",
                placeholders
        );
    }

    @Getter
    @RequiredArgsConstructor
    public enum ArgumentParseErrorType {
        INVALID_ORDER("invalid_order", "invalid_order"),
        TOO_MANY_ARGUMENTS("command.too_many_arguments", "subcommand.too_many_arguments"),
        TOO_MANY_ARGUMENTS_NONE("command.too_many_arguments_none", "subcommand.too_many_arguments_none"),
        REQUIRED_ARGUMENT_MISSING("command.required_argument_missing", "subcommand.required_argument_missing"),
        UNKNOWN_ARGUMENT("command.unknown_argument", "subcommand.unknown_argument"),
        DUPLICATE_ARGUMENT("command.duplicate_argument", "subcommand.duplicate_argument"),
        OTHER("command.other", "subcommand.other");

        private final String commandKey;
        private final String subcommandKey;
    }

    public @NotNull record ArgumentParseError(
            @NotNull ArgumentParseErrorType errorType,
            @NotNull List<String> placeholders
    ) {}

    public record ArgumentValidationError(
            @NotNull String argumentName,
            @NotNull String reasonKey,
            @NotNull List<String> reasonPlaceholders
    ) {}
}