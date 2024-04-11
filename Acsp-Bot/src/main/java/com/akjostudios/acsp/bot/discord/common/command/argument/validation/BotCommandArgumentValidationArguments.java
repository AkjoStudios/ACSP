package com.akjostudios.acsp.bot.discord.common.command.argument.validation;

import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommand;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommandArgumentType;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannel;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannelCategory;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class BotCommandArgumentValidationArguments {
    @Contract(pure = true)
    private BotCommandArgumentValidationArguments() {}

    /**
     * <table>
     *     <tr>
     *         <th width='100'>Name</th>
     *         <th width='120'>Type</th>
     *         <th width='150'>Default</th>
     *         <th width='200'>Description</th>
     *     </tr>
     *     <tr>
     *         <td>minLength</td>
     *         <td>int</td>
     *         <td>0</td>
     *         <td>Minimum length of the string.</td>
     *     </tr>
     *     <tr>
     *         <td>maxLength</td>
     *         <td>int</td>
     *         <td>Integer.MAX_VALUE</td>
     *         <td>Maximum length of the string.</td>
     *     </tr>
     *     <tr>
     *         <td>regex</td>
     *         <td>String (Regex)</td>
     *         <td>".*"</td>
     *         <td>Regular expression the string must match.</td>
     *     </tr>
     * </table>
     * @see BotConfigCommandArgumentType#STRING
     **/
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StringValidationArguments implements BotCommandArgumentValidationArgument {
        private int minLength = 0;
        private int maxLength = Integer.MAX_VALUE;
        @NotNull private String regex = ".*";
    }

    /**
     * <table>
     *     <tr>
     *         <th width='100'>Name</th>
     *         <th width='80'>Type</th>
     *         <th width='150'>Default</th>
     *         <th width='200'>Description</th>
     *     </tr>
     *     <tr>
     *         <td>minValue</td>
     *         <td>byte</td>
     *         <td>Byte.MIN_VALUE</td>
     *         <td>Minimum value of the byte.</td>
     *     </tr>
     *     <tr>
     *         <td>maxValue</td>
     *         <td>byte</td>
     *         <td>Byte.MAX_VALUE</td>
     *         <td>Maximum value of the byte.</td>
     *     </tr>
     * </table>
     * @see BotConfigCommandArgumentType#BYTE
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ByteValidationArguments implements BotCommandArgumentValidationArgument {
        private byte minValue = Byte.MIN_VALUE;
        private byte maxValue = Byte.MAX_VALUE;
    }

    /**
     * <table>
     *     <tr>
     *         <th width='100'>Name</th>
     *         <th width='80'>Type</th>
     *         <th width='150'>Default</th>
     *         <th width='200'>Description</th>
     *     </tr>
     *     <tr>
     *         <td>minValue</td>
     *         <td>short</td>
     *         <td>Short.MIN_VALUE</td>
     *         <td>Minimum value of the short.</td>
     *     </tr>
     *     <tr>
     *         <td>maxValue</td>
     *         <td>short</td>
     *         <td>Short.MAX_VALUE</td>
     *         <td>Maximum value of the short.</td>
     *     </tr>
     * </table>
     * @see BotConfigCommandArgumentType#SHORT
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShortValidationArguments implements BotCommandArgumentValidationArgument {
        private short minValue = Short.MIN_VALUE;
        private short maxValue = Short.MAX_VALUE;
    }

    /**
     * <table>
     *     <tr>
     *         <th width='100'>Name</th>
     *         <th width='80'>Type</th>
     *         <th width='150'>Default</th>
     *         <th width='200'>Description</th>
     *     </tr>
     *     <tr>
     *         <td>minValue</td>
     *         <td>int</td>
     *         <td>Integer.MIN_VALUE</td>
     *         <td>Minimum value of the integer.</td>
     *     </tr>
     *     <tr>
     *         <td>maxValue</td>
     *         <td>int</td>
     *         <td>Integer.MAX_VALUE</td>
     *         <td>Maximum value of the integer.</td>
     *     </tr>
     * </table>
     * @see BotConfigCommandArgumentType#INTEGER
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IntegerValidationArguments implements BotCommandArgumentValidationArgument {
        private int minValue = Integer.MIN_VALUE;
        private int maxValue = Integer.MAX_VALUE;
    }

    /**
     * <table>
     *     <tr>
     *         <th width='100'>Name</th>
     *         <th width='80'>Type</th>
     *         <th width='150'>Default</th>
     *         <th width='200'>Description</th>
     *     </tr>
     *     <tr>
     *         <td>minValue</td>
     *         <td>long</td>
     *         <td>Long.MIN_VALUE</td>
     *         <td>Minimum value of the long.</td>
     *     </tr>
     *     <tr>
     *         <td>maxValue</td>
     *         <td>long</td>
     *         <td>Long.MAX_VALUE</td>
     *         <td>Maximum value of the long.</td>
     *     </tr>
     * </table>
     * @see BotConfigCommandArgumentType#LONG
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LongValidationArguments implements BotCommandArgumentValidationArgument {
        private long minValue = Long.MIN_VALUE;
        private long maxValue = Long.MAX_VALUE;
    }

    /**
     * <table>
     *     <tr>
     *         <th width='100'>Name</th>
     *         <th width='80'>Type</th>
     *         <th width='150'>Default</th>
     *         <th width='200'>Description</th>
     *     </tr>
     *     <tr>
     *         <td>minValue</td>
     *         <td>float</td>
     *         <td>Float.MIN_VALUE</td>
     *         <td>Minimum value of the float.</td>
     *     </tr>
     *     <tr>
     *         <td>maxValue</td>
     *         <td>float</td>
     *         <td>Float.MAX_VALUE</td>
     *         <td>Maximum value of the float.</td>
     *     </tr>
     * </table>
     * @see BotConfigCommandArgumentType#FLOAT
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FloatValidationArguments implements BotCommandArgumentValidationArgument {
        private float minValue = Float.MIN_VALUE;
        private float maxValue = Float.MAX_VALUE;
    }

    /**
     * <table>
     *     <tr>
     *         <th width='100'>Name</th>
     *         <th width='80'>Type</th>
     *         <th width='150'>Default</th>
     *         <th width='200'>Description</th>
     *     </tr>
     *     <tr>
     *         <td>minValue</td>
     *         <td>double</td>
     *         <td>Double.MIN_VALUE</td>
     *         <td>Minimum value of the double.</td>
     *     </tr>
     *     <tr>
     *         <td>maxValue</td>
     *         <td>double</td>
     *         <td>Double.MAX_VALUE</td>
     *         <td>Maximum value of the double.</td>
     *     </tr>
     * </table>
     * @see BotConfigCommandArgumentType#DOUBLE
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DoubleValidationArguments implements BotCommandArgumentValidationArgument {
        private double minValue = Double.MIN_VALUE;
        private double maxValue = Double.MAX_VALUE;
    }

    /**
     * <table>
     *     <tr>
     *         <th width='120'>Name</th>
     *         <th width='150'>Type</th>
     *         <th width='100'>Default</th>
     *         <th width='200'>Description</th>
     *     </tr>
     *     <tr>
     *         <td>allowBot</td>
     *         <td>boolean</td>
     *         <td>false</td>
     *         <td>Whether the user can be a bot.</td>
     *     </tr>
     *     <tr>
     *         <td>minAge</td>
     *         <td>String (Duration)</td>
     *         <td>null</td>
     *         <td>Minimum account age of the user.</td>
     *     </tr>
     * </table>
     * @see BotConfigCommandArgumentType#USER
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserValidationArguments implements BotCommandArgumentValidationArgument {
        private boolean allowBot = false;
        @Nullable private String minAge = null;
    }

    /**
     * <table>
     *     <tr>
     *         <th width='120'>Name</th>
     *         <th width='180'>Type</th>
     *         <th width='100'>Default</th>
     *         <th width='200'>Description</th>
     *     </tr>
     *     <tr>
     *         <td>requiredRoles</td>
     *         <td>List(RolePermission)</td>
     *         <td>null</td>
     *         <td>Roles the member must have.</td>
     *     </tr>
     *     <tr>
     *         <td>requiredStatus</td>
     *         <td>List(OnlineStatus)</td>
     *         <td>null</td>
     *         <td>Online status the member must have.</td>
     *     </tr>
     *     <tr>
     *         <td>allowBot</td>
     *         <td>boolean</td>
     *         <td>false</td>
     *         <td>Whether the member can be a bot.</td>
     *     </tr>
     *     <tr>
     *         <td>minAge</td>
     *         <td>String (Duration)</td>
     *         <td>null</td>
     *         <td>Minimum account age of the member.</td>
     *     </tr>
     * </table>
     * @see BotConfigCommandArgumentType#MEMBER
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MemberValidationArguments implements BotCommandArgumentValidationArgument {
        @Nullable private Map<Integer, BotConfigCommand.RolePermission> requiredRoles = null;
        @Nullable private Map<Integer, String> requiredStatus = null;
        private boolean allowBot = false;
        @Nullable private String minAge = null;
    }

    /**
     * <table>
     *     <tr>
     *         <th width='180'>Name</th>
     *         <th width='200'>Type</th>
     *         <th width='80'>Default</th>
     *         <th width='200'>Description</th>
     *     </tr>
     *     <tr>
     *         <td>allowedRoles</td>
     *         <td>List(BotConfigServerRole)</td>
     *         <td>null</td>
     *         <td>Allowed roles.</td>
     *     </tr>
     *     <tr>
     *         <td>mentionableRequired</td>
     *         <td>boolean</td>
     *         <td>false</td>
     *         <td>Whether the role must be mentionable.</td>
     *     </tr>
     *     <tr>
     *         <td>authorMustHaveRole</td>
     *         <td>boolean</td>
     *         <td>true</td>
     *         <td>Whether the author must have the role.</td>
     *     </tr>
     *     <tr>
     *         <td>minAge</td>
     *         <td>String (Duration)</td>
     *         <td>null</td>
     *         <td>Minimum age of the role.</td>
     *     </tr>
     * </table>
     * @see BotConfigCommandArgumentType#ROLE
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoleValidationArguments implements BotCommandArgumentValidationArgument {
        @Nullable private Map<Integer, BotConfigServerRole> allowedRoles = null;
        private boolean mentionableRequired = false;
        private boolean authorMustHaveRole = true;
        @Nullable private String minAge = null;
    }

    /**
     * <table>
     *     <tr>
     *         <th width='190'>Name</th>
     *         <th width='220'>Type</th>
     *         <th width='80'>Default</th>
     *         <th width='200'>Description</th>
     *     </tr>
     *     <tr>
     *         <td>allowedChannels</td>
     *         <td>List(BotConfigServerChannel)</td>
     *         <td>null</td>
     *         <td>Allowed channels.</td>
     *     </tr>
     *     <tr>
     *         <td>authorMustBeInChannel</td>
     *         <td>boolean</td>
     *         <td>true</td>
     *         <td>Whether the author must be in the channel.</td>
     *     </tr>
     *     <tr>
     *         <td>minAge</td>
     *         <td>String (Duration)</td>
     *         <td>null</td>
     *         <td>Minimum age of the channel.</td>
     *     </tr>
     * </table>
     * @see BotConfigCommandArgumentType#CHANNEL
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChannelValidationArguments implements BotCommandArgumentValidationArgument {
        @Nullable private Map<Integer, BotConfigServerChannel> allowedChannels = null;
        private boolean authorMustBeInChannel = true;
        @Nullable private String minAge = null;
    }

    /**
     * <table>
     *     <tr>
     *         <th width='150'>Name</th>
     *         <th width='280'>Type</th>
     *         <th width='60'>Default</th>
     *         <th width='200'>Description</th>
     *     </tr>
     *     <tr>
     *         <td>allowedCategories</td>
     *         <td>List(BotConfigServerChannelCategory)</td>
     *         <td>null</td>
     *         <td>Allowed categories.</td>
     *     </tr>
     *     <tr>
     *         <td>minAge</td>
     *         <td>String (Duration)</td>
     *         <td>null</td>
     *         <td>Minimum age of the category.</td>
     *     </tr>
     * </table>
     * @see BotConfigCommandArgumentType#CATEGORY
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CategoryValidationArguments implements BotCommandArgumentValidationArgument {
        @Nullable private Map<Integer, BotConfigServerChannelCategory> allowedCategories = null;
        @Nullable private String minAge = null;
    }

    /**
     * <table>
     *     <tr>
     *         <th width='100'>Name</th>
     *         <th width='120'>Type</th>
     *         <th width='150'>Default</th>
     *         <th width='200'>Description</th>
     *     </tr>
     *     <tr>
     *         <td>isRelative</td>
     *         <td>RelativeType</td>
     *         <td>ANY</td>
     *         <td>Whether the time must be in the past, future, or any.</td>
     *     </tr>
     *     <tr>
     *         <td>isBefore</td>
     *         <td>long</td>
     *         <td>Long.MAX_VALUE</td>
     *         <td>Maximum time in milliseconds since epoch.</td>
     *     </tr>
     *     <tr>
     *         <td>isAfter</td>
     *         <td>long</td>
     *         <td>Long.MIN_VALUE</td>
     *         <td>Minimum time in milliseconds since epoch.</td>
     *     </tr>
     * </table>
     * @see BotConfigCommandArgumentType#TIME
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TimeValidationArguments implements BotCommandArgumentValidationArgument {
        @NotNull private RelativeType isRelative = RelativeType.ANY;
        private long isBefore = Long.MAX_VALUE;
        private long isAfter = Long.MIN_VALUE;

        public enum RelativeType {
            PAST, FUTURE, ANY
        }
    }

    /**
     * <table>
     *     <tr>
     *         <th width='100'>Name</th>
     *         <th width='120'>Type</th>
     *         <th width='200'>Default</th>
     *         <th width='200'>Description</th>
     *     </tr>
     *     <tr>
     *         <td>minDuration</td>
     *         <td>String (Duration)</td>
     *         <td>"0s"</td>
     *         <td>Minimum duration.</td>
     *     </tr>
     *     <tr>
     *         <td>maxDuration</td>
     *         <td>String (Duration)</td>
     *         <td>"9223372036854775807s"</td>
     *         <td>Maximum duration.</td>
     *     </tr>
     * </table>
     * @see BotConfigCommandArgumentType#DURATION
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DurationValidationArguments implements BotCommandArgumentValidationArgument {
        @NotNull private String minDuration = "0s";
        @NotNull private String maxDuration = "9223372036854775807s";
    }

    /**
     * <table>
     *     <tr>
     *         <th width='110'>Name</th>
     *         <th width='120'>Type</th>
     *         <th width='100'>Default</th>
     *         <th width='200'>Description</th>
     *     </tr>
     *     <tr>
     *         <td>schemeRegex</td>
     *         <td>String (Regex)</td>
     *         <td>".*"</td>
     *         <td>Regular expression the scheme must match.</td>
     *     </tr>
     *     <tr>
     *         <td>hostRegex</td>
     *         <td>String (Regex)</td>
     *         <td>".*"</td>
     *         <td>Regular expression the host must match.</td>
     *     </tr>
     *     <tr>
     *         <td>pathRegex</td>
     *         <td>String (Regex)</td>
     *         <td>".*"</td>
     *         <td>Regular expression the path must match.</td>
     *     </tr>
     *     <tr>
     *         <td>queryRegex</td>
     *         <td>String (Regex)</td>
     *         <td>".*"</td>
     *         <td>Regular expression the query must match.</td>
     *     </tr>
     * </table>
     * @see BotConfigCommandArgumentType#URL
     **/
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UrlValidationArguments implements BotCommandArgumentValidationArgument {
        @NotNull private String schemeRegex = ".*";
        @NotNull private String hostRegex = ".*";
        @NotNull private String pathRegex = ".*";
        @NotNull private String queryRegex = ".*";
    }
}