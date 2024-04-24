package com.akjostudios.acsp.common.dto.bot.log.command;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public record CommandResponseCreateRequest(
        @JsonProperty(value = "messageId", required = true) long messageId,
        @JsonProperty(value = "channelId", required = true) long channelId
) {}