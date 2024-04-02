package com.akjostudios.acsp.bot.discord.common.command.argument.parsing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BotCommandArgumentParseErrorType {
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