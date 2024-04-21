package com.akjostudios.acsp.backend;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableWebFlux
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class AcspBackendApp {
    public static void main(String[] args) {
        SpringApplication.run(AcspBackendApp.class, args);
    }
}