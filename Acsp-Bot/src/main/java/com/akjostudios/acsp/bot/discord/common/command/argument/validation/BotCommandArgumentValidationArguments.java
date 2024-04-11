package com.akjostudios.acsp.bot.discord.common.command.argument.validation;

import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommand;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannel;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannelCategory;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.OnlineStatus;
import org.jetbrains.annotations.Contract;

import java.util.List;

public final class BotCommandArgumentValidationArguments {
    @Contract(pure = true)
    private BotCommandArgumentValidationArguments() {}

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StringValidationArguments implements BotCommandArgumentValidationArgument {
        private int minLength = 0;
        private int maxLength = Integer.MAX_VALUE;
        private String regex = ".*";
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ByteValidationArguments implements BotCommandArgumentValidationArgument {
        private byte minValue = Byte.MIN_VALUE;
        private byte maxValue = Byte.MAX_VALUE;
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShortValidationArguments implements BotCommandArgumentValidationArgument {
        private short minValue = Short.MIN_VALUE;
        private short maxValue = Short.MAX_VALUE;
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IntegerValidationArguments implements BotCommandArgumentValidationArgument {
        private int minValue = Integer.MIN_VALUE;
        private int maxValue = Integer.MAX_VALUE;
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LongValidationArguments implements BotCommandArgumentValidationArgument {
        private long minValue = Long.MIN_VALUE;
        private long maxValue = Long.MAX_VALUE;
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FloatValidationArguments implements BotCommandArgumentValidationArgument {
        private float minValue = Float.MIN_VALUE;
        private float maxValue = Float.MAX_VALUE;
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DoubleValidationArguments implements BotCommandArgumentValidationArgument {
        private double minValue = Double.MIN_VALUE;
        private double maxValue = Double.MAX_VALUE;
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserValidationArguments implements BotCommandArgumentValidationArgument {
        private boolean allowBot = false;
        private String minAge = null;
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MemberValidationArguments implements BotCommandArgumentValidationArgument {
        private List<BotConfigCommand.RolePermission> requiredRoles = null;
        private OnlineStatus requiredStatus = null;
        private boolean allowBot = false;
        private String minAge = null;
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoleValidationArguments implements BotCommandArgumentValidationArgument {
        private List<BotConfigServerRole> allowedRoles = null;
        private boolean mentionableRequired = false;
        private String minAge = null;
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChannelValidationArguments implements BotCommandArgumentValidationArgument {
        private List<BotConfigServerChannel> allowedChannels = null;
        private String minAge = null;
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CategoryValidationArguments implements BotCommandArgumentValidationArgument {
        private List<BotConfigServerChannelCategory> allowedCategories = null;
        private String minAge = null;
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TimeValidationArguments implements BotCommandArgumentValidationArgument {
        private RelativeType isRelative = RelativeType.ANY;
        private long isBefore = Long.MAX_VALUE;
        private long isAfter = Long.MIN_VALUE;

        public enum RelativeType {
            PAST, FUTURE, ANY
        }
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DurationValidationArguments implements BotCommandArgumentValidationArgument {
        private String minDuration = "0s";
        private String maxDuration = "9223372036854775807s";
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UrlValidationArguments implements BotCommandArgumentValidationArgument {
        private String schemeRegex = ".*";
        private String hostRegex = ".*";
        private String pathRegex = ".*";
        private String queryRegex = ".*";
    }
}