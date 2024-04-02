package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.AcspBotApp;
import com.akjostudios.acsp.bot.discord.common.command.CommandContext;
import com.akjostudios.acsp.bot.discord.common.command.argument.BotCommandArgument;
import com.akjostudios.acsp.bot.discord.common.command.argument.BotCommandArgumentConversionError;
import com.akjostudios.acsp.bot.discord.common.command.argument.BotCommandArgumentConverters;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommand;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessage;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import com.github.tonivade.purefun.type.Validation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
        Map<String, String> result = new HashMap<>();

        if (arguments.isEmpty()) { return Validation.valid(result); }

        Map<Integer, Boolean> indexTypeMap = new HashMap<>();
        AtomicInteger index = new AtomicInteger(0);
        List<BotConfigCommand.Argument> argumentDefinitions = ctx.getArgumentDefinitions();

        if (arguments.size() > argumentDefinitions.size()) {
            return Validation.invalid(getTooManyArgumentsError(ctx, argumentDefinitions));
        } else {
            AtomicReference<ArgumentParseError> error = new AtomicReference<>(null);
            arguments.forEach(argument -> parseArgument(
                    ctx, argumentDefinitions, result, indexTypeMap, index, error, argument
            ));
            if (error.get() != null) { return Validation.invalid(error.get()); }
        }

        Validation<ArgumentParseError, List<String>> unknownArgumentsCheck =
                checkUnknownArguments(ctx, argumentDefinitions, result);
        if (unknownArgumentsCheck.isInvalid()) { return Validation.invalid(unknownArgumentsCheck.getError()); }

        Validation<ArgumentParseError, List<String>> missingArgumentsCheck =
                checkMissingArguments(ctx, argumentDefinitions, result);
        if (missingArgumentsCheck.isInvalid()) { return Validation.invalid(missingArgumentsCheck.getError()); }

        return Validation.valid(result);
    }

    private void parseArgument(
            @NotNull CommandContext ctx,
            @NotNull List<BotConfigCommand.Argument> argumentDefinitions,
            @NotNull Map<String, String> result,
            @NotNull Map<Integer, Boolean> indexTypeMap,
            @NotNull AtomicInteger index,
            @NotNull AtomicReference<ArgumentParseError> error,
            @NotNull String argument
    ) {
        if (indexTypeMap.containsValue(true) && !argument.contains("=")) {
            error.set(new ArgumentParseError(
                    ArgumentParseErrorType.INVALID_ORDER,
                    List.of()
            ));
            return;
        }

        boolean isNamed = argument.contains("=");
        Validation<ArgumentParseError, Map.Entry<String, String>> parsedArgument = isNamed
                ? getNamedArgument(ctx, argument, argumentDefinitions)
                : Validation.valid(Map.entry(argumentDefinitions.get(index.get()).getId(), argument));

        if (parsedArgument.isInvalid()) {
            error.set(parsedArgument.getError());
            return;
        }

        Map.Entry<String, String> entry = parsedArgument.get();
        if (result.containsKey(entry.getKey())) {
            error.set(new ArgumentParseError(
                    ArgumentParseErrorType.DUPLICATE_ARGUMENT,
                    ctx.isSubcommand() ? List.of(
                            entry.getKey(),
                            ctx.getSubcommandName().getOrElse("???"),
                            ctx.getName()
                    ) : List.of(
                            entry.getKey(),
                            ctx.getName()
                    )
            ));
            return;
        }

        result.put(entry.getKey(), entry.getValue());
        indexTypeMap.put(index.get(), isNamed);
        index.getAndIncrement();
    }

    private @NotNull Validation<ArgumentParseError, Map.Entry<String, String>> getNamedArgument(
            @NotNull CommandContext ctx,
            @NotNull String argument,
            @NotNull List<BotConfigCommand.Argument> argumentDefinitions
    ) {
        String[] split = argument.split("=", 2);
        String name = split[0];

        if (name.isBlank()) {
            return Validation.invalid(new ArgumentParseError(
                    ArgumentParseErrorType.INVALID_NAMED_ARGUMENT,
                    ctx.isSubcommand() ? List.of(
                            argument,
                            ctx.getSubcommandName().getOrElse("???"),
                            ctx.getName()
                    ) : List.of(
                            argument,
                            ctx.getName()
                    )
            ));
        }

        return Validation.valid(Map.entry(name, split[1]));
    }

    @SuppressWarnings("java:S3358")
    private @NotNull ArgumentParseError getTooManyArgumentsError(
            @NotNull CommandContext ctx,
            @NotNull List<BotConfigCommand.Argument> argumentDefinitions
    ) {
        return argumentDefinitions.isEmpty() ? new ArgumentParseError(
                ArgumentParseErrorType.TOO_MANY_ARGUMENTS_NONE,
                ctx.isSubcommand() ? List.of(
                        ctx.getSubcommandName().getOrElse("???"),
                        ctx.getName()
                ) : List.of(
                        ctx.getName()
                )
        ) : new ArgumentParseError(
                ArgumentParseErrorType.TOO_MANY_ARGUMENTS,
                ctx.isSubcommand() ? List.of(
                        ctx.getSubcommandName().getOrElse("???"),
                        ctx.getName(),
                        String.valueOf(argumentDefinitions.size())
                ) : List.of(
                        ctx.getName(),
                        String.valueOf(argumentDefinitions.size())
                )
        );
    }

    private Validation<ArgumentParseError, List<String>> checkUnknownArguments(
            @NotNull CommandContext ctx,
            @NotNull List<BotConfigCommand.Argument> argumentDefinitions,
            @NotNull Map<String, String> result
    ) {
        List<String> unknownArguments = result.keySet().stream()
                .filter(argument -> argumentDefinitions.stream()
                        .noneMatch(definition -> definition.getId().equals(argument))
                ).toList();
        if (!unknownArguments.isEmpty()) {
            return Validation.invalid(new ArgumentParseError(
                    ArgumentParseErrorType.UNKNOWN_ARGUMENT,
                    ctx.isSubcommand() ? List.of(
                            unknownArguments.getFirst(),
                            ctx.getSubcommandName().getOrElse("???"),
                            ctx.getName()
                    ) : List.of(
                            unknownArguments.getFirst(),
                            ctx.getName()
                    )
            ));
        }

        return Validation.valid(unknownArguments);
    }

    private Validation<ArgumentParseError, List<String>> checkMissingArguments(
            @NotNull CommandContext ctx,
            @NotNull List<BotConfigCommand.Argument> argumentDefinitions,
            @NotNull Map<String, String> result
    ) {
        List<String> missingArguments = argumentDefinitions.stream()
                .filter(definition -> !result.containsKey(definition.getId()))
                .filter(BotConfigCommand.Argument::isRequired)
                .map(BotConfigCommand.Argument::getId)
                .toList();
        if (!missingArguments.isEmpty()) {
            return Validation.invalid(new ArgumentParseError(
                    ArgumentParseErrorType.REQUIRED_ARGUMENT_MISSING,
                    ctx.isSubcommand() ? List.of(
                            missingArguments.getFirst(),
                            ctx.getSubcommandName().getOrElse("???"),
                            ctx.getName()
                    ) : List.of(
                            missingArguments.getFirst(),
                            ctx.getName()
                    )
            ));
        }

        return Validation.valid(missingArguments);
    }

    @SuppressWarnings("java:S1452")
    public @NotNull List<Validation<BotCommandArgumentConversionError, BotCommandArgument<?>>> convertArguments(
            @NotNull CommandContext ctx,
            @NotNull Map<String, String> arguments
    ) {
        return arguments.entrySet().stream()
                .map(entry -> convertArgument(ctx, entry.getKey(), entry.getValue()))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private @NotNull Validation<BotCommandArgumentConversionError, BotCommandArgument<?>> convertArgument(
            @NotNull CommandContext ctx,
            @NotNull String argumentName,
            @NotNull String argumentValue
    ) {
        return BotCommandArgumentConverters.from(
                ctx.getArgumentDefinitions().stream()
                        .filter(definition -> definition.getId().equals(argumentName))
                        .findFirst()
                        .map(BotConfigCommand.Argument::getType)
                        .orElseThrow()
        ).convert(argumentName, argumentValue, ctx.getJumpUrl()).map(value -> {
            Class<Object> valueClass = (Class<Object>) value.getClass();
            return new BotCommandArgument<>(argumentName, valueClass, value);
        });
    }

    @SuppressWarnings("java:S1452")
    public @NotNull List<Validation<ArgumentValidationError, BotCommandArgument<?>>> validateArguments(
            @NotNull CommandContext ctx,
            @NotNull List<BotCommandArgument<?>> conversions
    ) {
        return conversions.stream()
                .map(argument -> validateArgument(ctx, argument))
                .toList();
    }

    private @NotNull Validation<ArgumentValidationError, BotCommandArgument<?>> validateArgument(
            @NotNull CommandContext ctx,
            @NotNull BotCommandArgument<?> argument
    ) {
        return Validation.valid(argument);
    }

    public @NotNull Try<BotConfigMessage> getValidationReport(
            @NotNull CommandContext ctx,
            @NotNull List<ArgumentValidationError> validations,
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
        return getErrorMessage(error.errorType().getCommandKey(), error.placeholders());
    }

    public @NotNull Try<BotConfigMessage> getSubcommandErrorMessage(
            @NotNull ArgumentParseError error
    ) {
        return getErrorMessage(error.errorType().getSubcommandKey(), error.placeholders());
    }

    private @NotNull Try<BotConfigMessage> getErrorMessage(
           @NotNull String key,
           @NotNull List<String> placeholders
    ) {
        return errorMessageService.getErrorMessage(
                "$error." + key + ".title$",
                "$error." + key + ".description$",
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
        INVALID_NAMED_ARGUMENT("command.invalid_named_argument", "subcommand.invalid_named_argument"),
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