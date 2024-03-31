package com.akjostudios.acsp.bot.discord.listeners;

import com.akjostudios.acsp.bot.discord.common.BotEventType;
import com.akjostudios.acsp.bot.discord.common.command.BotCommand;
import com.akjostudios.acsp.bot.discord.common.command.CommandContext;
import com.akjostudios.acsp.bot.discord.common.listener.BotListener;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommand;
import com.akjostudios.acsp.bot.discord.service.*;
import com.github.tonivade.purefun.type.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("java:S1192")
public class CommandListener implements BotListener<MessageReceivedEvent> {
    private final DiscordMessageService discordMessageService;
    private final BotErrorMessageService errorMessageService;
    private final BotDefinitionService botDefinitionService;
    private final BotPrimitiveService botPrimitiveService;
    private final BotCommandPermissionService botCommandPermissionService;

    private final List<BotCommand> commands;

    @Override
    public void onEvent(BotEventType type, MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) { return; }
        if (!event.isFromGuild()) { return; }

        String commandPrefix = botDefinitionService.getCommandPrefix();
        if (!event.getMessage().getContentRaw().startsWith(commandPrefix)) { return; }

        String commandStr = event.getMessage().getContentRaw().substring(commandPrefix.length());

        List<String> commandParts = List.of(commandStr.split(" "));
        if (commandParts.isEmpty()) { return; }

        String commandName = commandParts.getFirst();

        CommandContext context = new CommandContext(commandName, event);
        context.initialize(botDefinitionService, discordMessageService, errorMessageService, botPrimitiveService);

        if (!checkAvailability(context)) { return; }
        if (!checkPermissions(context)) { return; }

        runCommand(context);
    }

    private boolean checkAvailability(@NotNull CommandContext ctx) {
        if (ctx.getDefinition().isEmpty()) {
            log.warn("User '{}' tried to use unavailable command '{}'!",
                    ctx.getOriginalAuthor().getUser().getName(),
                    ctx.getName()
            );
            ctx.answer(ctx.getErrorMessage(
                    "$error.unknown_command.title$",
                    "$error.unknown_command.description$",
                    List.of(ctx.getName())
            )).onFailure(ex -> log.error("Failed to send error message!", ex));
            return false;
        }
        return true;
    }

    private boolean checkPermissions(@NotNull CommandContext ctx) {
        return ctx.getDefinition().map(BotConfigCommand::getPermissions)
                .map(botCommandPermissionService::getPermissions)
                .map(permissions -> botCommandPermissionService.validatePermissions(
                        permissions,
                        ctx.getOriginalAuthor(),
                        ctx.getOriginalChannel()
                )).map(validation -> validation.fold(
                        error -> {
                            log.warn("User '{}' tried to use command '{}' but does not have the required permissions!",
                                    ctx.getOriginalAuthor().getUser().getName(),
                                    ctx.getName()
                            );
                            ctx.answer(ctx.getErrorMessage(
                                    "$error.permission_denied.title$",
                                    "$error.permission_denied.description$",
                                    List.of(ctx.getName(), validation.getError())
                            )).onFailure(ex -> log.error("Failed to send error message!", ex));
                            return false;
                        }, success -> true
                )).getOrElse(false);
    }

    private void runCommand(@NotNull CommandContext ctx) {
        Option.from(commands.stream().filter(command -> command.getName().equals(ctx.getName())).findFirst())
                .ifPresent(command -> command.execute(ctx))
                .ifEmpty(() -> {
                    log.warn("User '{}' tried to execute command '{}' but no implementation was found!",
                            ctx.getOriginalAuthor().getUser().getName(),
                            ctx.getName()
                    );
                    ctx.answer(ctx.getErrorMessage(
                            "$error.unimplemented_command.title$",
                            "$error.unimplemented_command.description$",
                            List.of(ctx.getName())
                    )).onFailure(ex -> log.error("Failed to send error message!", ex));
                });
    }
}