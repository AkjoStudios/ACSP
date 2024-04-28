package com.akjostudios.acsp.bot.discord.listeners;

import com.akjostudios.acsp.bot.discord.common.BotEventType;
import com.akjostudios.acsp.bot.discord.common.command.BotCommand;
import com.akjostudios.acsp.bot.discord.common.listener.BotListener;
import com.akjostudios.acsp.bot.discord.service.BotErrorMessageService;
import com.akjostudios.acsp.bot.discord.service.DiscordMessageService;
import com.akjostudios.acsp.common.api.ExternalServiceClient;
import com.akjostudios.acsp.common.dto.bot.log.command.CommandExecutionGetResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandInteractionListener implements BotListener<GenericComponentInteractionCreateEvent> {
    private final DiscordMessageService discordMessageService;
    private final BotErrorMessageService errorMessageService;

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
        ).doOnError(__ -> onInvalidInteraction(event)).map(CommandExecutionGetResponse::result).subscribe(
                commandExecution -> commands.stream()
                        .filter(command -> command.getName().equals(commandExecution.commandName()))
                        .findFirst()
                        .ifPresent(BotCommand::onInteraction)
        );
    }

    private void onInvalidInteraction(@NotNull GenericComponentInteractionCreateEvent event) {
        errorMessageService.getErrorMessage(
                "$error.invalid_interaction.invalid.title$",
                "$error.invalid_interaction.invalid.description$"
        ).map(discordMessageService::createMessage).map(event.getChannel()::sendMessage)
                .onSuccess(RestAction::queue)
                .onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err));
    }
}