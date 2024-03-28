package com.akjostudios.acsp.bot.discord.config.definition;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@Validated
@ToString
public class BotConfigMessage {
    private String content;
    private List<BotConfigMessageEmbed> embeds;

    @Getter
    @Setter
    @Validated
    @ToString
    public static class Wrapper {
        @NotBlank private String label;
        private BotConfigMessage message;
    }
}