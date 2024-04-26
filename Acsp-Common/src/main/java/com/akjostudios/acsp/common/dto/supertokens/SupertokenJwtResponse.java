package com.akjostudios.acsp.common.dto.supertokens;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SupertokenJwtResponse(
        @JsonProperty("status") String status,
        @JsonProperty("jwt") String jwt

) {}