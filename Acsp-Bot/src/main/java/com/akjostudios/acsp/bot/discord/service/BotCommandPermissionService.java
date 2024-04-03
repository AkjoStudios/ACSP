package com.akjostudios.acsp.bot.discord.service;

import com.akjostudios.acsp.bot.discord.common.command.BotCommandPermission;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommand;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannel;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerRole;
import com.github.tonivade.purefun.type.Validation;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class BotCommandPermissionService {
    private final BotPrimitiveService botPrimitiveService;

    public @NotNull List<BotCommandPermission> getPermissions(
            @NotNull List<BotConfigCommand.PermissionDeclaration> permissions
    ) {
        Map<BotConfigServerRole, EnumSet<BotConfigServerChannel>> roleChannelMap = new EnumMap<>(BotConfigServerRole.class);

        permissions.stream().map(declaration -> Map.entry(
                getRolesFromDefinitions(declaration.getRoles()),
                getChannelsFromDefinitions(declaration.getChannels()))
        ).forEach(entry -> entry.getKey().forEach(
                role -> roleChannelMap.computeIfAbsent(
                        role,
                        k -> EnumSet.noneOf(BotConfigServerChannel.class)
                ).addAll(entry.getValue())
        ));

        return reverseMapping(roleChannelMap).entrySet().stream()
                .map(entry -> new BotCommandPermission(entry.getKey(), entry.getValue()))
                .toList();
    }

    public @NotNull Validation<String, List<BotCommandPermission>> validatePermissions(
            @NotNull List<BotCommandPermission> permissions,
            @NotNull Member member,
            @NotNull TextChannel channel
    ) {
        boolean channelDenied = permissions.stream()
                .map(BotCommandPermission::channel)
                .noneMatch(perm -> botPrimitiveService.getChannelId(perm).equals(channel.getIdLong()));
        if (channelDenied) return Validation.invalid("$error.missing_permissions.reason.channel$");

        boolean hasPermission = permissions.stream()
                .filter(perm -> botPrimitiveService.getChannelId(perm.channel()).equals(channel.getIdLong()))
                .map(BotCommandPermission::roles).flatMap(EnumSet::stream)
                .anyMatch(role -> botPrimitiveService.memberHasRole(member, role));

        return hasPermission
                ? Validation.valid(permissions)
                : Validation.invalid("$error.missing_permissions.reason.role$");
    }

    @SuppressWarnings("java:S1301")
    private @NotNull EnumSet<BotConfigServerRole> getRolesFromDefinitions(
            @NotNull List<BotConfigCommand.RolePermission> roleDefinitions
    ) {
        EnumSet<BotConfigServerRole> result = EnumSet.noneOf(BotConfigServerRole.class);
        roleDefinitions.forEach(permission -> {
            switch (permission.getType()) {
                case SIMPLE -> result.add(permission.getRole());
                case EXCLUSION -> result.remove(permission.getRole());
            }
        });
        return result;
    }

    private @NotNull EnumSet<BotConfigServerChannel> getChannelsFromDefinitions(
            @NotNull List<BotConfigCommand.ChannelPermission> channelDefinitions
    ) {
        EnumSet<BotConfigServerChannel> result = EnumSet.noneOf(BotConfigServerChannel.class);
        channelDefinitions.forEach(permission -> {
            switch (permission.getType()) {
                case SIMPLE -> result.add(permission.getChannel());
                case SIMPLE_EXCLUSION -> result.remove(permission.getChannel());
                case CATEGORY -> result.addAll(permission.getCategory().getChannels());
                case CATEGORY_EXCLUSION -> result.removeAll(permission.getCategory().getChannels());
                case ALL -> result.addAll(EnumSet.allOf(BotConfigServerChannel.class));
            }
        });
        return result;
    }

    private @NotNull Map<BotConfigServerChannel, EnumSet<BotConfigServerRole>> reverseMapping(
            @NotNull Map<BotConfigServerRole, EnumSet<BotConfigServerChannel>> roleChannelMap
    ) {
        return roleChannelMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(
                        channel -> Map.entry(channel, entry.getKey())
                )).collect(
                        HashMap::new,
                        (map, entry) -> map.computeIfAbsent(
                                entry.getKey(), k -> EnumSet.noneOf(BotConfigServerRole.class)
                        ).add(entry.getValue()),
                        HashMap::putAll
                );
    }
}