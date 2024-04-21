package com.akjostudios.acsp.backend.controller.bot.log;

import com.akjostudios.acsp.backend.data.repository.bot.log.batch.BatchExecutionRepository;
import com.akjostudios.acsp.backend.data.repository.bot.log.batch.BatchInteractionRepository;
import com.akjostudios.acsp.backend.data.repository.bot.log.batch.BatchResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bot/log/batch")
@Secured({"SERVICE_BOT"})
@RequiredArgsConstructor
public class BatchExecutionController {
    private final BatchExecutionRepository executionRepository;
    private final BatchResponseRepository responseRepository;
    private final BatchInteractionRepository interactionRepository;
}