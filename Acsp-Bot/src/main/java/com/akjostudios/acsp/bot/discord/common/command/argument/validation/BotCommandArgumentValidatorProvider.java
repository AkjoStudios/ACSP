package com.akjostudios.acsp.bot.discord.common.command.argument.validation;

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
public interface BotCommandArgumentValidatorProvider<T, V extends BotCommandArgumentValidator<T>> {
    @NotNull BotCommandArgumentValidatorProvider<String, BotCommandArgumentValidators.StringValidator> STRING = BotCommandArgumentValidators.StringValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<Byte, BotCommandArgumentValidators.ByteValidator> BYTE = BotCommandArgumentValidators.ByteValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<Short, BotCommandArgumentValidators.ShortValidator> SHORT = BotCommandArgumentValidators.ShortValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<Integer, BotCommandArgumentValidators.IntegerValidator> INTEGER = BotCommandArgumentValidators.IntegerValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<Long, BotCommandArgumentValidators.LongValidator> LONG = BotCommandArgumentValidators.LongValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<Float, BotCommandArgumentValidators.FloatValidator> FLOAT = BotCommandArgumentValidators.FloatValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<Double, BotCommandArgumentValidators.DoubleValidator> DOUBLE = BotCommandArgumentValidators.DoubleValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<Boolean, BotCommandArgumentValidators.BooleanValidator> BOOLEAN = BotCommandArgumentValidators.BooleanValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<User, BotCommandArgumentValidators.UserValidator> USER = BotCommandArgumentValidators.UserValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<Member, BotCommandArgumentValidators.MemberValidator> MEMBER = BotCommandArgumentValidators.MemberValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<Role, BotCommandArgumentValidators.RoleValidator> ROLE = BotCommandArgumentValidators.RoleValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<TextChannel, BotCommandArgumentValidators.TextChannelValidator> TEXT_CHANNEL = BotCommandArgumentValidators.TextChannelValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<VoiceChannel, BotCommandArgumentValidators.VoiceChannelValidator> VOICE_CHANNEL = BotCommandArgumentValidators.VoiceChannelValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<Category, BotCommandArgumentValidators.CategoryValidator> CATEGORY = BotCommandArgumentValidators.CategoryValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<Emoji, BotCommandArgumentValidators.EmojiValidator> EMOJI = BotCommandArgumentValidators.EmojiValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<Instant, BotCommandArgumentValidators.TimeValidator> TIME = BotCommandArgumentValidators.TimeValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<Duration, BotCommandArgumentValidators.DurationValidator> DURATION = BotCommandArgumentValidators.DurationValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<URI, BotCommandArgumentValidators.UrlValidator> URL = BotCommandArgumentValidators.UrlValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<Color, BotCommandArgumentValidators.ColorValidator> COLOR = BotCommandArgumentValidators.ColorValidator::new;
    @NotNull BotCommandArgumentValidatorProvider<Object, BotCommandArgumentValidators.UnknownValidator> UNKNOWN = BotCommandArgumentValidators.UnknownValidator::new;

    @NotNull V provide();
}
