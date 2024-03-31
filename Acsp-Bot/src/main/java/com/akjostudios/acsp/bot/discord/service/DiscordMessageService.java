package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.AcspBotApp;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessage;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessageEmbed;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class DiscordMessageService {
    public @NotNull MessageCreateData createMessage(String message) {
        return new MessageCreateBuilder().setContent(message).build();
    }

    public @NotNull MessageCreateData createMessage(BotConfigMessage message) {
        MessageCreateBuilder builder = new MessageCreateBuilder();

        if (message == null) {
            return builder.build();
        }

        builder.setContent(message.getContent());
        builder.setEmbeds(message.getEmbeds().stream().map(this::toMessageEmbed).toList());

        return builder.build();
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
}