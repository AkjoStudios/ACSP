package com.akjostudios.acsp.bot.discord.command;

import com.akjostudios.acsp.bot.discord.common.command.BotCommand;
import com.akjostudios.acsp.bot.discord.common.command.BotCommandContext;
import com.akjostudios.acsp.bot.discord.common.command.BotCommandInteractionContext;
import com.akjostudios.acsp.bot.discord.common.command.IBotCommandContext;
import com.akjostudios.acsp.bot.discord.service.BotDefinitionService;
import com.akjostudios.acsp.common.stream.PagingCollector;
import com.github.tonivade.purefun.type.Option;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class HelpCommand implements BotCommand {
    private BotDefinitionService botDefinitionService;
    private List<BotCommand> botCommands;
    private Map<String, Object> commandData;

    @Override
    public void init(@NotNull IBotCommandContext ctx) {
        botDefinitionService = ctx.getBean(BotDefinitionService.class);
        botCommands = ctx.getBeans(BotCommand.class);
        commandData = ctx.getCommandData().block();
    }

    @Override
    public void execute(@NotNull BotCommandContext ctx) {
        ctx.getArgument("command", String.class)
                .ifEmpty(() -> printCommandList(1))
                .ifPresent(this::printCommandHelp);
    }

    @Override
    public void onInteraction(@NotNull BotCommandInteractionContext ctx) {
        ctx.getArgument("command", String.class).ifEmpty(() -> printCommandList(
                (int) commandData.getOrDefault("page", 1))
        );
    }

    private void printCommandList(int page) {
        Option.of(() -> botCommands.stream()
                .map(BotCommand::getName)
                .map(botDefinitionService::getCommandDefinition)
                .filter(Option::isPresent)
                .map(Option::getOrElseThrow)
                .collect(new PagingCollector<>(25)))
                .filter(map -> !map.isEmpty())
                .map(map -> map.get(page))
                .ifPresent(System.out::println);
    }

    private void printCommandHelp(String commandName) {

    }

    @Override
    public String getName() { return "help"; }
}