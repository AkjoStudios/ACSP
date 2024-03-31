package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommand;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessage;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessageEmbed;
import com.akjostudios.acsp.bot.discord.config.definition.BotDefinitionProperties;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
        return getMessageDefinition(label, Option.none(), List.of(placeholders), placeholders);
    }

    public @NotNull Try<BotConfigMessage> getMessageDefinition(
            @NotNull String label,
            @NotNull Option<Locale> locale,
            String@NotNull... placeholders
    ) {
        return getMessageDefinition(label, locale, List.of(placeholders), placeholders);
    }

    public @NotNull Try<BotConfigMessage> getMessageDefinition(
            @NotNull String label,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    ) {
        return getMessageDefinition(label, Option.none(), labelPlaceholders, placeholders);
    }

    @SuppressWarnings("java:S3776")
    public @NotNull Try<BotConfigMessage> getMessageDefinition(
            @NotNull String label,
            @NotNull Option<Locale> locale,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    ) {
        return Try.of(() ->
                properties.getMessages().stream()
                        .filter(wrapper -> wrapper.getLabel().equals(label))
                        .map(BotConfigMessage.Wrapper::getMessage).findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("No message definition found for label: " + label))
        ).map(message -> {
            BotConfigMessage result = new BotConfigMessage();
            result.setContent(botStringsService.getString(message.getContent(), locale, labelPlaceholders, placeholders).getOrElse(""));
            result.setEmbeds(new ArrayList<>());
            if (message.getEmbeds() != null) {
                for (BotConfigMessageEmbed embed : message.getEmbeds()) {
                    BotConfigMessageEmbed resultEmbed = new BotConfigMessageEmbed();
                    if (embed.getAuthor() != null) {
                        BotConfigMessageEmbed.Author author = new BotConfigMessageEmbed.Author();
                        author.setName(botStringsService.getString(embed.getAuthor().getName(), locale, labelPlaceholders, placeholders).getOrElse(""));
                        author.setUrl(botStringsService.getString(embed.getAuthor().getUrl(), locale, labelPlaceholders, placeholders).getOrElseNull());
                        author.setIconUrl(botStringsService.getString(embed.getAuthor().getIconUrl(), locale, labelPlaceholders, placeholders).getOrElseNull());
                        resultEmbed.setAuthor(author);
                    }
                    resultEmbed.setTitle(botStringsService.getString(embed.getTitle(), locale, labelPlaceholders, placeholders).getOrElse(""));
                    resultEmbed.setDescription(botStringsService.getString(embed.getDescription(), locale, labelPlaceholders, placeholders).getOrElseNull());
                    resultEmbed.setUrl(botStringsService.getString(embed.getUrl(), locale, labelPlaceholders, placeholders).getOrElseNull());
                    resultEmbed.setColor(embed.getColor());
                    resultEmbed.setFields(new ArrayList<>());
                    if (embed.getFields() != null) {
                        for (BotConfigMessageEmbed.Field field : embed.getFields()) {
                            BotConfigMessageEmbed.Field resultField = new BotConfigMessageEmbed.Field();
                            resultField.setName(botStringsService.getString(field.getName(), locale, labelPlaceholders, placeholders).getOrElse(""));
                            resultField.setValue(botStringsService.getString(field.getValue(), locale, labelPlaceholders, placeholders).getOrElse(""));
                            resultField.setInline(field.isInline());
                            resultEmbed.getFields().add(resultField);
                        }
                    }
                    resultEmbed.setImageUrl(botStringsService.getString(embed.getImageUrl(), locale, labelPlaceholders, placeholders).getOrElseNull());
                    resultEmbed.setThumbnailUrl(botStringsService.getString(embed.getThumbnailUrl(), locale, labelPlaceholders, placeholders).getOrElseNull());
                    if (embed.getFooter() != null) {
                        BotConfigMessageEmbed.Footer footer = new BotConfigMessageEmbed.Footer();
                        footer.setText(botStringsService.getString(embed.getFooter().getText(), locale, labelPlaceholders, placeholders).getOrElse(""));
                        footer.setTimestamp(botStringsService.getString(embed.getFooter().getTimestamp(), locale, labelPlaceholders, placeholders).getOrElseNull());
                        footer.setIconUrl(botStringsService.getString(embed.getFooter().getIconUrl(), locale, labelPlaceholders, placeholders).getOrElseNull());
                        resultEmbed.setFooter(footer);
                    }
                    result.getEmbeds().add(resultEmbed);
                }
            }

            return result;
        });
    }

    public @NotNull Option<BotConfigMessageEmbed.Field> getFieldDefinition(
            @NotNull String label,
            String@NotNull... placeholders
    ) {
        return getFieldDefinition(label, Option.none(), List.of(placeholders), placeholders);
    }

    public @NotNull Option<BotConfigMessageEmbed.Field> getFieldDefinition(
            @NotNull String label,
            @NotNull Option<Locale> locale,
            String@NotNull... placeholders
    ) {
        return getFieldDefinition(label, locale, List.of(placeholders), placeholders);
    }

    public @NotNull Option<BotConfigMessageEmbed.Field> getFieldDefinition(
            @NotNull String label,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    ) {
        return getFieldDefinition(label, Option.none(), labelPlaceholders, placeholders);
    }

    public @NotNull Option<BotConfigMessageEmbed.Field> getFieldDefinition(
            @NotNull String label,
            @NotNull Option<Locale> locale,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    ) {
        return Option.from(properties.getFields().stream()
                .filter(wrapper -> wrapper.getLabel().equals(label))
                .map(BotConfigMessageEmbed.Field.Wrapper::getField).findFirst()
        ).map(field -> {
            BotConfigMessageEmbed.Field result = new BotConfigMessageEmbed.Field();
            result.setName(botStringsService.getString(field.getName(), locale, labelPlaceholders, placeholders).getOrElse(""));
            result.setValue(botStringsService.getString(field.getValue(), locale, labelPlaceholders, placeholders).getOrElse(""));
            result.setInline(field.isInline());
            return result;
        });
    }

    public @NotNull Option<BotConfigCommand> getCommandDefinition(
            @NotNull String name,
            String@NotNull... placeholders
    ) {
        return getCommandDefinition(name, Option.none(), List.of(placeholders), placeholders);
    }

    public @NotNull Option<BotConfigCommand> getCommandDefinition(
            @NotNull String name,
            @NotNull Option<Locale> locale,
            String@NotNull... placeholders
    ) {
        return getCommandDefinition(name, locale, List.of(placeholders), placeholders);
    }

    public @NotNull Option<BotConfigCommand> getCommandDefinition(
            @NotNull String name,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    ) {
        return getCommandDefinition(name, Option.none(), labelPlaceholders, placeholders);
    }

    @SuppressWarnings({"DuplicatedCode", "java:S3776"})
    public @NotNull Option<BotConfigCommand> getCommandDefinition(
            @NotNull String name,
            @NotNull Option<Locale> locale,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    ) {
        return Option.from(properties.getCommands().stream()
                .filter(command -> command.getName().equals(name))
                .findFirst()
        ).map(command -> {
            BotConfigCommand result = new BotConfigCommand();
            result.setName(command.getName());
            result.setEnabled(command.isEnabled());
            result.setDescription(botStringsService.getString(command.getDescription(), locale, labelPlaceholders, placeholders).getOrElse(""));
            BotConfigCommand.Subcommands resultSubcommands = new BotConfigCommand.Subcommands();
            resultSubcommands.setAvailable(command.getSubcommands().isAvailable());
            resultSubcommands.setRequired(command.getSubcommands().isRequired());
            if (command.getSubcommands().getCommands() != null) {
                resultSubcommands.setCommands(new ArrayList<>());
                for (BotConfigCommand.Subcommand subcommand : command.getSubcommands().getCommands()) {
                    BotConfigCommand.Subcommand resultSubcommand = new BotConfigCommand.Subcommand();
                    resultSubcommand.setName(subcommand.getName());
                    resultSubcommand.setEnabled(subcommand.isEnabled());
                    resultSubcommand.setDescription(botStringsService.getString(subcommand.getDescription(), locale, labelPlaceholders, placeholders).getOrElse(""));
                    if (subcommand.getArguments() != null) {
                        resultSubcommand.setArguments(new ArrayList<>());
                        for (BotConfigCommand.Argument argument : subcommand.getArguments()) {
                            BotConfigCommand.Argument resultArgument = new BotConfigCommand.Argument();
                            resultArgument.setId(argument.getId());
                            resultArgument.setType(argument.getType());
                            resultArgument.setRequired(argument.isRequired());
                            resultArgument.setDescription(botStringsService.getString(argument.getDescription(), locale, labelPlaceholders, placeholders).getOrElse(""));
                            resultArgument.setValidation(argument.getValidation());
                            resultSubcommand.getArguments().add(resultArgument);
                        }
                    }
                    resultSubcommand.setPermissions(subcommand.getPermissions());
                    resultSubcommands.getCommands().add(resultSubcommand);
                }
            }
            result.setSubcommands(resultSubcommands);
            if (command.getArguments() != null) {
                result.setArguments(new ArrayList<>());
                for (BotConfigCommand.Argument argument : command.getArguments()) {
                    BotConfigCommand.Argument resultArgument = new BotConfigCommand.Argument();
                    resultArgument.setId(argument.getId());
                    resultArgument.setType(argument.getType());
                    resultArgument.setRequired(argument.isRequired());
                    resultArgument.setDescription(botStringsService.getString(argument.getDescription(), locale, labelPlaceholders, placeholders).getOrElse(""));
                    resultArgument.setValidation(argument.getValidation());
                    result.getArguments().add(resultArgument);
                }
            }
            result.setPermissions(command.getPermissions());
            return result;
        });
    }
}