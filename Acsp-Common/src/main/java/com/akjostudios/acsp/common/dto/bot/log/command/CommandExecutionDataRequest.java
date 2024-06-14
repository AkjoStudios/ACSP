package com.akjostudios.acsp.common.dto.bot.log.command;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@SuppressWarnings("unused")
public record CommandExecutionDataRequest(
        @JsonProperty(value = "data", required = true) Map<String, Object> data
) {}