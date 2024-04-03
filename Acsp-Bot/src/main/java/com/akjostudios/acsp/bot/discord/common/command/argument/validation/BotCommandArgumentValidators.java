package com.akjostudios.acsp.bot.discord.common.command.argument.validation;

import com.akjostudios.acsp.bot.discord.common.command.BotCommandContext;
import com.akjostudios.acsp.bot.discord.common.command.argument.BotCommandArgument;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommandArgumentType;
import com.github.tonivade.purefun.type.Validation;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public final class BotCommandArgumentValidators {
    @Contract(pure = true)
    private BotCommandArgumentValidators() {}

    public static class StringValidator implements BotCommandArgumentValidator<String> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<String>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<String> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class ByteValidator implements BotCommandArgumentValidator<Byte> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Byte>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Byte> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class ShortValidator implements BotCommandArgumentValidator<Short> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Short>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Short> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class IntegerValidator implements BotCommandArgumentValidator<Integer> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Integer>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Integer> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class LongValidator implements BotCommandArgumentValidator<Long> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Long>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Long> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class FloatValidator implements BotCommandArgumentValidator<Float> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Float>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Float> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class DoubleValidator implements BotCommandArgumentValidator<Double> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Double>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Double> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class BooleanValidator implements BotCommandArgumentValidator<Boolean> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Boolean>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Boolean> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class UserValidator implements BotCommandArgumentValidator<User> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<User>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<User> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class MemberValidator implements BotCommandArgumentValidator<Member> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Member>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Member> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class RoleValidator implements BotCommandArgumentValidator<Role> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Role>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Role> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class TextChannelValidator implements BotCommandArgumentValidator<TextChannel> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<TextChannel>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<TextChannel> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class VoiceChannelValidator implements BotCommandArgumentValidator<VoiceChannel> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<VoiceChannel>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<VoiceChannel> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class CategoryValidator implements BotCommandArgumentValidator<Category> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Category>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Category> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class EmojiValidator implements BotCommandArgumentValidator<Emoji> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Emoji>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Emoji> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class TimeValidator implements BotCommandArgumentValidator<Instant> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Instant>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Instant> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class DurationValidator implements BotCommandArgumentValidator<Duration> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Duration>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Duration> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class UrlValidator implements BotCommandArgumentValidator<URI> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<URI>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<URI> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class ColorValidator implements BotCommandArgumentValidator<Color> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Color>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Color> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    public static class UnknownValidator implements BotCommandArgumentValidator<Object> {
        @Override
        public @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Object>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Object> value,
                @NotNull Map<String, Object> validation
        ) {
            return Validation.valid(value);
        }
    }

    @SuppressWarnings({"java:S1452", "unchecked"})
    public static <T> @NotNull BotCommandArgumentValidator<T> from(
            @NotNull BotConfigCommandArgumentType type
    ) {
        return (BotCommandArgumentValidator<T>) (switch (type) {
            case STRING -> BotCommandArgumentValidatorProvider.STRING;
            case BYTE -> BotCommandArgumentValidatorProvider.BYTE;
            case SHORT -> BotCommandArgumentValidatorProvider.SHORT;
            case INTEGER -> BotCommandArgumentValidatorProvider.INTEGER;
            case LONG -> BotCommandArgumentValidatorProvider.LONG;
            case FLOAT -> BotCommandArgumentValidatorProvider.FLOAT;
            case DOUBLE -> BotCommandArgumentValidatorProvider.DOUBLE;
            case BOOLEAN -> BotCommandArgumentValidatorProvider.BOOLEAN;
            case USER -> BotCommandArgumentValidatorProvider.USER;
            case MEMBER -> BotCommandArgumentValidatorProvider.MEMBER;
            case ROLE -> BotCommandArgumentValidatorProvider.ROLE;
            case TEXT_CHANNEL -> BotCommandArgumentValidatorProvider.TEXT_CHANNEL;
            case VOICE_CHANNEL -> BotCommandArgumentValidatorProvider.VOICE_CHANNEL;
            case CATEGORY -> BotCommandArgumentValidatorProvider.CATEGORY;
            case EMOJI -> BotCommandArgumentValidatorProvider.EMOJI;
            case TIME -> BotCommandArgumentValidatorProvider.TIME;
            case DURATION -> BotCommandArgumentValidatorProvider.DURATION;
            case URL -> BotCommandArgumentValidatorProvider.URL;
            case COLOR -> BotCommandArgumentValidatorProvider.COLOR;
            case UNKNOWN -> BotCommandArgumentValidatorProvider.UNKNOWN;
        }).provide();
    }
}