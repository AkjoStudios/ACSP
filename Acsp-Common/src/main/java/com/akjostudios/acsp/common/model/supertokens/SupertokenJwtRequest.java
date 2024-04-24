package com.akjostudios.acsp.common.model.supertokens;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

@RequiredArgsConstructor
@Getter
@ToString
@SuppressWarnings("ClassCanBeRecord")
public class SupertokenJwtRequest {
    @JsonProperty("payload")
    public final Map<String, Object> payload;

    @JsonProperty("algorithm")
    public final String algorithm;

    @JsonProperty("jwksDomain")
    private final String issuer;

    @JsonProperty("validity")
    private final int validitySeconds;
}