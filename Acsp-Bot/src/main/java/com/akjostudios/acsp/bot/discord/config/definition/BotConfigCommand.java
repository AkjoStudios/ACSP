package com.akjostudios.acsp.bot.discord.config.definition;

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
        @NotNull private BotConfigCommandArgumentType type;
        @NotNull private boolean required;
        private String description;
        private Map<@NotBlank String, @NotNull Object> validation;
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
        private BotConfigServerRole role;

        public enum Type {
            SIMPLE, EXCLUSION, ALL
        }
    }
}