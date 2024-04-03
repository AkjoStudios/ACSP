package com.akjostudios.acsp.bot.discord.common.command.argument.conversion;

import com.akjostudios.acsp.bot.AcspBotApp;
import com.akjostudios.acsp.bot.discord.common.command.BotCommandContext;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommandArgumentType;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
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
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static class TextChannelConverter implements BotCommandArgumentConverter<TextChannel> {
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
                            BotConfigCommandArgumentType.TEXT_CHANNEL,
                            ctx.getJumpUrl()
                    ));
        }
    }

    public static class VoiceChannelConverter implements BotCommandArgumentConverter<VoiceChannel> {
        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, VoiceChannel> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return ctx.getVoiceChannel(value).toTry()
                    .toValidation(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.VOICE_CHANNEL,
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
            return Try.of(() -> ZonedDateTime.parse(value, AcspBotApp.DATE_TIME_FORMATTER))
                    .map(ChronoZonedDateTime::toInstant)
                    .toValidation(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.TIME,
                            ctx.getJumpUrl()
                    ));
        }
    }

    public static class DurationConverter implements BotCommandArgumentConverter<Duration> {
        private static final Pattern DURATION_PATTERN = Pattern.compile(
                "(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?"
        );

        @Override
        public @NotNull Validation<BotCommandArgumentConversionError, Duration> convert(
                @NotNull BotCommandContext ctx,
                @NotNull String name,
                @NotNull String value
        ) {
            return parseDuration(value).toTry()
                    .toValidation(throwable -> new BotCommandArgumentConversionError(
                            name,
                            value,
                            BotConfigCommandArgumentType.DURATION,
                            ctx.getJumpUrl()
                    ));
        }

        private static Option<Duration> parseDuration(String input) {
            Matcher matcher = DURATION_PATTERN.matcher(input);
            if (!matcher.matches()) { return Option.none(); }

            long days = parseNumber(matcher.group(1));
            long hours = parseNumber(matcher.group(2));
            long minutes = parseNumber(matcher.group(3));
            long seconds = parseNumber(matcher.group(4));

            return Option.some(Duration.ofDays(days)
                    .plusHours(hours)
                    .plusMinutes(minutes)
                    .plusSeconds(seconds));
        }

        private static long parseNumber(String number) {
            return (number != null && !number.isEmpty()) ? Long.parseLong(number) : 0;
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
            case TEXT_CHANNEL -> BotCommandArgumentConverterProvider.TEXT_CHANNEL;
            case VOICE_CHANNEL -> BotCommandArgumentConverterProvider.VOICE_CHANNEL;
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