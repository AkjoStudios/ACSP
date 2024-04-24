package com.akjostudios.acsp.common.dto.bot.log.command;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@SuppressWarnings("unused")
public record CommandExecutionCreateRequest(
        @JsonProperty(value = "messageId", required = true) long messageId,
        @JsonProperty(value = "channelId", required = true) long channelId,
        @JsonProperty(value = "userId", required = true) long userId,
        @JsonProperty(value = "commandName", required = true) String commandName,
        @JsonProperty(value = "commandArgs", required = true) Map<String, String> commandArgs,
        @JsonProperty(value = "subcommandName") String subcommandName
) {}