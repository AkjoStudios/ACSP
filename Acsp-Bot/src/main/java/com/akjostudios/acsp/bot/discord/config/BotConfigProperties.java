package com.akjostudios.acsp.bot.discord.config;

import com.akjostudios.acsp.bot.discord.common.BotEnvironment;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServer;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerEnvironment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@ConfigurationProperties(prefix = "app.config")
@Getter
@Setter
@ToString
@Validated
public class BotConfigProperties {
    @NotNull private String defaultLocale;

    @NotNull private BotEnvironment environment;
    @NotNull private String deploymentId;

    @NotBlank private String clientId;
    @NotBlank private String clientSecret;
    @NotBlank private String botToken;

    @NotNull private Map<@NotNull BotConfigServerEnvironment, @NotNull BotConfigServer> layout;
}