package com.akjostudios.acsp.backend.controller.bot.log;

import com.akjostudios.acsp.backend.data.entity.bot.log.command.CommandExecution;
import com.akjostudios.acsp.backend.data.repository.bot.log.command.CommandExecutionRepository;
import com.akjostudios.acsp.common.dto.bot.log.command.CommandExecutionGetAllResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bot/log/command")
@Secured({"SERVICE_BOT"})
@RequiredArgsConstructor
public class CommandExecutionController {
    private final CommandExecutionRepository executionRepository;

    @GetMapping
    public ResponseEntity<CommandExecutionGetAllResponse> getAll() {
        return ResponseEntity.ok(new CommandExecutionGetAllResponse(
                executionRepository.findAll().stream()
                        .map(CommandExecution::toDao)
                        .toList()
        ));
    }
}