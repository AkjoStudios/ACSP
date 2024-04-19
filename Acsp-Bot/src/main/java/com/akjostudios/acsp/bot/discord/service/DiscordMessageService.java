package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.AcspBotApp;
import com.akjostudios.acsp.bot.discord.common.component.BotActionRowComponent;
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
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class DiscordMessageService {
    public @NotNull MessageCreateData createMessage(String message) {
        return new MessageCreateBuilder().setContent(message).build();
    }

    public @NotNull MessageCreateData createMessage(
            String message,
            @NotNull List<Option<BotActionRowComponent>> rowComponents
    ) {
        MessageCreateBuilder builder = new MessageCreateBuilder();
        builder.setContent(message);
        builder.setComponents(rowComponents.stream()
                .filter(Option::isPresent)
                .map(Option::getOrElseThrow)
                .map(this::toActionRow)
                .toList());
        return builder.build();
    }

    public @NotNull MessageCreateData createMessage(BotConfigMessage message) {
        MessageCreateBuilder builder = new MessageCreateBuilder();

        if (message == null) {
            return builder.build();
        }

        builder.setContent(message.getContent());
        builder.setEmbeds(message.getEmbeds().stream().map(this::toMessageEmbed).toList());
        if (message.getComponents() != null) {
            builder.setComponents(message.getComponents().stream().map(this::toActionRow).toList());
        }

        return builder.build();
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

    public @NotNull Try<BotConfigMessage> injectComponents(
            @NotNull Try<BotConfigMessage> message,
            List<Option<BotActionRowComponent>> rowComponents
    ) {
        return message.onSuccess(msg -> msg.getComponents().addAll(
                rowComponents.stream().filter(Option::isPresent).map(Option::getOrElseThrow).toList()
        ));
    }

    public @NotNull Option<BotActionRowComponent> createActionRow(
            @NotNull List<Option<BotComponent>> components
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