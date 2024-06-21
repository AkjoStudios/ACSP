package com.akjostudios.acsp.bot.discord.command;

import com.akjostudios.acsp.bot.AcspBotApp;
import com.akjostudios.acsp.bot.discord.common.command.BotCommand;
import com.akjostudios.acsp.bot.discord.common.command.BotCommandContext;
import com.akjostudios.acsp.bot.discord.common.command.BotCommandInteractionContext;
import com.akjostudios.acsp.bot.discord.common.command.IBotCommandContext;
import com.akjostudios.acsp.bot.discord.common.component.BotActionRowComponent;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommand;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessage;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessageEmbed;
import com.akjostudios.acsp.bot.discord.service.BotDefinitionService;
import com.akjostudios.acsp.bot.discord.service.DiscordMessageService;
import com.akjostudios.acsp.common.stream.PagingCollector;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class HelpCommand implements BotCommand {
    private static final int PAGE_SIZE = 10;

    private Option<Map<String, Object>> commandData = Option.none();

    private BotDefinitionService botDefinitionService;
    private DiscordMessageService discordMessageService;
    private List<BotCommand> botCommands;

    @Override
    public void init(@NotNull IBotCommandContext ctx) {
        botDefinitionService = ctx.getBean(BotDefinitionService.class);
        discordMessageService = ctx.getBean(DiscordMessageService.class);
        botCommands = ctx.getBeans(BotCommand.class);

        ctx.getCommandData().subscribe(data -> commandData = Option.of(data));
    }

    @Override
    public void execute(@NotNull BotCommandContext ctx) {
        ctx.getArgument("command", String.class)
                .ifEmpty(() -> printCommandList(ctx, 1))
                .ifPresent(command -> printCommandHelp(ctx, command));
    }

    @Override
    public void onInteraction(@NotNull BotCommandInteractionContext ctx) {
        ctx.getCommandData().subscribe(data -> ctx.getArgument("command", String.class).ifEmpty(
                () -> printCommandList(ctx, (int) data.getOrDefault("page", 1))
        ));
    }

    private void printCommandList(@NotNull IBotCommandContext ctx, int page) {
        Option<Map<Integer, List<BotConfigCommand>>> commandsPaginated = getAllCommands();

        List<BotConfigCommand> commands = commandsPaginated
                .filter(map -> !map.isEmpty())
                .map(map -> map.getOrDefault(page - 1, List.of()))
                .getOrElseThrow(() -> new IllegalStateException("Couldn't get commands for page " + page + "!"));

        List<Option<BotConfigMessageEmbed.Field>> fields = commands.stream()
                .map(this::getListFieldDefinition)
                .toList();

        List<Option<BotActionRowComponent>> components = discordMessageService.createPaginationComponents(
                page,
                hasNextPage(commandsPaginated, page),
                hasPreviousPage(page)
        );

        Try<BotConfigMessage> message = getListMessageDefinition(page, commandsPaginated.map(Map::size).getOrElse(0))
                .flatMap(msg -> discordMessageService.injectFields(
                        msg, Option.none(), fields
                )).flatMap(msg -> discordMessageService.injectComponents(
                        msg, components
                ));

        ctx.answerOrReply(message);

        commandData.map(data -> {
            data.put("page", page);
            return data;
        }).ifPresent(ctx::setCommandData);
    }

    private Option<Map<Integer, List<BotConfigCommand>>> getAllCommands() {
        return Option.of(() -> botCommands.stream()
                .map(BotCommand::getName)
                .map(botDefinitionService::getCommandDefinition)
                .filter(Option::isPresent)
                .map(Option::getOrElseThrow)
                .collect(new PagingCollector<>(PAGE_SIZE))
        );
    }

    @Contract(pure = true)
    private boolean hasNextPage(@NotNull Option<Map<Integer, List<BotConfigCommand>>> commandMap, int page) {
        return commandMap.map(cmdMap -> cmdMap.containsKey(page)).getOrElse(false);
    }

    @Contract(pure = true)
    private boolean hasPreviousPage(int page) {
        return page > 1;
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

    private @NotNull Try<BotConfigMessage> getListMessageDefinition(int page, int pageCount) {
        return botDefinitionService.getMessageDefinition(
                "help-command-list",
                botDefinitionService.getCommandPrefix() + "help",
                AcspBotApp.BOT_NAME,
                Integer.toString(page),
                Integer.toString(pageCount),
                AcspBotApp.DATE_TIME_FORMATTER.format(Instant.now())
        );
    }

    private void printCommandHelp(@NotNull IBotCommandContext ctx, String commandName) {
        BotConfigCommand command = botDefinitionService.getCommandDefinition(commandName)
                .ifEmpty(() -> ctx.answerOrReply(ctx.getErrorMessage(
                        "$error.help.command-not-found.title$",
                        "$error.help.command-not-found.description$",
                        List.of(commandName)
                ))).getOrElseNull();
        if (command == null) {  return; }

        List<Option<BotConfigMessageEmbed.Field>> argumentFields = command.getArguments().stream()
                .map(this::getArgumentFieldDefinition)
                .toList();

        List<Option<BotConfigMessageEmbed.Field>> subcommandFields = command.getSubcommands().isAvailable()
                ? command.getSubcommands().getCommands().stream().map(this::getSubcommandFieldDefinition).toList()
                : new ArrayList<>();

        Try<BotConfigMessage> message = getCommandMessageDefinition(command)
                .flatMap(msg -> discordMessageService.injectFields(
                        msg, Option.none(), argumentFields
                )).flatMap(msg -> discordMessageService.injectFields(
                        msg, Option.none(), subcommandFields
                ));

        ctx.answerOrReply(message);

        commandData.map(data -> {
            data.put("page", 1);
            return data;
        }).ifPresent(ctx::setCommandData);
    }

    private @NotNull Option<BotConfigMessageEmbed.Field> getArgumentFieldDefinition(
            @NotNull BotConfigCommand.Argument argument
    ) {
        String name = argument.isRequired()
                ? "`" + argument.getId() + "`*"
                : "`" + argument.getId() + "` (optional)";

        return botDefinitionService.getFieldDefinition(
                "help-command.argument",
                name,
                argument.getDescription()
        );
    }

    private @NotNull Option<BotConfigMessageEmbed.Field> getSubcommandFieldDefinition(
            @NotNull BotConfigCommand.Subcommand subcommand
    ) {
        return botDefinitionService.getFieldDefinition(
                "help-command.subcommand",
                subcommand.getName(),
                subcommand.getDescription()
        );
    }

    private @NotNull Try<BotConfigMessage> getCommandMessageDefinition(
            @NotNull BotConfigCommand command
    ) {
        return botDefinitionService.getMessageDefinition(
                "help-command",
                command.getName(),
                AcspBotApp.BOT_NAME,
                command.getDescription(),
                AcspBotApp.DATE_TIME_FORMATTER.format(Instant.now())
        );
    }

    @Override
    public String getName() { return "help"; }
}