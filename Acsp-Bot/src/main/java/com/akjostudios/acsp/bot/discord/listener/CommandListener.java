package com.akjostudios.acsp.bot.discord.listener;

import com.akjostudios.acsp.bot.discord.common.BotEventType;
import com.akjostudios.acsp.bot.discord.common.listener.BotListener;
import com.akjostudios.acsp.bot.discord.service.BotDefinitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandListener implements BotListener<MessageReceivedEvent> {
    private final BotDefinitionService botDefinitionService;

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

        log.info("Command {} received from {} with args {}.", commandName, event.getAuthor().getName(), commandArgs);
    }
}