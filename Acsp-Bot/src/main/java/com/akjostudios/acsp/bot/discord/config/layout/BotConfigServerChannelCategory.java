package com.akjostudios.acsp.bot.discord.config.layout;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;

import java.util.EnumSet;

@Getter
@RequiredArgsConstructor
@SuppressWarnings("unused")
public enum BotConfigServerChannelCategory {
    WELCOME(EnumSet.of(
            BotConfigServerChannel.WELCOME,
            BotConfigServerChannel.RULES
    )), BEGIN(EnumSet.of(
            BotConfigServerChannel.BEGIN
    )), TEAM(EnumSet.of(
            BotConfigServerChannel.TEAM_CHAT,
            BotConfigServerChannel.AUDIT_LOG
    )), ADMIN(EnumSet.of(
            BotConfigServerChannel.ADMIN_CHAT
    ));

    private final EnumSet<BotConfigServerChannel> channels;

    @Contract(pure = true)
    public boolean contains(BotConfigServerChannel channel) {
        return channels.contains(channel);
    }
}