package com.akjostudios.acsp.bot.discord.common.command.argument.conversion;

import com.akjostudios.acsp.bot.discord.common.command.BotCommandContext;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommandArgumentType;
import com.akjostudios.acsp.bot.util.DurationUtils;
import com.github.tonivade.purefun.type.Try;
import com.github.tonivade.purefun.type.Validation;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;

public final class BotCommandArgumentConverters {
    @Contract(pure = true)
    private BotCommandArgumentConverters() {}

    public static class StringConverter implements BotCommandArgumentConverter<String> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, String> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return Validation.valid(value);
        }
    }

    public static class ByteConverter implements BotCommandArgumentConverter<Byte> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, Byte> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return Try.of(() -> Byte.parseByte(value))
                    .toEither()
                    .mapLeft(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.BYTE,
                            ctx.getJumpUrl()
                    )).toValidation();
        }
    }

    public static class ShortConverter implements BotCommandArgumentConverter<Short> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, Short> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return Try.of(() -> Short.parseShort(value))
                    .toEither()
                    .mapLeft(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.SHORT,
                            ctx.getJumpUrl()
                    )).toValidation();
        }
    }

    public static class IntegerConverter implements BotCommandArgumentConverter<Integer> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, Integer> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return Try.of(() -> Integer.parseInt(value))
                    .toEither()
                    .mapLeft(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.INTEGER,
                            ctx.getJumpUrl()
                    )).toValidation();
        }
    }

    public static class LongConverter implements BotCommandArgumentConverter<Long> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, Long> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return Try.of(() -> Long.parseLong(value))
                    .toEither()
                    .mapLeft(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.LONG,
                            ctx.getJumpUrl()
                    )).toValidation();
        }
    }

    public static class FloatConverter implements BotCommandArgumentConverter<Float> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, Float> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return Try.of(() -> Float.parseFloat(value))
                    .toEither()
                    .mapLeft(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.FLOAT,
                            ctx.getJumpUrl()
                    )).toValidation();
        }
    }

    public static class DoubleConverter implements BotCommandArgumentConverter<Double> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, Double> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return Try.of(() -> Double.parseDouble(value))
                    .toEither()
                    .mapLeft(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.DOUBLE,
                            ctx.getJumpUrl()
                    )).toValidation();
        }
    }

    public static class BooleanConverter implements BotCommandArgumentConverter<Boolean> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, Boolean> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return Try.of(() -> Boolean.parseBoolean(value))
                    .toEither()
                    .mapLeft(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.BOOLEAN,
                            ctx.getJumpUrl()
                    )).toValidation();
        }
    }

    public static class UserConverter implements BotCommandArgumentConverter<User> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, User> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return ctx.getUser(value).toTry()
                    .toValidation(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.USER,
                            ctx.getJumpUrl()
                    ));
        }
    }

    public static class MemberConverter implements BotCommandArgumentConverter<Member> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, Member> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return ctx.getMember(value).toTry()
                    .toValidation(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.MEMBER,
                            ctx.getJumpUrl()
                    ));
        }
    }

    public static class RoleConverter implements BotCommandArgumentConverter<Role> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, Role> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return ctx.getRole(value).toTry()
                    .toValidation(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.ROLE,
                            ctx.getJumpUrl()
                    ));
        }
    }

    public static class ChannelConverter implements BotCommandArgumentConverter<TextChannel> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, TextChannel> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return ctx.getTextChannel(value).toTry()
                    .toValidation(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.CHANNEL,
                            ctx.getJumpUrl()
                    ));
        }
    }

    public static class CategoryConverter implements BotCommandArgumentConverter<Category> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, Category> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return ctx.getCategory(value).toTry()
                    .toValidation(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.CATEGORY,
                            ctx.getJumpUrl()
                    ));
        }
    }

    public static class EmojiConverter implements BotCommandArgumentConverter<Emoji> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, Emoji> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return ctx.getEmoji(value).toTry()
                    .toValidation(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.EMOJI,
                            ctx.getJumpUrl()
                    ));
        }
    }

    public static class TimeConverter implements BotCommandArgumentConverter<Instant> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, Instant> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return Try.of(() -> Instant.ofEpochMilli(Long.parseLong(value)))
                    .toValidation(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.TIME,
                            ctx.getJumpUrl()
                    ));
        }
    }

    public static class DurationConverter implements BotCommandArgumentConverter<Duration> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, Duration> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return DurationUtils.parse(value).toTry()
                    .toValidation(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.DURATION,
                            ctx.getJumpUrl()
                    ));
        }
    }

    public static class UrlConverter implements BotCommandArgumentConverter<URI> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, URI> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return Try.of(() -> URI.create(value))
                    .toValidation(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.URL,
                            ctx.getJumpUrl()
                    ));
        }
    }

    public static class ColorConverter implements BotCommandArgumentConverter<Color> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, Color> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return Try.of(() -> Color.decode(value))
                    .toValidation(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.COLOR,
                            ctx.getJumpUrl()
                    ));
        }
    }

    public static class UnknownConverter implements BotCommandArgumentConverter<String> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, String> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return Validation.invalid(new BotCommandArgumentConversionError(
                    name,
                    value,
                    BotConfigCommandArgumentType.UNKNOWN,
                    ctx.getJumpUrl()
            ));
        }
    }

    @SuppressWarnings({"java:S1452", "unchecked"})
    public static <T> @NotNull BotCommandArgumentConverter<T> from(
            @NotNull BotConfigCommandArgumentType type
    ) {
        return (BotCommandArgumentConverter<T>) (switch (type) {
            case STRING -> BotCommandArgumentConverterProvider.STRING;
            case BYTE -> BotCommandArgumentConverterProvider.BYTE;
            case SHORT -> BotCommandArgumentConverterProvider.SHORT;
            case INTEGER -> BotCommandArgumentConverterProvider.INTEGER;
            case LONG -> BotCommandArgumentConverterProvider.LONG;
            case FLOAT -> BotCommandArgumentConverterProvider.FLOAT;
            case DOUBLE -> BotCommandArgumentConverterProvider.DOUBLE;
            case BOOLEAN -> BotCommandArgumentConverterProvider.BOOLEAN;
            case USER -> BotCommandArgumentConverterProvider.USER;
            case MEMBER -> BotCommandArgumentConverterProvider.MEMBER;
            case ROLE -> BotCommandArgumentConverterProvider.ROLE;
            case CHANNEL -> BotCommandArgumentConverterProvider.TEXT_CHANNEL;
            case CATEGORY -> BotCommandArgumentConverterProvider.CATEGORY;
            case EMOJI -> BotCommandArgumentConverterProvider.EMOJI;
            case TIME -> BotCommandArgumentConverterProvider.TIME;
            case DURATION -> BotCommandArgumentConverterProvider.DURATION;
            case URL -> BotCommandArgumentConverterProvider.URL;
            case COLOR -> BotCommandArgumentConverterProvider.COLOR;
            case UNKNOWN -> BotCommandArgumentConverterProvider.UNKNOWN;
        }).provide();
    }
}