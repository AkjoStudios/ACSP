package com.akjostudios.acsp.backend;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@RequiredArgsConstructor
@Getter
@Slf4j
public class AcspBackendApp {
    public static void main(String[] args) {
        SpringApplication.run(AcspBackendApp.class, args);
    }
}