package com.akjostudios.acsp.common.dto.bot.log.command;

import com.akjostudios.acsp.common.model.bot.log.command.CommandExecutionDao;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@SuppressWarnings("unused")
public record CommandExecutionGetAllResponse(
        @JsonProperty("executions") List<CommandExecutionDao> executions
) {}