package com.akjostudios.acsp.common.dto.supertokens;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record SupertokenJwtRequest(
        @JsonProperty("payload") Map<String, Object> payload,
        @JsonProperty("algorithm") String algorithm,
        @JsonProperty("jwksDomain") String issuer,
        @JsonProperty("validity") int validitySeconds

) {}