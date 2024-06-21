package com.akjostudios.acsp.bot.discord.command;

import com.akjostudios.acsp.bot.AcspBotApp;
import com.akjostudios.acsp.bot.discord.common.command.BotCommand;
import com.akjostudios.acsp.bot.discord.common.command.BotCommandContext;
import com.akjostudios.acsp.bot.discord.common.command.BotCommandInteractionContext;
import com.akjostudios.acsp.bot.discord.common.command.IBotCommandContext;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommand;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessage;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessageEmbed;
import com.akjostudios.acsp.bot.discord.service.BotDefinitionService;
import com.akjostudios.acsp.bot.discord.service.DiscordMessageService;
import com.akjostudios.acsp.common.stream.PagingCollector;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

// TODO: Add buttons for switching pages when there are more commands than can fit on one page
@Component
public class HelpCommand implements BotCommand {
    private static final int PAGE_SIZE = 10;

    private BotDefinitionService botDefinitionService;
    private DiscordMessageService discordMessageService;
    private List<BotCommand> botCommands;

    @Override
    public void init(@NotNull IBotCommandContext ctx) {
        botDefinitionService = ctx.getBean(BotDefinitionService.class);
        discordMessageService = ctx.getBean(DiscordMessageService.class);
        botCommands = ctx.getBeans(BotCommand.class);
    }

    @Override
    public void execute(@NotNull BotCommandContext ctx) {
        ctx.getArgument("command", String.class)
                .ifEmpty(() -> printCommandList(ctx, 1))
                .ifPresent(this::printCommandHelp);
    }

    @Override
    public void onInteraction(@NotNull BotCommandInteractionContext ctx) {
        ctx.getCommandData().subscribe(data -> ctx.getArgument("command", String.class).ifEmpty(
                () -> printCommandList(ctx, (int) data.getOrDefault("page", 1))
        ));
    }

    private void printCommandList(@NotNull IBotCommandContext ctx, int page) {
        List<BotConfigCommand> commands = getCommandsForPage(page)
                .getOrElseThrow(() -> new IllegalStateException("Couldn't get commands for page " + page + "!"));

        List<Option<BotConfigMessageEmbed.Field>> fields = commands.stream()
                .map(this::getListFieldDefinition)
                .toList();

        Try<BotConfigMessage> message = discordMessageService.injectFields(
                getListMessageDefinition(),
                Option.none(),
                fields
        );

        ctx.answerOrReply(message);
    }

    private Option<List<BotConfigCommand>> getCommandsForPage(int page) {
        return Option.of(() -> botCommands.stream()
                        .map(BotCommand::getName)
                        .map(botDefinitionService::getCommandDefinition)
                        .filter(Option::isPresent)
                        .map(Option::getOrElseThrow)
                        .collect(new PagingCollector<>(PAGE_SIZE))
                )
                .filter(map -> !map.isEmpty())
                .map(map -> map.getOrDefault(page - 1, List.of()));
    }

    private @NotNull Option<BotConfigMessageEmbed.Field> getListFieldDefinition(
            @NotNull BotConfigCommand command
    ) {
        return botDefinitionService.getFieldDefinition(
                "help-command-list.command",
                command.getName(),
                command.getDescription()
        );
    }

    private @NotNull Try<BotConfigMessage> getListMessageDefinition(){
        return botDefinitionService.getMessageDefinition(
                "help-command-list",
                botDefinitionService.getCommandPrefix() + "help",
                AcspBotApp.BOT_NAME,
                AcspBotApp.DATE_TIME_FORMATTER.format(Instant.now())
        );
    }

    private void printCommandHelp(String commandName) {
        // TODO: Implement command help
    }

    @Override
    public String getName() { return "help"; }
}