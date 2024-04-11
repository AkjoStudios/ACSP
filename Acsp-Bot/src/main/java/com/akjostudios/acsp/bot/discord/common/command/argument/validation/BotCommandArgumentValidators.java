package com.akjostudios.acsp.bot.discord.common.command.argument.validation;

import com.akjostudios.acsp.bot.AcspBotApp;
import com.akjostudios.acsp.bot.discord.common.command.BotCommandContext;
import com.akjostudios.acsp.bot.discord.common.command.argument.BotCommandArgument;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommand;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommandArgumentType;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannel;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannelCategory;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerRole;
import com.akjostudios.acsp.bot.util.DurationUtils;
import com.github.tonivade.purefun.type.Try;
import com.github.tonivade.purefun.type.Validation;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.akjostudios.acsp.bot.discord.common.command.argument.validation.BotCommandArgumentValidationArguments.*;

@SuppressWarnings("java:S1192")
public final class BotCommandArgumentValidators {
    @Contract(pure = true)
    private BotCommandArgumentValidators() {}

    public static class StringValidator implements BotCommandArgumentValidator<String> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<String>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<String> argument,
                @NotNull Map<String, Object> validation
        ) {
            Validation<BotCommandArgumentValidationError, StringValidationArguments> argsValidation = getArgs(
                    ctx, argument.id(), validation, StringValidationArguments.class
            );
            if (argsValidation.isInvalid()) { return Validation.invalid(List.of(argsValidation.getError())); }

            return aggregate(List.of(
                    validateMinLength(argument, argsValidation.get().getMinLength()),
                    validateMaxLength(argument, argsValidation.get().getMaxLength()),
                    validateRegex(
                            argument, argument.value(), argsValidation.get().getRegex(),
                            "$error.command_argument_validation_report.reason.string.regex$"
                    )
            ), () -> argument);
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<String>> validateMinLength(
                @NotNull BotCommandArgument<String> argument,
                int minLength
        ) {
            return argument.value().length() >= minLength
                    ? Validation.valid(argument)
                    : Validation.invalid(new BotCommandArgumentValidationError(
                            argument.id(),
                            "$error.command_argument_validation_report.reason.string.min_length$",
                            List.of(String.valueOf(minLength), String.valueOf(argument.value().length()))
                    ));
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<String>> validateMaxLength(
                @NotNull BotCommandArgument<String> argument,
                int maxLength
        ) {
            return argument.value().length() <= maxLength
                    ? Validation.valid(argument)
                    : Validation.invalid(new BotCommandArgumentValidationError(
                            argument.id(),
                            "$error.command_argument_validation_report.reason.string.max_length$",
                            List.of(String.valueOf(maxLength), String.valueOf(argument.value().length()))
                    ));
        }
    }

    public static class ByteValidator implements BotCommandArgumentValidator<Byte> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<Byte>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Byte> argument,
                @NotNull Map<String, Object> validation
        ) {
            Validation<BotCommandArgumentValidationError, ByteValidationArguments> argsValidation = getArgs(
                    ctx, argument.id(), validation, ByteValidationArguments.class
            );
            if (argsValidation.isInvalid()) { return Validation.invalid(List.of(argsValidation.getError())); }

            return validateNumber(argument, argsValidation.get().getMinValue(), argsValidation.get().getMaxValue());
        }
    }

    public static class ShortValidator implements BotCommandArgumentValidator<Short> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<Short>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Short> argument,
                @NotNull Map<String, Object> validation
        ) {
            Validation<BotCommandArgumentValidationError, ShortValidationArguments> argsValidation = getArgs(
                    ctx, argument.id(), validation, ShortValidationArguments.class
            );
            if (argsValidation.isInvalid()) { return Validation.invalid(List.of(argsValidation.getError())); }

            return validateNumber(argument, argsValidation.get().getMinValue(), argsValidation.get().getMaxValue());
        }
    }

    public static class IntegerValidator implements BotCommandArgumentValidator<Integer> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<Integer>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Integer> argument,
                @NotNull Map<String, Object> validation
        ) {
            Validation<BotCommandArgumentValidationError, IntegerValidationArguments> argsValidation = getArgs(
                    ctx, argument.id(), validation, IntegerValidationArguments.class
            );
            if (argsValidation.isInvalid()) { return Validation.invalid(List.of(argsValidation.getError())); }

            return validateNumber(argument, argsValidation.get().getMinValue(), argsValidation.get().getMaxValue());
        }
    }

    public static class LongValidator implements BotCommandArgumentValidator<Long> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<Long>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Long> argument,
                @NotNull Map<String, Object> validation
        ) {
            Validation<BotCommandArgumentValidationError, LongValidationArguments> argsValidation = getArgs(
                    ctx, argument.id(), validation, LongValidationArguments.class
            );
            if (argsValidation.isInvalid()) { return Validation.invalid(List.of(argsValidation.getError())); }

            return validateNumber(argument, argsValidation.get().getMinValue(), argsValidation.get().getMaxValue());
        }
    }

    public static class FloatValidator implements BotCommandArgumentValidator<Float> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<Float>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Float> argument,
                @NotNull Map<String, Object> validation
        ) {
            Validation<BotCommandArgumentValidationError, FloatValidationArguments> argsValidation = getArgs(
                    ctx, argument.id(), validation, FloatValidationArguments.class
            );
            if (argsValidation.isInvalid()) { return Validation.invalid(List.of(argsValidation.getError())); }

            return validateNumber(argument, argsValidation.get().getMinValue(), argsValidation.get().getMaxValue());
        }
    }

    public static class DoubleValidator implements BotCommandArgumentValidator<Double> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<Double>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Double> argument,
                @NotNull Map<String, Object> validation
        ) {
            Validation<BotCommandArgumentValidationError, DoubleValidationArguments> argsValidation = getArgs(
                    ctx, argument.id(), validation, DoubleValidationArguments.class
            );
            if (argsValidation.isInvalid()) { return Validation.invalid(List.of(argsValidation.getError())); }

            return validateNumber(argument, argsValidation.get().getMinValue(), argsValidation.get().getMaxValue());
        }
    }

    public static class BooleanValidator implements BotCommandArgumentValidator<Boolean> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<Boolean>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Boolean> argument,
                @NotNull Map<String, Object> validation
        ) {
            return aggregate(List.of(), () -> argument);
        }
    }

    public static class UserValidator implements BotCommandArgumentValidator<User> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<User>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<User> argument,
                @NotNull Map<String, Object> validation
        ) {
            Validation<BotCommandArgumentValidationError, UserValidationArguments> argsValidation = getArgs(
                    ctx, argument.id(), validation, UserValidationArguments.class
            );
            if (argsValidation.isInvalid()) { return Validation.invalid(List.of(argsValidation.getError())); }

            return aggregate(List.of(
                    validateBot(argument, argsValidation.get().isAllowBot()),
                    validateMinAge(argument, argsValidation.get().getMinAge())
            ), () -> argument);
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<User>> validateBot(
                @NotNull BotCommandArgument<User> argument,
                boolean allowsBots
        ) {
            return allowsBots || !argument.value().isBot()
                    ? Validation.valid(argument)
                    : Validation.invalid(new BotCommandArgumentValidationError(
                            argument.id(),
                            "$error.command_argument_validation_report.reason.user.bot$",
                            List.of(
                                    argument.value().getName()
                            )
                    ));
        }
    }

    public static class MemberValidator implements BotCommandArgumentValidator<Member> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<Member>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Member> argument,
                @NotNull Map<String, Object> validation
        ) {
            Validation<BotCommandArgumentValidationError, MemberValidationArguments> argsValidation = getArgs(
                    ctx, argument.id(), validation, MemberValidationArguments.class
            );
            if (argsValidation.isInvalid()) { return Validation.invalid(List.of(argsValidation.getError())); }

            return aggregate(List.of(
                    validateRoles(ctx, argument, argsValidation.get().getRequiredRoles()),
                    validateStatus(argument, argsValidation.get().getRequiredStatus()),
                    validateBot(argument, argsValidation.get().isAllowBot()),
                    validateMinAge(argument, argsValidation.get().getMinAge())
            ), () -> argument);
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Member>> validateRoles(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Member> argument,
                @Nullable Map<Integer, BotConfigCommand.RolePermission> roles
        ) {
            Map<BotConfigServerRole, Boolean> roleValidation = roles == null
                    ? Map.of(BotConfigServerRole.EVERYONE, true)
                    : ctx.memberMatchesRoles(argument.value(), roles.values());
            return roleValidation.values().stream().allMatch(Boolean::booleanValue)
                    ? Validation.valid(argument)
                    : Validation.invalid(new BotCommandArgumentValidationError(
                            argument.id(),
                            "$error.command_argument_validation_report.reason.member.roles$",
                            List.of(
                                    roleValidation.keySet().stream()
                                            .map(BotConfigServerRole::name)
                                            .collect(Collectors.joining(", ", "[", "]")),
                                    argument.value().getEffectiveName(),
                                    roleValidation.entrySet().stream()
                                            .filter(entry -> !entry.getValue())
                                            .map(entry -> entry.getKey().name())
                                            .collect(Collectors.joining(", ", "[", "]"))
                            )
                    ));
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Member>> validateStatus(
                @NotNull BotCommandArgument<Member> argument,
                @Nullable Map<Integer, String> requiredStatus
        ) {
            if (requiredStatus == null) { return Validation.valid(argument); }
            return requiredStatus.values().stream()
                    .map(OnlineStatus::fromKey)
                    .toList()
                    .contains(argument.value().getOnlineStatus())
                    ? Validation.valid(argument)
                    : Validation.invalid(new BotCommandArgumentValidationError(
                            argument.id(),
                            "$error.command_argument_validation_report.reason.member.status$",
                            List.of(
                                    requiredStatus.values().stream()
                                            .map(OnlineStatus::fromKey)
                                            .map(OnlineStatus::name)
                                            .collect(Collectors.joining(", ", "[", "]")),
                                    argument.value().getEffectiveName(),
                                    argument.value().getOnlineStatus().name()
                            )
                    ));
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Member>> validateBot(
                @NotNull BotCommandArgument<Member> argument,
                boolean allowsBots
        ) {
            return allowsBots || !argument.value().getUser().isBot()
                    ? Validation.valid(argument)
                    : Validation.invalid(new BotCommandArgumentValidationError(
                    argument.id(),
                    "$error.command_argument_validation_report.reason.user.bot$",
                    List.of(
                            argument.value().getEffectiveName()
                    )
            ));
        }
    }

    public static class RoleValidator implements BotCommandArgumentValidator<Role> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<Role>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Role> argument,
                @NotNull Map<String, Object> validation
        ) {
            Validation<BotCommandArgumentValidationError, RoleValidationArguments> argsValidation = getArgs(
                    ctx, argument.id(), validation, RoleValidationArguments.class
            );
            if (argsValidation.isInvalid()) { return Validation.invalid(List.of(argsValidation.getError())); }

            return aggregate(List.of(
                    checkAllowed(ctx, argument, argsValidation.get().getAllowedRoles()),
                    checkMentionable(argument, argsValidation.get().isMentionableRequired()),
                    checkAuthorHasRole(ctx, argument, argsValidation.get().isAuthorMustHaveRole()),
                    validateMinAge(argument, argsValidation.get().getMinAge())
            ), () -> argument);
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Role>> checkAllowed(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Role> argument,
                @Nullable Map<Integer, BotConfigServerRole> allowedRoles
        ) {
            BotCommandArgumentValidationError error = new BotCommandArgumentValidationError(
                    argument.id(),
                    "$error.command_argument_validation_report.reason.role.allowed$",
                    List.of(
                            allowedRoles == null ? "[any]" : allowedRoles.values().stream()
                                    .map(BotConfigServerRole::name)
                                    .collect(Collectors.joining(", ", "[", "]")),
                            ctx.getServerRole(argument.value().getIdLong())
                                    .map(BotConfigServerRole::name)
                                    .getOrElse("unknown")
                    )
            );
            return ctx.getServerRole(argument.value().getIdLong())
                    .map(serverRole -> allowedRoles == null || allowedRoles.containsValue(serverRole))
                    .toTry().toValidation(ex -> error)
                    .fold(Validation::invalid, valid -> valid ? Validation.valid(argument) : Validation.invalid(error));
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Role>> checkMentionable(
                @NotNull BotCommandArgument<Role> argument,
                boolean mentionableRequired
        ) {
            return mentionableRequired && !argument.value().isMentionable()
                    ? Validation.invalid(new BotCommandArgumentValidationError(
                            argument.id(),
                            "$error.command_argument_validation_report.reason.role.mentionable$",
                            List.of()
                    )) : Validation.valid(argument);
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Role>> checkAuthorHasRole(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Role> argument,
                boolean authorMustHaveRole
        ) {
            return authorMustHaveRole && !ctx.getOriginalAuthor().getRoles().contains(argument.value())
                    ? Validation.invalid(new BotCommandArgumentValidationError(
                            argument.id(),
                            "$error.command_argument_validation_report.reason.role.author_has_role$",
                            List.of()
                    )) : Validation.valid(argument);
        }
    }

    public static class ChannelValidator implements BotCommandArgumentValidator<TextChannel> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<TextChannel>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<TextChannel> argument,
                @NotNull Map<String, Object> validation
        ) {
            Validation<BotCommandArgumentValidationError, ChannelValidationArguments> argsValidation = getArgs(
                    ctx, argument.id(), validation, ChannelValidationArguments.class
            );
            if (argsValidation.isInvalid()) { return Validation.invalid(List.of(argsValidation.getError())); }

            return aggregate(List.of(
                    checkAllowed(ctx, argument, argsValidation.get().getAllowedChannels()),
                    checkAuthorInChannel(ctx, argument, argsValidation.get().isAuthorMustBeInChannel()),
                    validateMinAge(argument, argsValidation.get().getMinAge())
            ), () -> argument);
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<TextChannel>> checkAllowed(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<TextChannel> argument,
                @Nullable Map<Integer, BotConfigServerChannel> allowedChannels
        ) {
            BotCommandArgumentValidationError error = new BotCommandArgumentValidationError(
                    argument.id(),
                    "$error.command_argument_validation_report.reason.channel.allowed$",
                    List.of(
                            allowedChannels == null ? "[any]" : allowedChannels.values().stream()
                                    .map(BotConfigServerChannel::name)
                                    .collect(Collectors.joining(", ", "[", "]")),
                            ctx.getServerChannel(argument.value().getIdLong())
                                    .map(BotConfigServerChannel::name)
                                    .getOrElse("unknown")
                    )
            );
            return ctx.getServerChannel(argument.value().getIdLong())
                    .map(serverChannel -> allowedChannels == null || allowedChannels.containsValue(serverChannel))
                    .toTry().toValidation(ex -> error)
                    .fold(Validation::invalid, valid -> valid ? Validation.valid(argument) : Validation.invalid(error));
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<TextChannel>> checkAuthorInChannel(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<TextChannel> argument,
                boolean authorMustBeInChannel
        ) {
            if (!authorMustBeInChannel) { return Validation.valid(argument); }

            BotCommandArgumentValidationError error = new BotCommandArgumentValidationError(
                    argument.id(),
                    "$error.command_argument_validation_report.reason.channel.author_in_channel$",
                    List.of()
            );
            return ctx.getServerChannel(argument.value().getIdLong())
                    .map(serverChannel -> ctx.memberInChannel(ctx.getOriginalAuthor(), serverChannel))
                    .toTry().toValidation(ex -> error)
                    .fold(Validation::invalid, valid -> valid ? Validation.valid(argument) : Validation.invalid(error));
        }
    }

    public static class CategoryValidator implements BotCommandArgumentValidator<Category> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<Category>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Category> argument,
                @NotNull Map<String, Object> validation
        ) {
            Validation<BotCommandArgumentValidationError, CategoryValidationArguments> argsValidation = getArgs(
                    ctx, argument.id(), validation, CategoryValidationArguments.class
            );
            if (argsValidation.isInvalid()) { return Validation.invalid(List.of(argsValidation.getError())); }

            return aggregate(List.of(
                    checkAllowed(ctx, argument, argsValidation.get().getAllowedCategories()),
                    validateMinAge(argument, argsValidation.get().getMinAge())
            ), () -> argument);
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Category>> checkAllowed(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Category> argument,
                @Nullable Map<Integer, BotConfigServerChannelCategory> allowedCategories
        ) {
            BotCommandArgumentValidationError error = new BotCommandArgumentValidationError(
                    argument.id(),
                    "$error.command_argument_validation_report.reason.category.allowed$",
                    List.of(
                            allowedCategories == null ? "[any]" : allowedCategories.values().stream()
                                    .map(BotConfigServerChannelCategory::name)
                                    .collect(Collectors.joining(", ", "[", "]")),
                            ctx.getServerCategory(argument.value().getIdLong())
                                    .map(BotConfigServerChannelCategory::name)
                                    .getOrElse("unknown")
                    )
            );
            return ctx.getServerCategory(argument.value().getIdLong())
                    .map(serverCategory -> allowedCategories == null || allowedCategories.containsValue(serverCategory))
                    .toTry().toValidation(ex -> error)
                    .fold(Validation::invalid, valid -> valid ? Validation.valid(argument) : Validation.invalid(error));
        }
    }

    public static class EmojiValidator implements BotCommandArgumentValidator<Emoji> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<Emoji>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Emoji> argument,
                @NotNull Map<String, Object> validation
        ) {
            return aggregate(List.of(), () -> argument);
        }
    }

    public static class TimeValidator implements BotCommandArgumentValidator<Instant> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<Instant>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Instant> argument,
                @NotNull Map<String, Object> validation
        ) {
            Validation<BotCommandArgumentValidationError, TimeValidationArguments> argsValidation = getArgs(
                    ctx, argument.id(), validation, TimeValidationArguments.class
            );
            if (argsValidation.isInvalid()) { return Validation.invalid(List.of(argsValidation.getError())); }

            return aggregate(List.of(
                    validateRelative(argument, argsValidation.get().getIsRelative()),
                    validateBefore(argument, argsValidation.get().getIsBefore()),
                    validateAfter(argument, argsValidation.get().getIsAfter())
            ), () -> argument);
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Instant>> validateRelative(
                @NotNull BotCommandArgument<Instant> argument,
                @NotNull TimeValidationArguments.RelativeType relativeType
        ) {
            return switch (relativeType) {
                case PAST -> argument.value().isBefore(Instant.now())
                        ? Validation.valid(argument)
                        : Validation.invalid(new BotCommandArgumentValidationError(
                                argument.id(),
                                "$error.command_argument_validation_report.reason.time.past$",
                                List.of(AcspBotApp.DATE_TIME_FORMATTER.format(argument.value()))
                        ));
                case FUTURE -> argument.value().isAfter(Instant.now())
                        ? Validation.valid(argument)
                        : Validation.invalid(new BotCommandArgumentValidationError(
                                argument.id(),
                                "$error.command_argument_validation_report.reason.time.future$",
                                List.of()
                        ));
                case ANY -> Validation.valid(argument);
            };
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Instant>> validateBefore(
                @NotNull BotCommandArgument<Instant> argument,
                long before
        ) {
            BotCommandArgumentValidationError error = new BotCommandArgumentValidationError(
                    argument.id(),
                    "$error.command_argument_validation_report.reason.time.before$",
                    List.of(
                            AcspBotApp.DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(before)),
                            AcspBotApp.DATE_TIME_FORMATTER.format(argument.value())
                    )
            );
            return Try.of(() -> Instant.ofEpochMilli(before))
                    .map(instant -> argument.value().isBefore(instant))
                    .toValidation(ex -> error)
                    .fold(Validation::invalid, valid -> valid ? Validation.valid(argument) : Validation.invalid(error));
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Instant>> validateAfter(
                @NotNull BotCommandArgument<Instant> argument,
                long after
        ) {
            BotCommandArgumentValidationError error = new BotCommandArgumentValidationError(
                    argument.id(),
                    "$error.command_argument_validation_report.reason.time.after$",
                    List.of(
                            AcspBotApp.DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(after)),
                            AcspBotApp.DATE_TIME_FORMATTER.format(argument.value())
                    )
            );
            return Try.of(() -> Instant.ofEpochMilli(after))
                    .map(instant -> argument.value().isAfter(instant))
                    .toValidation(ex -> error)
                    .fold(Validation::invalid, valid -> valid ? Validation.valid(argument) : Validation.invalid(error));
        }
    }

    public static class DurationValidator implements BotCommandArgumentValidator<Duration> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<Duration>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Duration> argument,
                @NotNull Map<String, Object> validation
        ) {
            Validation<BotCommandArgumentValidationError, DurationValidationArguments> argsValidation = getArgs(
                    ctx, argument.id(), validation, DurationValidationArguments.class
            );
            if (argsValidation.isInvalid()) { return Validation.invalid(List.of(argsValidation.getError())); }

            return aggregate(List.of(
                    validateMinDuration(argument, argsValidation.get().getMinDuration()),
                    validateMaxDuration(argument, argsValidation.get().getMaxDuration())
            ), () -> argument);
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Duration>> validateMinDuration(
                @NotNull BotCommandArgument<Duration> argument,
                @NotNull String minDuration
        ) {
            BotCommandArgumentValidationError error = new BotCommandArgumentValidationError(
                    argument.id(),
                    "$error.command_argument_validation_report.reason.duration.min$",
                    List.of(
                            minDuration,
                            DurationUtils.format(argument.value())
                    )
            );
            return DurationUtils.parse(minDuration).toTry()
                    .map(duration -> argument.value().compareTo(duration) >= 0)
                    .toValidation(ex -> error)
                    .fold(Validation::invalid, valid -> valid ? Validation.valid(argument) : Validation.invalid(error));
        }

        private @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<Duration>> validateMaxDuration(
                @NotNull BotCommandArgument<Duration> argument,
                @NotNull String maxDuration
        ) {
            BotCommandArgumentValidationError error = new BotCommandArgumentValidationError(
                    argument.id(),
                    "$error.command_argument_validation_report.reason.duration.max$",
                    List.of(
                            maxDuration,
                            DurationUtils.format(argument.value())
                    )
            );
            return DurationUtils.parse(maxDuration).toTry()
                    .map(duration -> argument.value().compareTo(duration) <= 0)
                    .toValidation(ex -> error)
                    .fold(Validation::invalid, valid -> valid ? Validation.valid(argument) : Validation.invalid(error));
        }
    }

    public static class UrlValidator implements BotCommandArgumentValidator<URI> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<URI>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<URI> argument,
                @NotNull Map<String, Object> validation
        ) {
            Validation<BotCommandArgumentValidationError, UrlValidationArguments> argsValidation = getArgs(
                    ctx, argument.id(), validation, UrlValidationArguments.class
            );
            if (argsValidation.isInvalid()) { return Validation.invalid(List.of(argsValidation.getError())); }

            return aggregate(List.of(
                    validateRegex(
                            argument, argument.value().getScheme(), argsValidation.get().getSchemeRegex(),
                            "$error.command_argument_validation_report.reason.url.scheme$"
                    ),
                    validateRegex(
                            argument, argument.value().getHost(), argsValidation.get().getHostRegex(),
                            "$error.command_argument_validation_report.reason.url.host$"
                    ),
                    validateRegex(
                            argument, argument.value().getPath(), argsValidation.get().getPathRegex(),
                            "$error.command_argument_validation_report.reason.url.path$"
                    ),
                    validateRegex(
                            argument, argument.value().getQuery(), argsValidation.get().getQueryRegex(),
                            "$error.command_argument_validation_report.reason.url.query$"
                    )
            ), () -> argument);
        }
    }

    public static class ColorValidator implements BotCommandArgumentValidator<Color> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<Color>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Color> argument,
                @NotNull Map<String, Object> validation
        ) {
            return aggregate(List.of(), () -> argument);
        }
    }

    public static class UnknownValidator implements BotCommandArgumentValidator<Object> {
        @Override
        public @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<Object>> validate(
                @NotNull BotCommandContext ctx,
                @NotNull BotCommandArgument<Object> argument,
                @NotNull Map<String, Object> validation
        ) {
            return aggregate(List.of(), () -> argument);
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
            case CHANNEL -> BotCommandArgumentValidatorProvider.CHANNEL;
            case CATEGORY -> BotCommandArgumentValidatorProvider.CATEGORY;
            case EMOJI -> BotCommandArgumentValidatorProvider.EMOJI;
            case TIME -> BotCommandArgumentValidatorProvider.TIME;
            case DURATION -> BotCommandArgumentValidatorProvider.DURATION;
            case URL -> BotCommandArgumentValidatorProvider.URL;
            case COLOR -> BotCommandArgumentValidatorProvider.COLOR;
            case UNKNOWN -> BotCommandArgumentValidatorProvider.UNKNOWN;
        }).provide();
    }

    private static <T> @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<T>> validateRegex(
            @NotNull BotCommandArgument<T> argument,
            @NotNull String value,
            @NotNull String regex,
            @NotNull String errorKey
    ) {
        return value.matches(regex)
                ? Validation.valid(argument)
                : Validation.invalid(new BotCommandArgumentValidationError(
                argument.id(),
                errorKey,
                List.of(regex)
        ));
    }

    private static <T extends Number & Comparable<T>> @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<T>> validateNumber(
            @NotNull BotCommandArgument<T> argument,
            @NotNull T minValue,
            @NotNull T maxValue
    ) {
        return aggregate(List.of(
            validateMinValue(argument, minValue),
            validateMaxValue(argument, maxValue)
        ), () -> argument);
    }

    private static <T extends Number & Comparable<T>> @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<T>> validateMinValue(
            @NotNull BotCommandArgument<T> argument,
            T minValue
    ) {
        return argument.value().compareTo(minValue) >= 0
                ? Validation.valid(argument)
                : Validation.invalid(new BotCommandArgumentValidationError(
                        argument.id(),
                        "$error.command_argument_validation_report.reason.number.min_value$",
                        List.of(String.valueOf(minValue), String.valueOf(argument.value()))
                ));
    }

    private static <T extends Number & Comparable<T>> @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<T>> validateMaxValue(
            @NotNull BotCommandArgument<T> argument,
            T maxValue
    ) {
        return argument.value().compareTo(maxValue) <= 0
                ? Validation.valid(argument)
                : Validation.invalid(new BotCommandArgumentValidationError(
                        argument.id(),
                        "$error.command_argument_validation_report.reason.number.max_value$",
                        List.of(String.valueOf(maxValue), String.valueOf(argument.value()))
                ));
    }

    private static <T extends ISnowflake> @NotNull Validation<BotCommandArgumentValidationError, BotCommandArgument<T>> validateMinAge(
            @NotNull BotCommandArgument<T> argument,
            @Nullable String minAge
    ) {
        if (minAge == null) { return Validation.valid(argument); }

        BotCommandArgumentValidationError error = new BotCommandArgumentValidationError(
                argument.id(),
                "$error.command_argument_validation_report.reason.primitive.min_age$",
                List.of(
                        minAge,
                        DurationUtils.format(Duration.between(
                                argument.value().getTimeCreated().toInstant(),
                                Instant.now()
                        ))
                )
        );
        return DurationUtils.parse(minAge).toTry()
                .map(duration -> argument.value().getTimeCreated().toInstant().isBefore(Instant.now().minus(duration)))
                .toValidation(ex -> error)
                .fold(Validation::invalid, valid -> valid ? Validation.valid(argument) : Validation.invalid(error));
    }

    public static <T> @NotNull Validation<BotCommandArgumentValidationError, T> getArgs(
            @NotNull BotCommandContext ctx,
            @NotNull String id,
            @NotNull Map<String, Object> validation,
            @NotNull Class<T> type
    ) {
        return Try.of(() -> ctx.getMapper().convertValue(validation, type))
                .toValidation(ex -> new BotCommandArgumentValidationError(
                        id, "$error.command_argument_validation_report.reason.generic$", List.of()
                ));
    }

    public static <T> @NotNull Validation<List<BotCommandArgumentValidationError>, BotCommandArgument<T>> aggregate(
            @NotNull List<Validation<BotCommandArgumentValidationError, BotCommandArgument<T>>> validations,
            @NotNull Supplier<BotCommandArgument<T>> successValue
    ) {
        List<BotCommandArgumentValidationError> errors = validations.stream()
                .filter(Validation::isInvalid)
                .map(Validation::getError)
                .toList();
        return errors.isEmpty()
                ? Validation.valid(successValue.get())
                : Validation.invalid(errors);
    }
}