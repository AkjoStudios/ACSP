package com.akjostudios.acsp.bot.discord.listeners;

import com.akjostudios.acsp.bot.discord.common.BotEnvironment;
import com.akjostudios.acsp.bot.discord.common.BotEventType;
import com.akjostudios.acsp.bot.discord.common.listener.BotListener;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannel;
import com.akjostudios.acsp.bot.discord.impl.AcspBotImpl;
import com.akjostudios.acsp.bot.discord.service.BotPrimitiveService;
import com.akjostudios.acsp.bot.discord.service.DiscordMessageService;
import com.github.tonivade.purefun.type.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReadyListener implements BotListener<ReadyEvent> {
    private final DiscordMessageService discordMessageService;
    private final BotPrimitiveService botPrimitiveService;

    @Override
    public void onEvent(@NotNull BotEventType type, @NotNull ReadyEvent event) {
        log.info("Bot is now ready!");

        BotEnvironment currentEnvironment = AcspBotImpl.getEnvironment().getOrElse(BotEnvironment.UNKNOWN);
        Option.of(() -> discordMessageService.createMessage(
                "Bot is now ready and running in " + currentEnvironment + " mode!"
        )).flatMap(data -> botPrimitiveService.getChannel(
                event, BotConfigServerChannel.ADMIN_CHAT
        ).map(channel -> channel.sendMessage(data))).ifPresent(RestAction::queue);
    }
}