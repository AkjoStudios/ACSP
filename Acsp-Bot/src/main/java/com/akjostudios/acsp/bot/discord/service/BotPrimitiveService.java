package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannel;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerRole;
import com.github.tonivade.purefun.type.Option;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
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

    public Long getChannelId(
            @NotNull BotConfigServerChannel channel
    ) { return botLayoutService.getServerLayout().flatMap(layout -> botLayoutService.getChannelId(layout, channel)).getOrElse(-1L); }

    public Option<Long> getChannelIdOption(
            @NotNull BotConfigServerChannel channel
    ) { return botLayoutService.getServerLayout().flatMap(layout -> botLayoutService.getChannelId(layout, channel)); }

    public @NotNull Option<TextChannel> getChannel(
            @NotNull JDA botInstance,
            @NotNull BotConfigServerChannel channel
    ) { return getChannelIdOption(channel).map(botInstance::getTextChannelById); }

    public @NotNull Option<TextChannel> getChannel(
            @NotNull Event event,
            @NotNull BotConfigServerChannel channel
    ) { return getChannel(event.getJDA(), channel); }

    public Long getRoleId(
            @NotNull BotConfigServerRole role
    ) { return botLayoutService.getServerLayout().flatMap(layout -> botLayoutService.getRoleId(layout, role)).getOrElse(-1L); }

    public Option<Long> getRoleIdOption(
            @NotNull BotConfigServerRole role
    ) { return botLayoutService.getServerLayout().flatMap(layout -> botLayoutService.getRoleId(layout, role)); }

    public @NotNull Option<Role> getRole(
            @NotNull JDA botInstance,
            @NotNull BotConfigServerRole role
    ) { return getRoleIdOption(role).map(botInstance::getRoleById); }

    public @NotNull Option<Role> getRole(
            @NotNull Event event,
            @NotNull BotConfigServerRole role
    ) { return getRole(event.getJDA(), role); }

    public boolean memberHasRole(
            @NotNull Member member,
            @NotNull BotConfigServerRole role
    ) {
        return getRole(member.getJDA(), role)
                .map(roleP -> member.getRoles().contains(roleP))
                .getOrElse(false);
    }
}