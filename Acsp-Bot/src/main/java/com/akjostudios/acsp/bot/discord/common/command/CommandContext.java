package com.akjostudios.acsp.bot.discord.common.command;

import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommand;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessage;
import com.akjostudios.acsp.bot.discord.service.BotDefinitionService;
import com.akjostudios.acsp.bot.discord.service.BotErrorMessageService;
import com.akjostudios.acsp.bot.discord.service.DiscordMessageService;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class CommandContext {
    @Getter private final String name;
    @Getter private final List<String> args;

    private final MessageReceivedEvent event;

    private BotDefinitionService botDefinitionService;
    private DiscordMessageService discordMessageService;
    private BotErrorMessageService errorMessageService;

    public void initialize(
            BotDefinitionService botDefinitionService,
            DiscordMessageService discordMessageService,
            BotErrorMessageService errorMessageService
    ) {
        this.botDefinitionService = botDefinitionService;
        this.discordMessageService = discordMessageService;
        this.errorMessageService = errorMessageService;
    }

    public @NotNull Option<BotConfigCommand> getDefinition() {
        return botDefinitionService.getCommandDefinition(name);
    }

    public @NotNull Try<BotConfigMessage> getMessage(
            @NotNull String label,
            String@NotNull... placeholders
    ) { return botDefinitionService.getMessageDefinition(label, placeholders); }

    public @NotNull Try<BotConfigMessage> getMessage(
            @NotNull String label,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    ) { return botDefinitionService.getMessageDefinition(label, labelPlaceholders, placeholders); }

    public @NotNull Try<BotConfigMessage> getErrorMessage(
            @NotNull String errorTitle,
            @NotNull String errorDescription
    ) { return errorMessageService.getErrorMessage(errorTitle, errorDescription); }

    public Try<BotConfigMessage> getErrorMessage(
            @NotNull String errorTitle,
            @NotNull String errorDescription,
            @NotNull List<String> placeholders
    ) { return errorMessageService.getErrorMessage(errorTitle, errorDescription, placeholders); }

    public @NotNull Try<BotConfigMessage> getInternalErrorMessage(
            @NotNull String errorMessage
    ) { return errorMessageService.getInternalErrorMessage(errorMessage); }

    public @NotNull Try<MessageCreateAction> answer(@NotNull Try<BotConfigMessage> message) {
        return message.map(discordMessageService::createMessage)
                .map(event.getChannel()::sendMessage)
                .onSuccess(RestAction::queue);
    }
}