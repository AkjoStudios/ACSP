package com.akjostudios.acsp.bot.web.external.model.supertokens;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SupertokenJwtResponse {
    @JsonProperty("status")
    private String status;

    @JsonProperty("jwt")
    private String jwt;
}