package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannel;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerRole;
import com.github.tonivade.purefun.type.Option;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.Event;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class BotPrimitiveService {
    private final BotLayoutService botLayoutService;

    public @NotNull Option<TextChannel> getChannel(
            @NotNull JDA botInstance,
            @NotNull BotConfigServerChannel channel
    ) {
        return botLayoutService.getServerLayout()
                .flatMap(layout -> botLayoutService.getChannelId(layout, channel))
                .map(botInstance::getTextChannelById);
    }

    public @NotNull Option<TextChannel> getChannel(
            @NotNull Event event,
            @NotNull BotConfigServerChannel channel
    ) {
        return getChannel(event.getJDA(), channel);
    }

    public @NotNull Option<Role> getRole(
            @NotNull JDA botInstance,
            @NotNull BotConfigServerRole role
    ) {
        return botLayoutService.getServerLayout()
                .flatMap(layout -> botLayoutService.getRoleId(layout, role))
                .map(botInstance::getRoleById);
    }

    public @NotNull Option<Role> getRole(
            @NotNull Event event,
            @NotNull BotConfigServerRole role
    ) {
        return getRole(event.getJDA(), role);
    }
}