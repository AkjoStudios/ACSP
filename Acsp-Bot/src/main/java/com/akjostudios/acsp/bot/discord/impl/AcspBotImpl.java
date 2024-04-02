package com.akjostudios.acsp.bot.discord.impl;

import com.akjostudios.acsp.bot.discord.api.AcspBot;
import com.akjostudios.acsp.bot.discord.common.BotEnvironment;
import com.akjostudios.acsp.bot.discord.common.listener.CommonListener;
import com.akjostudios.acsp.bot.discord.config.BotConfigProperties;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannel;
import com.akjostudios.acsp.bot.discord.service.BotPrimitiveService;
import com.akjostudios.acsp.bot.discord.service.DiscordMessageService;
import com.github.tonivade.purefun.type.Option;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.util.Version;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.EnumSet;

@Component
@Slf4j
public class AcspBotImpl implements AcspBot {
    @Getter private static Option<BotEnvironment> environment = Option.none();
    private final JDA botInstance;

    private final DiscordMessageService discordMessageService;
    private final BotPrimitiveService botPrimitiveService;
    private final BotConfigProperties botConfigProperties;

    @Autowired
    @SuppressWarnings("java:S3010")
    public AcspBotImpl(
            @NotNull BotConfigProperties properties,
            @NotNull CommonListener commonListener,
            @NotNull DiscordMessageService discordMessageService,
            @NotNull BotPrimitiveService botPrimitiveService,
            @NotNull BotConfigProperties botConfigProperties
    ) {
        environment = Option.of(properties.getEnvironment());
        log.info("Starting ACSP Discord Bot in environment '{}'.", properties.getEnvironment().name());

        JDABuilder builder = JDABuilder.createDefault(properties.getBotToken())
                .setEnabledIntents(EnumSet.allOf(GatewayIntent.class))
                .setMemberCachePolicy(MemberCachePolicy.ALL);

        builder.addEventListeners(commonListener);
        builder.setEnableShutdownHook(false);

        botInstance = builder.build();

        this.discordMessageService = discordMessageService;
        this.botPrimitiveService = botPrimitiveService;
        this.botConfigProperties = botConfigProperties;
    }

    @Override
    public @NotNull JDA.Status getStatus() {
        return botInstance.getStatus();
    }

    @Override
    public @NotNull Version getVersion() {
        return Version.parse(JDAInfo.VERSION_MAJOR + "." + JDAInfo.VERSION_MINOR + "." + JDAInfo.VERSION_REVISION);
    }

    @Override
    @SuppressWarnings("java:S2142")
    public void shutdown(@NotNull ConfigurableApplicationContext context) {
        log.info("Shutting down ACSP Discord Bot...");

        if (!botConfigProperties.getDeploymentId().equals("none")) {
            Option.of(() -> discordMessageService.createMessage(
                    "`" + botConfigProperties.getDeploymentId() + "`Bot is now shutting down in " + environment.getOrElse(BotEnvironment.UNKNOWN) + " mode!"
            )).flatMap(data -> botPrimitiveService.getChannel(
                    botInstance, BotConfigServerChannel.AUDIT_LOG
            ).map(channel -> channel.sendMessage(data))).ifPresent(RestAction::queue);
        }

        try (context) { botInstance.shutdown(); } catch (Exception ex) {
            log.error("Failed to shutdown ACSP Discord Bot!", ex);
            Runtime.getRuntime().halt(1);
        }

        try {
            if (!botInstance.awaitShutdown(Duration.ofSeconds(10))) {
                log.error("Failed to await shutdown of ACSP Discord Bot!");
                Runtime.getRuntime().halt(1);
            }
        } catch (InterruptedException ex) {
            log.error("Failed to await shutdown of ACSP Discord Bot!", ex);
            Runtime.getRuntime().halt(1);
        }

        log.info("ACSP Discord Bot has been shutdown.");
        Runtime.getRuntime().halt(0);
    }
}