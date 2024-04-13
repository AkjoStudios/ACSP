package com.akjostudios.acsp.bot.discord.config.definition;

import com.akjostudios.acsp.bot.discord.common.command.argument.validation.BotCommandArgumentValidationArguments;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannel;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannelCategory;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Validated
public class BotConfigCommand {
    @NotBlank private String name;
    @NotNull private boolean enabled;
    private String description;
    private Subcommands subcommands;
    private List<Argument> arguments;
    @NotEmpty private List<PermissionDeclaration> permissions;

    @Getter
    @Setter
    @ToString
    @Validated
    public static class Subcommands {
        @NotNull private boolean available;
        @NotNull private boolean required;
        private List<Subcommand> commands;
    }

    @Getter
    @Setter
    @ToString
    @Validated
    public static class Subcommand {
        @NotBlank private String name;
        @NotNull private boolean enabled;
        private String description;
        private List<Argument> arguments;
        @NotEmpty private List<PermissionDeclaration> permissions;
    }

    @Getter
    @Setter
    @ToString
    @Validated
    public static class Argument {
        @NotBlank private String id;
        @NotNull private Type type;
        @NotNull private boolean required;
        private String description;
        private Map<@NotBlank String, @NotNull Object> validation;

        @Getter
        @SuppressWarnings("unused")
        public enum Type {
            /**
             * {@link BotCommandArgumentValidationArguments.StringValidationArguments Validation}: minLength, maxLength, regex **/
            STRING,
            /** {@link BotCommandArgumentValidationArguments.ByteValidationArguments Validation}: minValue, maxValue **/
            BYTE,
            /** {@link BotCommandArgumentValidationArguments.ShortValidationArguments Validation}: minValue, maxValue **/
            SHORT,
            /** {@link BotCommandArgumentValidationArguments.IntegerValidationArguments Validation}: minValue, maxValue **/
            INTEGER,
            /** {@link BotCommandArgumentValidationArguments.LongValidationArguments Validation}: minValue, maxValue **/
            LONG,
            /** {@link BotCommandArgumentValidationArguments.FloatValidationArguments Validation}: minValue, maxValue **/
            FLOAT,
            /** {@link BotCommandArgumentValidationArguments.DoubleValidationArguments Validation}: minValue, maxValue **/
            DOUBLE,
            /** No validation arguments **/
            BOOLEAN,
            /** {@link BotCommandArgumentValidationArguments.UserValidationArguments Validation}: allowBot, minAge **/
            USER,
            /** {@link BotCommandArgumentValidationArguments.MemberValidationArguments Validation}: requiredRoles, requiredStatus, allowBot, minAge **/
            MEMBER,
            /** {@link BotCommandArgumentValidationArguments.RoleValidationArguments Validation}: allowedRoles, mentionableRequired, minAge **/
            ROLE,
            /** {@link BotCommandArgumentValidationArguments.ChannelValidationArguments Validation}: allowedChannels, minAge **/
            CHANNEL,
            /** {@link BotCommandArgumentValidationArguments.CategoryValidationArguments Validation}: allowedCategories, minAge **/
            CATEGORY,
            /** No validation arguments **/
            EMOJI,
            /** {@link BotCommandArgumentValidationArguments.TimeValidationArguments Validation}: isRelative (RelativeType), isBefore, isAfter **/
            TIME,
            /** {@link BotCommandArgumentValidationArguments.DurationValidationArguments Validation}: minDuration, maxDuration **/
            DURATION,
            /** {@link BotCommandArgumentValidationArguments.UrlValidationArguments Validation}: schemeRegex, hostRegex, pathRegex, queryRegex **/
            URL,
            /** No validation arguments **/
            COLOR,
            /** No validation arguments **/
            UNKNOWN;

            public @org.jetbrains.annotations.NotNull String getTranslationKey() {
                return "$command.arguments.type." + name().toLowerCase() + "$";
            }

            public @org.jetbrains.annotations.NotNull String getTooltipKey() {
                return "$command.arguments.type." + name().toLowerCase() + ".tooltip$";
            }
        }
    }

    @Getter
    @Setter
    @ToString
    @Validated
    public static class PermissionDeclaration {
        @NotEmpty private List<ChannelPermission> channels;
        @NotEmpty private List<RolePermission> roles;
    }

    @Getter
    @Setter
    @ToString
    @Validated
    public static class ChannelPermission {
        @NotNull private Type type;
        private BotConfigServerChannel channel;
        private BotConfigServerChannelCategory category;

        public enum Type {
            SIMPLE, CATEGORY, SIMPLE_EXCLUSION, CATEGORY_EXCLUSION, ALL
        }
    }

    @Getter
    @Setter
    @ToString
    @Validated
    public static class RolePermission {
        @NotNull private Type type;
        @NotNull private BotConfigServerRole role;

        public enum Type {
            SIMPLE, EXCLUSION
        }
    }
}