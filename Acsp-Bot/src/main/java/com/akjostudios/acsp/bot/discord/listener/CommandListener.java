package com.akjostudios.acsp.bot.discord.listener;

import com.akjostudios.acsp.bot.discord.common.BotEventType;
import com.akjostudios.acsp.bot.discord.common.listener.BotListener;
import com.akjostudios.acsp.bot.discord.service.BotErrorMessageService;
import com.akjostudios.acsp.bot.discord.service.DiscordMessageService;
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
    @Override
    public void onEvent(BotEventType type, MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) { return; }
        log.info("Message received: {}", event.getMessage().getContentRaw());
    }
}