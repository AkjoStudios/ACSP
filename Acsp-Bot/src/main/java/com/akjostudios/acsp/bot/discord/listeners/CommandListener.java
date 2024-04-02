package com.akjostudios.acsp.bot.discord.listeners;

import com.akjostudios.acsp.bot.discord.common.BotEventType;
import com.akjostudios.acsp.bot.discord.common.command.BotCommand;
import com.akjostudios.acsp.bot.discord.common.command.BotCommandPermission;
import com.akjostudios.acsp.bot.discord.common.command.CommandContext;
import com.akjostudios.acsp.bot.discord.common.command.argument.BotCommandArgument;
import com.akjostudios.acsp.bot.discord.common.listener.BotListener;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommand;
import com.akjostudios.acsp.bot.discord.service.*;
import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Validation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("java:S1192")
public class CommandListener implements BotListener<MessageReceivedEvent> {
    private final DiscordMessageService discordMessageService;
    private final BotErrorMessageService errorMessageService;
    private final BotDefinitionService botDefinitionService;
    private final BotPrimitiveService botPrimitiveService;
    private final BotCommandPermissionService botCommandPermissionService;
    private final BotCommandArgumentService botCommandArgumentService;

    private final List<BotCommand> commands;

    @PostConstruct
    public void init() {
        log.info("CommandListener initialized with commands: {}", commands.stream().map(BotCommand::getName).toList());
    }

    @Override
    public void onEvent(@NotNull BotEventType type, @NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) { return; }
        if (!event.isFromGuild()) { return; }

        String commandPrefix = botDefinitionService.getCommandPrefix();
        if (!event.getMessage().getContentRaw().startsWith(commandPrefix)) { return; }

        String commandStr = event.getMessage().getContentRaw().substring(commandPrefix.length());

        List<String> commandParts = List.of(commandStr.split(" "));
        if (commandParts.isEmpty()) { return; }

        String commandName = commandParts.getFirst();
        Option<String> subcommand = getSubcommand(commandName, commandParts);

        CommandContext context = new CommandContext(commandName, subcommand, event);
        context.initialize(botDefinitionService, discordMessageService, errorMessageService, botPrimitiveService);

        if (!checkAvailability(context)) { return; }
        if (!checkSubcommandRequired(context)) { return; }
        if (!checkPermissions(context)) { return; }
        if (!parseArguments(context, commandParts, context.getSubcommandName())) { return; }

        runCommand(context);
    }

    private @NotNull Option<String> getSubcommand(@NotNull String commandName, @NotNull List<String> commandParts) {
        if (commandParts.size() < 2) { return Option.none(); }

        return botDefinitionService.getCommandDefinition(commandName)
                .filter(BotConfigCommand::isEnabled)
                .map(BotConfigCommand::getSubcommands)
                .filter(BotConfigCommand.Subcommands::isAvailable)
                .map(BotConfigCommand.Subcommands::getCommands)
                .flatMap(subcommands -> Option.from(subcommands.stream()
                        .filter(subcommandP -> subcommandP.getName().equals(commandParts.get(1)))
                        .findFirst())
                ).map(BotConfigCommand.Subcommand::getName);
    }

    private @NotNull List<String> getArguments(
            @NotNull List<String> commandParts,
            @NotNull Option<String> subcommand
    ) {
        if (subcommand.isEmpty()) { return commandParts.subList(1, commandParts.size()); }
        return commandParts.subList(2, commandParts.size());
    }

    private boolean checkAvailability(@NotNull CommandContext ctx) {
        if (ctx.getDefinition().isEmpty()) {
            log.warn("User '{}' tried to use unavailable command '{}'!",
                    ctx.getOriginalAuthor().getUser().getName(),
                    ctx.getName()
            );
            ctx.answer(ctx.getErrorMessage(
                    "$error.unknown_command.title$",
                    "$error.unknown_command.description$",
                    List.of(ctx.getName())
            )).onFailure(ex -> log.error("Failed to send error message!", ex));
            return false;
        }
        return true;
    }

    @SuppressWarnings("java:S5411")
    private boolean checkSubcommandRequired(@NotNull CommandContext ctx) {
        return ctx.getDefinition()
                .map(BotConfigCommand::getSubcommands)
                .map(BotConfigCommand.Subcommands::isRequired)
                .map(required -> { if (required && ctx.getSubcommandDefinition().isEmpty()) {
                    log.warn("User '{}' tried to use command '{}' without a required subcommand!",
                            ctx.getOriginalAuthor().getUser().getName(),
                            ctx.getName()
                    );
                    ctx.answer(ctx.getErrorMessage(
                            "$error.subcommand_required.title$",
                            "$error.subcommand_required.description$",
                            List.of(ctx.getName())
                    )).onFailure(ex -> log.error("Failed to send error message!", ex));
                    return false;
                } else { return true; }}).getOrElse(true);
    }

    private boolean checkPermissions(@NotNull CommandContext ctx) {
        boolean commandPermitted = checkPermissions(ctx, ctx.getDefinition()
                .map(BotConfigCommand::getPermissions), validation -> validation.fold(
                        error -> {
                            log.warn("User '{}' tried to use command '{}' but does not have the required permissions!",
                                    ctx.getOriginalAuthor().getUser().getName(),
                                    ctx.getName()
                            );
                            ctx.answer(ctx.getErrorMessage(
                                    "$error.permission_denied.title$",
                                    "$error.command.permission_denied.description$",
                                    List.of(
                                            ctx.getName(),
                                            validation.getError()
                                    )
                            )).onFailure(ex -> log.error("Failed to send error message!", ex));
                            return false;
                        }, success -> true
                ));
        boolean subcommandPermitted = checkPermissions(ctx, ctx.getSubcommandDefinition()
                .map(BotConfigCommand.Subcommand::getPermissions), validation -> validation.fold(
                        error -> {
                            log.warn("User '{}' tried to use subcommand '{}' of command '{}' but does not have the required permissions!",
                                    ctx.getOriginalAuthor().getUser().getName(),
                                    ctx.getSubcommandName().getOrElse("???"),
                                    ctx.getName()
                            );
                            ctx.answer(ctx.getErrorMessage(
                                    "$error.permission_denied.title$",
                                    "$error.subcommand.permission_denied.description$",
                                    List.of(
                                            ctx.getName(),
                                            ctx.getSubcommandName().getOrElse("???"),
                                            validation.getError()
                                    )
                            )).onFailure(ex -> log.error("Failed to send error message!", ex));
                            return false;
                        }, success -> true
                ));
        return commandPermitted && subcommandPermitted;
    }

    private boolean checkPermissions(
            @NotNull CommandContext ctx,
            @NotNull Option<List<BotConfigCommand.PermissionDeclaration>> permissionsList,
            @NotNull Function1<Validation<String, List<BotCommandPermission>>, Boolean> permissionCheck
    ) {
        return permissionsList.map(botCommandPermissionService::getPermissions)
                .map(permissions -> botCommandPermissionService.validatePermissions(
                        permissions,
                        ctx.getOriginalAuthor(),
                        ctx.getOriginalChannel()
                )).map(permissionCheck).getOrElse(true);
    }

    private boolean parseArguments(
            @NotNull CommandContext context,
            @NotNull List<String> commandParts,
            @NotNull Option<String> subcommand
    ) {
        List<String> givenArguments = getArguments(commandParts, subcommand);
        Validation<BotCommandArgumentService.ArgumentParseError, Map<String, String>> parsedArguments =
                botCommandArgumentService.parseArguments(context, givenArguments);
        if (parsedArguments.isInvalid()) {
            context.answer(
                    context.isSubcommand()
                            ? botCommandArgumentService.getSubcommandErrorMessage(parsedArguments.getError())
                            : botCommandArgumentService.getCommandErrorMessage(parsedArguments.getError())
            ).onFailure(ex -> log.error("Failed to send error message!", ex));
            return false;
        }
        List<Validation<BotCommandArgumentService.ArgumentValidationError, BotCommandArgument<?>>> arguments =
                botCommandArgumentService.convertArguments(context, parsedArguments.getOrElseThrow());
        List<Validation<BotCommandArgumentService.ArgumentValidationError, BotCommandArgument<?>>> validations =
                botCommandArgumentService.validateArguments(context, arguments);
        if (validations.stream().anyMatch(Validation::isInvalid)) {
            context.answer(botCommandArgumentService.getValidationReport(context, validations, Option.none()))
                    .onFailure(ex -> log.error("Failed to send error message!", ex));
            return false;
        }
        context.setArguments(arguments.stream().map(Validation::get).toList());
        return true;
    }

    private void runCommand(@NotNull CommandContext ctx) {
        Option.from(commands.stream().filter(command -> command.getName().equals(ctx.getName())).findFirst())
                .ifPresent(command -> command.execute(ctx))
                .ifEmpty(() -> {
                    log.warn("User '{}' tried to execute command '{}' but no implementation was found!",
                            ctx.getOriginalAuthor().getUser().getName(),
                            ctx.getName()
                    );
                    ctx.answer(ctx.getErrorMessage(
                            "$error.unimplemented_command.title$",
                            "$error.unimplemented_command.description$",
                            List.of(ctx.getName())
                    )).onFailure(ex -> log.error("Failed to send error message!", ex));
                });
    }
}