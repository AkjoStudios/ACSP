package com.akjostudios.acsp.bot;

import com.akjostudios.acsp.bot.discord.api.AcspBot;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@SpringBootApplication(exclude={
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@ConfigurationPropertiesScan
@EnableWebFlux
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("unused")
public class AcspBotApp {
    public static final String BOT_NAME = "ACSP Bot";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    private final ApplicationContext context;
    private final AcspBot acspBot;

    public static void main(String[] args) {
        SpringApplication.run(AcspBotApp.class, args);
    }

    @PreDestroy
    public void shutdown() {
        acspBot.shutdown((ConfigurableApplicationContext) context);
    }
}