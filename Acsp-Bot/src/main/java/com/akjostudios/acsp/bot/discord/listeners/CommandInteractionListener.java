package com.akjostudios.acsp.bot.discord.listeners;

import com.akjostudios.acsp.bot.discord.common.BotEventType;
import com.akjostudios.acsp.bot.discord.common.command.BotCommand;
import com.akjostudios.acsp.bot.discord.common.command.BotCommandContext;
import com.akjostudios.acsp.bot.discord.common.command.BotCommandInteractionContext;
import com.akjostudios.acsp.bot.discord.common.command.argument.BotCommandArgument;
import com.akjostudios.acsp.bot.discord.common.listener.BotListener;
import com.akjostudios.acsp.bot.discord.service.*;
import com.akjostudios.acsp.common.api.ExternalServiceClient;
import com.akjostudios.acsp.common.dto.bot.log.command.CommandExecutionGetResponse;
import com.akjostudios.acsp.common.model.bot.log.command.CommandExecutionDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tonivade.purefun.type.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandInteractionListener implements BotListener<GenericComponentInteractionCreateEvent> {
    private final ApplicationContext applicationContext;
    private final DiscordMessageService discordMessageService;
    private final BotErrorMessageService errorMessageService;
    private final BotDefinitionService botDefinitionService;
    private final BotPrimitiveService botPrimitiveService;
    private final BotLayoutService botLayoutService;
    private final BotCommandArgumentService botCommandArgumentService;
    private final ObjectMapper objectMapper;

    @Qualifier("client.service.backend")
    private final ExternalServiceClient backendClient;

    private final List<BotCommand> commands;

    @Override
    public void onEvent(@NotNull BotEventType type, @NotNull GenericComponentInteractionCreateEvent event) {
        long messageId = event.getMessageIdLong();
        if (event.getMessage().getAuthor().getIdLong() != event.getJDA().getSelfUser().getIdLong()) {
            return;
        }

        backendClient.exchangeGet(
                "/api/bot/log/command/execution/rmid/" + messageId,
                CommandExecutionGetResponse.class
        ).onErrorResume(ex -> {
            onInvalidInteraction(event);
            return Mono.empty();
        }).map(CommandExecutionGetResponse::result)
                .filter(CommandExecutionDao::finished)
                .subscribe(commandExecution -> commands.stream()
                        .filter(command -> command.getName().equals(commandExecution.commandName()))
                        .findFirst()
                        .ifPresent(command -> onSuccessfulInteraction(command, commandExecution, event))
                );
    }

    private void onSuccessfulInteraction(
            @NotNull BotCommand command,
            @NotNull CommandExecutionDao commandExecution,
            @NotNull GenericComponentInteractionCreateEvent event
    ) {
        BotCommandInteractionContext ctx = new BotCommandInteractionContext(
                commandExecution.commandName(),
                Option.of(commandExecution::subcommandName),
                event.getComponentId(),
                event,
                commandExecution.executionId(),
                commandExecution.messageId(),
                commandExecution.channelId(),
                commandExecution.userId()
        );
        BotCommandContext originalCtx = ctx.initialize(
                applicationContext,
                botDefinitionService,
                discordMessageService,
                errorMessageService,
                botPrimitiveService,
                botLayoutService,
                objectMapper
        );

        List<BotCommandArgument<?>> convertedArguments = new ArrayList<>();
        botCommandArgumentService.convertArguments(
                originalCtx,
                commandExecution.commandArgs()
        ).forEach(validation -> validation.fold(
                err -> false,
                convertedArguments::add
        ));
        ctx.setArguments(convertedArguments);

        command.init(ctx);
        command.onInteraction(ctx);
    }

    private void onInvalidInteraction(@NotNull GenericComponentInteractionCreateEvent event) {
        errorMessageService.getErrorMessage(
                "$error.invalid_interaction.invalid.title$",
                "$error.invalid_interaction.invalid.description$"
        ).map(discordMessageService::editMessage).map(event::editMessage)
                .onSuccess(RestAction::queue)
                .onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err));
    }
}