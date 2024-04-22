package com.akjostudios.acsp.backend.controller.bot.log;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bot/log/batch")
@Secured({"SERVICE_BOT"})
@RequiredArgsConstructor
public class BatchExecutionController {}