package com.akjostudios.acsp.bot.discord.common.command.argument.conversion;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;

@FunctionalInterface
public interface BotCommandArgumentConverterProvider<T, C extends BotCommandArgumentConverter<T>> {
    @NotNull BotCommandArgumentConverterProvider<String, BotCommandArgumentConverters.StringConverter> STRING = BotCommandArgumentConverters.StringConverter::new;
    @NotNull BotCommandArgumentConverterProvider<Byte, BotCommandArgumentConverters.ByteConverter> BYTE = BotCommandArgumentConverters.ByteConverter::new;
    @NotNull BotCommandArgumentConverterProvider<Short, BotCommandArgumentConverters.ShortConverter> SHORT = BotCommandArgumentConverters.ShortConverter::new;
    @NotNull BotCommandArgumentConverterProvider<Integer, BotCommandArgumentConverters.IntegerConverter> INTEGER = BotCommandArgumentConverters.IntegerConverter::new;
    @NotNull BotCommandArgumentConverterProvider<Long, BotCommandArgumentConverters.LongConverter> LONG = BotCommandArgumentConverters.LongConverter::new;
    @NotNull BotCommandArgumentConverterProvider<Float, BotCommandArgumentConverters.FloatConverter> FLOAT = BotCommandArgumentConverters.FloatConverter::new;
    @NotNull BotCommandArgumentConverterProvider<Double, BotCommandArgumentConverters.DoubleConverter> DOUBLE = BotCommandArgumentConverters.DoubleConverter::new;
    @NotNull BotCommandArgumentConverterProvider<Boolean, BotCommandArgumentConverters.BooleanConverter> BOOLEAN = BotCommandArgumentConverters.BooleanConverter::new;
    @NotNull BotCommandArgumentConverterProvider<User, BotCommandArgumentConverters.UserConverter> USER = BotCommandArgumentConverters.UserConverter::new;
    @NotNull BotCommandArgumentConverterProvider<Member, BotCommandArgumentConverters.MemberConverter> MEMBER = BotCommandArgumentConverters.MemberConverter::new;
    @NotNull BotCommandArgumentConverterProvider<Role, BotCommandArgumentConverters.RoleConverter> ROLE = BotCommandArgumentConverters.RoleConverter::new;
    @NotNull BotCommandArgumentConverterProvider<TextChannel, BotCommandArgumentConverters.TextChannelConverter> TEXT_CHANNEL = BotCommandArgumentConverters.TextChannelConverter::new;
    @NotNull BotCommandArgumentConverterProvider<VoiceChannel, BotCommandArgumentConverters.VoiceChannelConverter> VOICE_CHANNEL = BotCommandArgumentConverters.VoiceChannelConverter::new;
    @NotNull BotCommandArgumentConverterProvider<Category, BotCommandArgumentConverters.CategoryConverter> CATEGORY = BotCommandArgumentConverters.CategoryConverter::new;
    @NotNull BotCommandArgumentConverterProvider<Emoji, BotCommandArgumentConverters.EmojiConverter> EMOJI = BotCommandArgumentConverters.EmojiConverter::new;
    @NotNull BotCommandArgumentConverterProvider<Instant, BotCommandArgumentConverters.TimeConverter> TIME = BotCommandArgumentConverters.TimeConverter::new;
    @NotNull BotCommandArgumentConverterProvider<Duration, BotCommandArgumentConverters.DurationConverter> DURATION = BotCommandArgumentConverters.DurationConverter::new;
    @NotNull BotCommandArgumentConverterProvider<URI, BotCommandArgumentConverters.UrlConverter> URL = BotCommandArgumentConverters.UrlConverter::new;
    @NotNull BotCommandArgumentConverterProvider<Color, BotCommandArgumentConverters.ColorConverter> COLOR = BotCommandArgumentConverters.ColorConverter::new;
    @NotNull BotCommandArgumentConverterProvider<String, BotCommandArgumentConverters.UnknownConverter> UNKNOWN = BotCommandArgumentConverters.UnknownConverter::new;

    @NotNull C provide();
}