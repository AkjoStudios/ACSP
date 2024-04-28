package com.akjostudios.acsp.common.dto.bot.log.command;

import com.akjostudios.acsp.common.dto.ExternalServiceResponse;
import com.akjostudios.acsp.common.model.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public record CommandExecutionFinishResponse(
        @JsonProperty(value = "status", required = true) ResponseStatus status,
        @JsonProperty(value = "error") String error
) implements ExternalServiceResponse {}