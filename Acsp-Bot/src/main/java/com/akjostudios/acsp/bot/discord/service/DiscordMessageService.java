package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.AcspBotApp;
import com.akjostudios.acsp.bot.discord.common.component.BotActionRowComponent;
import com.akjostudios.acsp.bot.discord.common.component.BotButtonComponent;
import com.akjostudios.acsp.bot.discord.common.component.BotComponent;
import com.akjostudios.acsp.bot.discord.common.component.conversion.BotComponentConverters;
import com.akjostudios.acsp.bot.discord.common.component.conversion.discord.DiscordComponentType;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessage;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessageEmbed;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.utils.messages.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class DiscordMessageService {
    public static final String MESSAGE_SEND_ERROR = "Failed to send message!";
    public static final String MESSAGE_EDIT_ERROR = "Failed to edit message!";

    private final BotDefinitionService botDefinitionService;

    public @NotNull MessageCreateData createMessage(String message) {
        return finishMessage(new MessageCreateBuilder(), message);
    }

    public @NotNull MessageCreateData createMessage(
            String message,
            @NotNull List<Option<BotActionRowComponent>> rowComponents
    ) {
        return finishMessage(new MessageCreateBuilder(), message, rowComponents);
    }

    public @NotNull MessageCreateData createMessage(BotConfigMessage message) {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        if (message == null) {
            return builder.build();
        }

        return finishMessage(builder, message);
    }

    public @NotNull MessageEditData editMessage(String message) {
        return finishMessage(new MessageEditBuilder(), message);
    }

    public @NotNull MessageEditData editMessage(
            String message,
            @NotNull List<Option<BotActionRowComponent>> rowComponents
    ) {
        return finishMessage(new MessageEditBuilder(), message, rowComponents);
    }

    public @NotNull MessageEditData editMessage(BotConfigMessage message) {
        MessageEditBuilder builder = new MessageEditBuilder();
        if (message == null) {
            return builder.build();
        }

        return finishMessage(builder, message);
    }

    private <T extends MessageData, B extends AbstractMessageBuilder<T, B>> @NotNull T finishMessage(
            @NotNull B builder,
            String message
    ) {
        return builder.setContent(message).build();
    }

    private <T extends MessageData, B  extends AbstractMessageBuilder<T, B>> @NotNull T finishMessage(
            @NotNull B builder,
            String message,
            @NotNull List<Option<BotActionRowComponent>> rowComponents
    ) {
        return builder.setContent(message)
                .setComponents(rowComponents.stream()
                        .filter(Option::isPresent)
                        .map(Option::getOrElseThrow)
                        .map(this::toActionRow)
                        .toList()
                ).build();
    }

    private <T extends MessageData, B  extends AbstractMessageBuilder<T, B>> @NotNull T finishMessage(
            @NotNull B builder,
            @NotNull BotConfigMessage message
    ) {
        return builder.setContent(message.getContent())
                .setEmbeds(message.getEmbeds() != null
                        ? message.getEmbeds().stream().map(this::toMessageEmbed).toList()
                        : List.of())
                .setComponents(message.getComponents() != null
                        ? message.getComponents().stream().map(this::toActionRow).toList()
                        : List.of()
                ).build();
    }

    public @NotNull Try<BotConfigMessage> injectFields(
            @NotNull Try<BotConfigMessage> message,
            Option<Integer> index,
            List<Option<BotConfigMessageEmbed.Field>> fields
    ) {
        return message.onSuccess(msg -> msg.getEmbeds().get(index.getOrElse(0)).getFields().addAll(
                fields.stream().filter(Option::isPresent).map(Option::getOrElseThrow).toList()
        ));
    }

    public @NotNull Try<BotConfigMessage> injectFields(
            @NotNull BotConfigMessage message,
            Option<Integer> index,
            List<Option<BotConfigMessageEmbed.Field>> fields
    ) {
        return Try.of(() -> message).onSuccess(msg -> msg.getEmbeds().get(index.getOrElse(0)).getFields().addAll(
                fields.stream().filter(Option::isPresent).map(Option::getOrElseThrow).toList()
        ));
    }

    public @NotNull Try<BotConfigMessage> injectComponents(
            @NotNull Try<BotConfigMessage> message,
            List<Option<BotActionRowComponent>> rowComponents
    ) {
        return message.onSuccess(msg -> msg.getComponents().addAll(
                rowComponents.stream().filter(Option::isPresent).map(Option::getOrElseThrow).toList()
        ));
    }

    public @NotNull Try<BotConfigMessage> injectComponents(
            @NotNull BotConfigMessage message,
            List<Option<BotActionRowComponent>> rowComponents
    ) {
        return Try.of(() -> message).onSuccess(msg -> msg.getComponents().addAll(
                rowComponents.stream().filter(Option::isPresent).map(Option::getOrElseThrow).toList()
        ));
    }

    public @NotNull Option<BotActionRowComponent> createActionRow(
            @NotNull List<Option<? extends BotComponent>> components
    ) {
        return Try.of(() -> components.stream()
                .filter(Option::isPresent)
                .map(Option::getOrElseThrow)
                .toList())
                .map(BotActionRowComponent::new)
                .toOption()
                .filter(row -> !row.getComponents().isEmpty())
                .filter(row -> row.getComponents().size() <= 5);
    }

    public @NotNull Option<BotButtonComponent> createButton(
            @NotNull String label,
            @NotNull Option<String> emoji,
            @NotNull String url,
            boolean disabled
    ) {
        return Option.some(new BotButtonComponent(label, emoji.getOrElseNull(), url, disabled));
    }

    public @NotNull Option<BotButtonComponent> createButton(
            @NotNull String interactionId,
            @NotNull String label,
            @NotNull BotButtonComponent.Style style,
            @NotNull Option<String> emoji,
            boolean disabled
    ) {
        return Option.some(new BotButtonComponent(interactionId, label, style, emoji.getOrElseNull(), disabled));
    }

    public @NotNull List<Option<BotActionRowComponent>> createPaginationComponents(
            int page,
            boolean hasNextPage,
            boolean hasPreviousPage
    ) {
        List<Option<? extends BotComponent>> components = new ArrayList<>();
        if (hasPreviousPage) {
            components.add(botDefinitionService.getComponentDefinition("previous-page-button"));
        }
        if (hasNextPage) {
            components.add(botDefinitionService.getComponentDefinition("next-page-button"));
        }

        return components.isEmpty()
                ? List.of()
                : List.of(createActionRow(components));
    }

    private @NotNull MessageEmbed toMessageEmbed(@NotNull BotConfigMessageEmbed embed) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setAuthor(embed.getAuthor().getName(), embed.getAuthor().getUrl(), embed.getAuthor().getIconUrl())
                .setTitle(embed.getTitle(), embed.getUrl())
                .setDescription(embed.getDescription())
                .setColor(Color.decode(embed.getColor()))
                .setImage(embed.getImageUrl())
                .setThumbnail(embed.getThumbnailUrl())
                .setFooter(embed.getFooter().getText(), embed.getFooter().getIconUrl())
                .setTimestamp(embed.getFooter().getTimestamp() != null
                        ? AcspBotApp.DATE_TIME_FORMATTER.parse(
                                embed.getFooter().getTimestamp(), Instant::from
                        ) : null
                );

        embed.getFields().forEach(field -> embedBuilder.addField(
                field.getName(),
                field.getValue(),
                field.isInline()
        ));

        return embedBuilder.build();
    }

    @Contract("_ -> new")
    private @NotNull ActionRow toActionRow(@NotNull BotActionRowComponent rowComponent) {
        return ActionRow.of(rowComponent.getComponents().stream()
                .map(component -> BotComponentConverters.forDiscord(
                        DiscordComponentType.fromConfigType(component.getType())
                ).convert(component)).toList()
        );
    }
}