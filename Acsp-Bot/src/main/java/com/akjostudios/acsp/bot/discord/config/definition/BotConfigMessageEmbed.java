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
public class BotConfigMessageEmbed {
    private Author author;
    @NotBlank private String title;
    private String description;
    private String url;
    private String color;
    private List<Field> fields;
    private String imageUrl;
    private String thumbnailUrl;
    private Footer footer;

    @Getter
    @Setter
    @Validated
    @ToString
    public static class Author {
        @NotBlank private String name;
        private String url;
        private String iconUrl;
    }

    @Getter
    @Setter
    @Validated
    @ToString
    public static class Field {
        @NotBlank private String name;
        @NotBlank private String value;
        @NotNull private boolean inline;

        @Getter
        @Setter
        @Validated
        @ToString
        public static class Wrapper {
            @NotBlank private String label;
            @NotNull private Locale locale;
            @NotNull private Field field;
        }
    }

    @Getter
    @Setter
    @Validated
    @ToString
    public static class Footer {
        @NotBlank private String text;
        private String timestamp;
        private String iconUrl;
    }
}