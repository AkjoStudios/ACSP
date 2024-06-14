package com.akjostudios.acsp.common.dto;

import com.akjostudios.acsp.common.model.ResponseStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public record SimpleExternalServiceResponse(
        @JsonProperty(value = "status", required = true) ResponseStatus status,
        @JsonProperty(value = "error") String error
) implements ExternalServiceResponse {}