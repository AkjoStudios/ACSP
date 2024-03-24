package com.akjostudios.acsp.bot.discord.impl;

import com.akjostudios.acsp.bot.discord.api.AcspBot;
import com.akjostudios.acsp.bot.discord.common.BotEnvironment;
import com.akjostudios.acsp.bot.discord.internal.BotConfigProperties;
import com.github.tonivade.purefun.type.Option;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.util.Version;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
@Slf4j
public class AcspBotImpl implements AcspBot {
    @Getter private static Option<BotEnvironment> environment = Option.none();
    private final JDA botInstance;

    @Autowired
    @SuppressWarnings("java:S3010")
    public AcspBotImpl(
            @NotNull BotConfigProperties properties
    ) {
        environment = Option.of(properties.getEnvironment());
        log.info("Starting ACSP Discord Bot in environment '{}'.", properties.getEnvironment().name());

        JDABuilder builder = JDABuilder.createDefault(properties.getBotToken())
                .setEnabledIntents(EnumSet.allOf(GatewayIntent.class))
                .setMemberCachePolicy(MemberCachePolicy.ALL);

        botInstance = builder.build();
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
    public void shutdown(@NotNull ConfigurableApplicationContext context) {
        log.info("Shutting down ACSP Discord Bot...");
        try (context) { botInstance.shutdownNow(); } catch (Exception ex) {
            log.error("Failed to shutdown ACSP Discord Bot!", ex);
            Runtime.getRuntime().halt(1);
        } finally {
            log.info("ACSP Discord Bot has been shutdown.");
            Runtime.getRuntime().halt(0);
        }
    }
}