package com.akjostudios.acsp.common.dto.bot.log.command;

import com.akjostudios.acsp.common.model.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CommandExecutionFinishResponse(
        @JsonProperty(value = "status", required = true) ResponseStatus status,
        @JsonProperty(value = "error") String error
) {}