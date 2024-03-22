package com.akjostudios.acsp.bot.discord.internal;

import com.akjostudios.acsp.bot.discord.common.BotEnvironment;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.config")
@Getter
@Setter
public class BotConfigProperties {
    private BotEnvironment environment;
    private String clientId;
    private String clientSecret;
    private String botToken;
}