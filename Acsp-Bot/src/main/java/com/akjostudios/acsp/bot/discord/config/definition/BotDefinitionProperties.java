package com.akjostudios.acsp.bot.discord.config.definition;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties(prefix = "app.config.definition")
@Getter
@Setter
@ToString
@Validated
public class BotDefinitionProperties {
    @NotBlank private String commandPrefix;
    private List<BotConfigMessage.Wrapper> messages;
    private List<BotConfigMessageEmbed.Field.Wrapper> fields;
}