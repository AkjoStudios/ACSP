package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannel;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerRole;
import com.github.tonivade.purefun.type.Option;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
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

    private Guild getGuild(
            @NotNull JDA botInstance
    ) { return botLayoutService.getServerLayout().flatMap(layout -> Option.of(botInstance.getGuildById(layout.getId()))).getOrElseThrow(); }

    public Option<User> getUser(
            @NotNull JDA botInstance,
            @NotNull String userId
    ) { return Option.of(botInstance.getUserById(userId)); }

    public Option<User> getUser(
            @NotNull Event event,
            @NotNull String userId
    ) { return getUser(event.getJDA(), userId); }

    public Option<Member> getMember(
            @NotNull JDA botInstance,
            @NotNull String memberId
    ) { return Option.of(getGuild(botInstance).getMemberById(memberId)); }

    public Option<Member> getMember(
            @NotNull Event event,
            @NotNull String memberId
    ) { return getMember(event.getJDA(), memberId); }

    public Option<Role> getRole(
            @NotNull JDA botInstance,
            @NotNull String roleId
    ) { return Option.of(botInstance.getRoleById(roleId)); }

    public Option<Role> getRole(
            @NotNull Event event,
            @NotNull String roleId
    ) { return getRole(event.getJDA(), roleId); }

    public Option<TextChannel> getTextChannel(
            @NotNull JDA botInstance,
            @NotNull String channelId
    ) { return Option.of(botInstance.getTextChannelById(channelId)); }

    public Option<TextChannel> getTextChannel(
            @NotNull Event event,
            @NotNull String channelId
    ) { return getTextChannel(event.getJDA(), channelId); }

    public Option<VoiceChannel> getVoiceChannel(
            @NotNull JDA botInstance,
            @NotNull String channelId
    ) { return Option.of(botInstance.getVoiceChannelById(channelId)); }

    public Option<VoiceChannel> getVoiceChannel(
            @NotNull Event event,
            @NotNull String channelId
    ) { return getVoiceChannel(event.getJDA(), channelId); }

    public Option<Category> getCategory(
            @NotNull JDA botInstance,
            @NotNull String categoryId
    ) { return Option.of(botInstance.getCategoryById(categoryId)); }

    public Option<Category> getCategory(
            @NotNull Event event,
            @NotNull String categoryId
    ) { return getCategory(event.getJDA(), categoryId); }

    public Option<Emoji> getEmoji(
            @NotNull JDA botInstance,
            @NotNull String emojiId
    ) { return Option.of(botInstance.getEmojiById(emojiId)); }

    public Option<Emoji> getEmoji(
            @NotNull Event event,
            @NotNull String emojiId
    ) { return getEmoji(event.getJDA(), emojiId); }
}