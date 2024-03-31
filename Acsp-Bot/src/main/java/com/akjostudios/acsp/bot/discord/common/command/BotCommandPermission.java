package com.akjostudios.acsp.bot.discord.common.command;

import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannel;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerRole;

import java.util.EnumSet;

public record BotCommandPermission(
        BotConfigServerChannel channel,
        EnumSet<BotConfigServerRole> roles
) {}