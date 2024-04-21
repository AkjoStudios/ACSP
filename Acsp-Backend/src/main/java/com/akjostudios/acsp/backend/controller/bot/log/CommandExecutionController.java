package com.akjostudios.acsp.backend.controller.bot.log;

import com.akjostudios.acsp.backend.data.repository.bot.log.command.CommandExecutionRepository;
import com.akjostudios.acsp.backend.data.repository.bot.log.command.CommandInteractionRepository;
import com.akjostudios.acsp.backend.data.repository.bot.log.command.CommandResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bot/log/command")
@Secured({"SERVICE_BOT"})
@RequiredArgsConstructor
public class CommandExecutionController {
    private final CommandExecutionRepository executionRepository;
    private final CommandResponseRepository responseRepository;
    private final CommandInteractionRepository interactionRepository;
}