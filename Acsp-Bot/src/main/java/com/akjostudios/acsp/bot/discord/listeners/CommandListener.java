package com.akjostudios.acsp.bot.discord.listeners;

import com.akjostudios.acsp.bot.discord.common.BotEventType;
import com.akjostudios.acsp.bot.discord.common.command.BotCommand;
import com.akjostudios.acsp.bot.discord.common.command.CommandContext;
import com.akjostudios.acsp.bot.discord.common.listener.BotListener;
import com.akjostudios.acsp.bot.discord.service.BotDefinitionService;
import com.akjostudios.acsp.bot.discord.service.BotErrorMessageService;
import com.akjostudios.acsp.bot.discord.service.BotLayoutService;
import com.akjostudios.acsp.bot.discord.service.DiscordMessageService;
import com.github.tonivade.purefun.type.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandListener implements BotListener<MessageReceivedEvent> {
    private final DiscordMessageService discordMessageService;
    private final BotErrorMessageService errorMessageService;
    private final BotDefinitionService botDefinitionService;
    private final BotLayoutService botLayoutService;

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
        List<String> commandArgs = commandParts.subList(1, commandParts.size());

        CommandContext context = new CommandContext(commandName, commandArgs, event);
        context.initialize(botDefinitionService, discordMessageService, errorMessageService, botLayoutService);

        if (context.getDefinition().isEmpty()) {
            log.warn("User '{}' tried to use unavailable command '{}'!", event.getAuthor().getName(), commandName);

            errorMessageService.getErrorMessage(
                    "$error.unknown_command.title$",
                    "$error.unknown_command.description$",
                    List.of(commandName)
            ).map(discordMessageService::createMessage)
                    .map(event.getChannel()::sendMessage)
                    .onSuccess(RestAction::queue)
                    .onFailure(ex -> log.error("Failed to send error message!", ex));
            return;
        }

        Option.from(commands.stream().filter(command -> command.getName().equals(context.getName())).findFirst())
                .ifPresent(command -> command.execute(context))
                .ifEmpty(() -> {
                    log.warn("User '{}' tried to execute command '{}' but it was not found!", event.getAuthor().getName(), commandName);

                    errorMessageService.getErrorMessage(
                            "$error.unimplemented_command.title$",
                            "$error.unimplemented_command.description$",
                            List.of(commandName)
                    ).map(discordMessageService::createMessage)
                            .map(event.getChannel()::sendMessage)
                            .onSuccess(RestAction::queue)
                            .onFailure(ex -> log.error("Failed to send error message!", ex));
                });
    }
}