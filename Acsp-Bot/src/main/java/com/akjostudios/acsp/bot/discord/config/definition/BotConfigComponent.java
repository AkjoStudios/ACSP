package com.akjostudios.acsp.bot.discord.config.definition;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Getter
@Setter
@ToString
@Validated
public class BotConfigComponent {
    @NotNull private Type type;
    @NotNull Map<@NotBlank String, @NotNull Object> data;

    @Getter
    @Setter
    @ToString
    @Validated
    public static class Wrapper {
        @NotBlank private String label;
        @NotNull BotConfigComponent component;
    }

    @Getter
    @SuppressWarnings("unused")
    public enum Type {
        ACTION_ROW,
        BUTTON,
        SELECT_MENU
    }
}