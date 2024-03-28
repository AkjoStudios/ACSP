package com.akjostudios.acsp.bot.discord.config.definition;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Locale;

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
        @NotNull private Locale locale;
        private BotConfigMessage message;
    }
}