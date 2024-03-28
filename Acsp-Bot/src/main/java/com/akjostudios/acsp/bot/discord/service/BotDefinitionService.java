package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessage;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessageEmbed;
import com.akjostudios.acsp.bot.discord.config.definition.BotDefinitionProperties;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class BotDefinitionService {
    private final BotDefinitionProperties properties;
    private final BotStringsService botStringsService;

    public String getCommandPrefix() {
        return properties.getCommandPrefix();
    }

    public @NotNull Try<BotConfigMessage> getMessageDefinition(
            @NotNull String label,
            String@NotNull... placeholders
    ) {
        return getMessageDefinition(label, Option.none(), placeholders);
    }

    public @NotNull Try<BotConfigMessage> getMessageDefinition(
            @NotNull String label,
            @NotNull Option<Locale> locale,
            String@NotNull... placeholders
    ) {
        return Try.of(() ->
                properties.getMessages().stream()
                        .filter(wrapper -> wrapper.getLabel().equals(label))
                        .map(BotConfigMessage.Wrapper::getMessage).findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No message definition found for label: " + label))
        ).map(message -> {
            BotConfigMessage result = new BotConfigMessage();
            result.setContent(botStringsService.getString(message.getContent(), locale, placeholders).getOrElse(""));
            result.setEmbeds(new ArrayList<>());
            for (BotConfigMessageEmbed embed : message.getEmbeds()) {
                BotConfigMessageEmbed resultEmbed = new BotConfigMessageEmbed();
                if (embed.getAuthor() != null) {
                    BotConfigMessageEmbed.Author author = new BotConfigMessageEmbed.Author();
                    author.setName(botStringsService.getString(embed.getAuthor().getName(), locale, placeholders).getOrElse(""));
                    author.setUrl(botStringsService.getString(embed.getAuthor().getUrl(), locale, placeholders).getOrElseNull());
                    author.setIconUrl(botStringsService.getString(embed.getAuthor().getIconUrl(), locale, placeholders).getOrElseNull());
                    resultEmbed.setAuthor(author);
                }
                resultEmbed.setTitle(botStringsService.getString(embed.getTitle(), locale, placeholders).getOrElse(""));
                resultEmbed.setDescription(botStringsService.getString(embed.getDescription(), locale, placeholders).getOrElseNull());
                resultEmbed.setUrl(botStringsService.getString(embed.getUrl(), locale, placeholders).getOrElseNull());
                resultEmbed.setColor(embed.getColor());
                resultEmbed.setFields(new ArrayList<>());
                if (embed.getFields() != null) {
                    for (BotConfigMessageEmbed.Field field : embed.getFields()) {
                        BotConfigMessageEmbed.Field resultField = new BotConfigMessageEmbed.Field();
                        resultField.setName(botStringsService.getString(field.getName(), locale, placeholders).getOrElse(""));
                        resultField.setValue(botStringsService.getString(field.getValue(), locale, placeholders).getOrElse(""));
                        resultField.setInline(field.isInline());
                        resultEmbed.getFields().add(resultField);
                    }
                }
                resultEmbed.setImageUrl(botStringsService.getString(embed.getImageUrl(), locale, placeholders).getOrElseNull());
                resultEmbed.setThumbnailUrl(botStringsService.getString(embed.getThumbnailUrl(), locale, placeholders).getOrElseNull());
                if (embed.getFooter() != null) {
                    BotConfigMessageEmbed.Footer footer = new BotConfigMessageEmbed.Footer();
                    footer.setText(botStringsService.getString(embed.getFooter().getText(), locale, placeholders).getOrElse(""));
                    footer.setTimestamp(botStringsService.getString(embed.getFooter().getTimestamp(), locale, placeholders).getOrElseNull());
                    footer.setIconUrl(botStringsService.getString(embed.getFooter().getIconUrl(), locale, placeholders).getOrElseNull());
                    resultEmbed.setFooter(footer);
                }
                result.getEmbeds().add(resultEmbed);
            }

            return result;
        });
    }

    public @NotNull Option<BotConfigMessageEmbed.Field> getFieldDefinition(
            @NotNull String label,
            String@NotNull... placeholders
    ) {
        return getFieldDefinition(label, Option.none(), placeholders);
    }

    public @NotNull Option<BotConfigMessageEmbed.Field> getFieldDefinition(
            @NotNull String label,
            @NotNull Option<Locale> locale,
            String@NotNull... placeholders
    ) {
        return Option.from(properties.getFields().stream()
                .filter(wrapper -> wrapper.getLabel().equals(label))
                .map(BotConfigMessageEmbed.Field.Wrapper::getField).findFirst()
        ).map(field -> {
            BotConfigMessageEmbed.Field result = new BotConfigMessageEmbed.Field();
            result.setName(botStringsService.getString(field.getName(), locale, placeholders).getOrElse(""));
            result.setValue(botStringsService.getString(field.getValue(), locale, placeholders).getOrElse(""));
            result.setInline(field.isInline());
            return result;
        });
    }
}