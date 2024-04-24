package com.akjostudios.acsp.backend.service;

import com.akjostudios.acsp.backend.data.entity.bot.log.command.CommandExecution;
import com.akjostudios.acsp.backend.data.entity.bot.log.command.CommandResponse;
import com.akjostudios.acsp.backend.data.repository.bot.log.command.CommandExecutionRepository;
import com.akjostudios.acsp.backend.data.repository.bot.log.command.CommandResponseRepository;
import com.akjostudios.acsp.common.dto.bot.log.command.CommandExecutionCreateRequest;
import com.akjostudios.acsp.common.dto.bot.log.command.CommandResponseCreateRequest;
import com.akjostudios.acsp.common.model.ResponseStatus;
import com.akjostudios.acsp.common.model.bot.log.command.CommandExecutionDao;
import com.github.tonivade.purefun.type.Validation;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@SuppressWarnings("java:S1192")
public class CommandExecutionService {
    private final CommandExecutionRepository executionRepository;
    private final CommandResponseRepository responseRepository;

    public long logExecution(@NotNull CommandExecutionCreateRequest request) {
        CommandExecution execution = new CommandExecution();
        execution.setMessageId(request.messageId());
        execution.setChannelId(request.channelId());
        execution.setUserId(request.userId());
        execution.setCommandName(request.commandName());
        execution.setCommandArgs(request.commandArgs());
        execution.setSubcommandName(request.subcommandName());
        execution.setFinished(false);
        execution.setFinishedAt(null);

        execution = executionRepository.save(execution);
        return execution.getExecutionId();
    }

    public Validation<String, ResponseStatus> finishExecution(long executionId) {
        CommandExecution execution = executionRepository.findById(executionId).orElse(null);
        if (execution == null) {
            return Validation.invalid("Execution with ID " + executionId + " not found!");
        }

        execution.setFinished(true);
        execution.setFinishedAt(Instant.now());

        executionRepository.save(execution);
        return Validation.valid(ResponseStatus.SUCCESS);
    }

    public Validation<String, Long> logResponse(
            long executionId,
            @NotNull CommandResponseCreateRequest request
    ) {
        CommandExecution execution = executionRepository.findById(executionId).orElse(null);
        if (execution == null) {
            return Validation.invalid("Execution with ID " + executionId + " not found!");
        }

        CommandResponse response = new CommandResponse();
        response.setExecution(execution);
        response.setMessageId(request.messageId());
        response.setChannelId(request.channelId());

        response = responseRepository.save(response);
        return Validation.valid(response.getResponseId());
    }

    public Validation<String, CommandExecutionDao> getByExecutionId(long executionId) {
        CommandExecution execution = executionRepository.findById(executionId).orElse(null);
        if (execution == null) {
            return Validation.invalid("Execution with ID " + executionId + " not found!");
        }

        return Validation.valid(execution.toDao());
    }

    public Validation<String, CommandExecutionDao> getByResponseMessageId(long responseMessageId) {
        CommandResponse response = responseRepository.findFirstByMessageId(responseMessageId).orElse(null);
        if (response == null) {
            return Validation.invalid("Response with message ID " + responseMessageId + " not found!");
        }

        return Validation.valid(response.getExecution().toDao());
    }
}