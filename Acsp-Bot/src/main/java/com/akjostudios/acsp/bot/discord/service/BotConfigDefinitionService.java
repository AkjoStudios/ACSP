package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.discord.config.BotLocalizationConfig;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessage;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessageEmbed;
import com.akjostudios.acsp.bot.discord.config.definition.BotDefinitionProperties;
import com.github.tonivade.purefun.type.Option;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class BotConfigDefinitionService {
    private final BotDefinitionProperties properties;
    private final BotStringsService botStringsService;

    public String getCommandPrefix() {
        return properties.getCommandPrefix();
    }

    public @NotNull Option<BotConfigMessage> getMessageDefinition(
            @NotNull String label,
            String@NotNull... placeholders
    ) {
        return getMessageDefinition(label, Option.none(), placeholders);
    }

    public @NotNull Option<BotConfigMessage> getMessageDefinition(
            @NotNull String label,
            @NotNull Option<Locale> locale,
            String@NotNull... placeholders
    ) {
        return Option.from(properties.getMessages().stream()
                .filter(wrapper -> wrapper.getLabel().equals(label))
                .filter(wrapper -> wrapper.getLocale().equals(locale.getOrElse(BotLocalizationConfig.DEFAULT_LOCALE)))
                .map(BotConfigMessage.Wrapper::getMessage).findFirst()
        ).map(message -> {
            BotConfigMessage result = new BotConfigMessage();
            result.setContent(botStringsService.replace(message.getContent(), placeholders).getOrElse(""));
            result.setEmbeds(new ArrayList<>());
            for (BotConfigMessageEmbed embed : message.getEmbeds()) {
                BotConfigMessageEmbed resultEmbed = new BotConfigMessageEmbed();
                if (embed.getAuthor() != null) {
                    BotConfigMessageEmbed.Author author = new BotConfigMessageEmbed.Author();
                    author.setName(botStringsService.replace(embed.getAuthor().getName(), placeholders).getOrElse(""));
                    author.setUrl(botStringsService.replace(embed.getAuthor().getUrl(), placeholders).getOrElseNull());
                    author.setIconUrl(botStringsService.replace(embed.getAuthor().getIconUrl(), placeholders).getOrElseNull());
                    resultEmbed.setAuthor(author);
                }
                resultEmbed.setTitle(botStringsService.replace(embed.getTitle(), placeholders).getOrElse(""));
                resultEmbed.setDescription(botStringsService.replace(embed.getDescription(), placeholders).getOrElseNull());
                resultEmbed.setUrl(botStringsService.replace(embed.getUrl(), placeholders).getOrElseNull());
                resultEmbed.setColor(embed.getColor());
                resultEmbed.setFields(new ArrayList<>());
                if (embed.getFields() != null) {
                    for (BotConfigMessageEmbed.Field field : embed.getFields()) {
                        BotConfigMessageEmbed.Field resultField = new BotConfigMessageEmbed.Field();
                        resultField.setName(botStringsService.replace(field.getName(), placeholders).getOrElse(""));
                        resultField.setValue(botStringsService.replace(field.getValue(), placeholders).getOrElse(""));
                        resultField.setInline(field.isInline());
                        resultEmbed.getFields().add(resultField);
                    }
                }
                resultEmbed.setImageUrl(botStringsService.replace(embed.getImageUrl(), placeholders).getOrElseNull());
                resultEmbed.setThumbnailUrl(botStringsService.replace(embed.getThumbnailUrl(), placeholders).getOrElseNull());
                if (embed.getFooter() != null) {
                    BotConfigMessageEmbed.Footer footer = new BotConfigMessageEmbed.Footer();
                    footer.setText(botStringsService.replace(embed.getFooter().getText(), placeholders).getOrElse(""));
                    footer.setTimestamp(botStringsService.replace(embed.getFooter().getTimestamp(), placeholders).getOrElseNull());
                    footer.setIconUrl(botStringsService.replace(embed.getFooter().getIconUrl(), placeholders).getOrElseNull());
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
                .filter(wrapper -> wrapper.getLocale().equals(locale.getOrElse(BotLocalizationConfig.DEFAULT_LOCALE)))
                .map(BotConfigMessageEmbed.Field.Wrapper::getField).findFirst()
        ).map(field -> {
            BotConfigMessageEmbed.Field result = new BotConfigMessageEmbed.Field();
            result.setName(botStringsService.replace(field.getName(), placeholders).getOrElse(""));
            result.setValue(botStringsService.replace(field.getValue(), placeholders).getOrElse(""));
            result.setInline(field.isInline());
            return result;
        });
    }
}