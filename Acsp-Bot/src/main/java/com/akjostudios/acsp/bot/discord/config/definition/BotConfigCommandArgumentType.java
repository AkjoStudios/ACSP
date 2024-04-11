package com.akjostudios.acsp.bot.discord.config.definition;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import static com.akjostudios.acsp.bot.discord.common.command.argument.validation.BotCommandArgumentValidationArguments.*;

@Getter
@SuppressWarnings("unused")
public enum BotConfigCommandArgumentType {
    /**
     * {@link StringValidationArguments Validation}: minLength, maxLength, regex **/
    STRING,
    /** {@link ByteValidationArguments Validation}: minValue, maxValue **/
    BYTE,
    /** {@link ShortValidationArguments Validation}: minValue, maxValue **/
    SHORT,
    /** {@link IntegerValidationArguments Validation}: minValue, maxValue **/
    INTEGER,
    /** {@link LongValidationArguments Validation}: minValue, maxValue **/
    LONG,
    /** {@link FloatValidationArguments Validation}: minValue, maxValue **/
    FLOAT,
    /** {@link DoubleValidationArguments Validation}: minValue, maxValue **/
    DOUBLE,
    /** No validation arguments **/
    BOOLEAN,
    /** {@link UserValidationArguments Validation}: allowBot, minAge **/
    USER,
    /** {@link MemberValidationArguments Validation}: requiredRoles, requiredStatus, allowBot, minAge **/
    MEMBER,
    /** {@link RoleValidationArguments Validation}: allowedRoles, mentionableRequired, minAge **/
    ROLE,
    /** {@link ChannelValidationArguments Validation}: allowedChannels, minAge **/
    CHANNEL,
    /** {@link CategoryValidationArguments Validation}: allowedCategories, minAge **/
    CATEGORY,
    /** No validation arguments **/
    EMOJI,
    /** {@link TimeValidationArguments Validation}: isRelative (RelativeType), isBefore, isAfter **/
    TIME,
    /** {@link DurationValidationArguments Validation}: minDuration, maxDuration **/
    DURATION,
    /** {@link UrlValidationArguments Validation}: schemeRegex, hostRegex, pathRegex, queryRegex **/
    URL,
    /** No validation arguments **/
    COLOR,
    /** No validation arguments **/
    UNKNOWN;

    public @NotNull String getTranslationKey() {
        return "$command.arguments.type." + name().toLowerCase() + "$";
    }

    public @NotNull String getTooltipKey() {
        return "$command.arguments.type." + name().toLowerCase() + ".tooltip$";
    }
}