package com.akjostudios.acsp.backend.controller.bot.log;

import com.akjostudios.acsp.backend.service.CommandExecutionService;
import com.akjostudios.acsp.common.dto.bot.log.command.*;
import com.akjostudios.acsp.common.model.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bot/log/command/execution")
@Secured({"SERVICE_BOT"})
@RequiredArgsConstructor
public class CommandExecutionController {
    private final CommandExecutionService service;

    @PostMapping
    public ResponseEntity<CommandExecutionCreateResponse> logExecution(
            @RequestBody CommandExecutionCreateRequest request
    ) {
        return ResponseEntity.ok(new CommandExecutionCreateResponse(
                service.logExecution(request),
                ResponseStatus.SUCCESS, null
        ));
    }

    @PutMapping("/{executionId}/finish")
    public ResponseEntity<CommandExecutionFinishResponse> finishExecution(
            @PathVariable(value = "executionId") long executionId
    ) {
        return service.finishExecution(executionId).fold(
                error -> ResponseEntity.badRequest().body(new CommandExecutionFinishResponse(
                        ResponseStatus.FAILURE, error
                )), status -> ResponseEntity.ok(new CommandExecutionFinishResponse(
                        status, null
                ))
        );
    }

    @PostMapping("/{executionId}/response")
    public ResponseEntity<CommandResponseCreateResponse> logResponse(
            @PathVariable(value = "executionId") long executionId,
            @RequestBody CommandResponseCreateRequest request
    ) {
        return service.logResponse(executionId, request).fold(
                error -> ResponseEntity.badRequest().body(new CommandResponseCreateResponse(
                        -1, ResponseStatus.FAILURE, error
                )), status -> ResponseEntity.ok(new CommandResponseCreateResponse(
                        status, ResponseStatus.SUCCESS, null
                ))
        );
    }

    @GetMapping("/exid/{executionId}")
    public ResponseEntity<CommandExecutionGetResponse> getByExecutionId(
            @PathVariable(value = "executionId") long executionId
    ) {
        return service.getByExecutionId(executionId).fold(
                error -> ResponseEntity.badRequest().body(new CommandExecutionGetResponse(
                        null, ResponseStatus.FAILURE, error
                )), response -> ResponseEntity.ok(new CommandExecutionGetResponse(
                        response, ResponseStatus.SUCCESS, null
                ))
        );
    }

    @GetMapping("/rmid/{responseMessageId}")
    public ResponseEntity<CommandExecutionGetResponse> getByResponseMessageId(
            @PathVariable(value = "responseMessageId") long responseMessageId
    ) {
        return service.getByResponseMessageId(responseMessageId).fold(
                error -> ResponseEntity.badRequest().body(new CommandExecutionGetResponse(
                        null, ResponseStatus.FAILURE, error
                )), response -> ResponseEntity.ok(new CommandExecutionGetResponse(
                        response, ResponseStatus.SUCCESS, null
                ))
        );
    }
}