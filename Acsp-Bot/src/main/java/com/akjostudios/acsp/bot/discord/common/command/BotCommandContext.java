package com.akjostudios.acsp.bot.discord.common.command;

import com.akjostudios.acsp.bot.discord.common.command.argument.BotCommandArgument;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommand;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessage;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannel;
import com.akjostudios.acsp.bot.discord.service.BotDefinitionService;
import com.akjostudios.acsp.bot.discord.service.BotErrorMessageService;
import com.akjostudios.acsp.bot.discord.service.BotPrimitiveService;
import com.akjostudios.acsp.bot.discord.service.DiscordMessageService;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor
@SuppressWarnings({"unused", "UnusedReturnValue", "java:S4968"})
public class BotCommandContext {
    @Getter private final String name;

    private final Option<String> subcommand;

    private final MessageReceivedEvent event;

    /**
     * @apiNote Should not be called by the command implementation.
     */
    @Setter
    private List<? extends BotCommandArgument<?>> arguments;

    private BotDefinitionService botDefinitionService;
    private DiscordMessageService discordMessageService;
    private BotErrorMessageService errorMessageService;
    private BotPrimitiveService botPrimitiveService;

    /**
     * @apiNote Should not be called by the command implementation.
     */
    public void initialize(
            BotDefinitionService botDefinitionService,
            DiscordMessageService discordMessageService,
            BotErrorMessageService errorMessageService,
            BotPrimitiveService botPrimitiveService
    ) {
        this.botDefinitionService = botDefinitionService;
        this.discordMessageService = discordMessageService;
        this.errorMessageService = errorMessageService;
        this.botPrimitiveService = botPrimitiveService;
    }

    public @NotNull Option<BotConfigCommand> getDefinition() {
        return botDefinitionService.getCommandDefinition(name)
                .filter(BotConfigCommand::isEnabled);
    }

    public @NotNull Option<BotConfigCommand.Subcommand> getSubcommandDefinition() {
        return getDefinition().map(BotConfigCommand::getSubcommands)
                .filter(BotConfigCommand.Subcommands::isAvailable)
                .map(BotConfigCommand.Subcommands::getCommands)
                .flatMap(subcommands -> Option.from(subcommands.stream()
                        .filter(subcommandP -> subcommandP.getName().equals(subcommand.getOrElseNull()))
                        .findFirst())
                );
    }

    public boolean isSubcommand() { return subcommand.isPresent(); }
    public Option<String> getSubcommandName() {
        return getSubcommandDefinition().map(BotConfigCommand.Subcommand::getName);
    }

    public @NotNull List<BotConfigCommand.Argument> getArgumentDefinitions() {
        return isSubcommand()
                ? getSubcommandDefinition().flatMap(subcommandP -> Option.of(subcommandP::getArguments)).getOrElse(List.of())
                : getDefinition().flatMap(subcommandP -> Option.of(subcommandP::getArguments)).getOrElse(List.of());
    }

    @SuppressWarnings("unchecked")
    public <T> @NotNull T getArgument(@NotNull String id, @NotNull T defaultValue) {
        return Option.from(arguments.stream()
                .filter(argument -> argument.id().equals(id))
                .findFirst())
                .map(argument -> (T) argument.value())
                .getOrElse(defaultValue);
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

    public @NotNull Try<MessageCreateAction> answer(String message) {
        return Try.of(() -> discordMessageService.createMessage(message))
                .map(event.getChannel()::sendMessage)
                .onSuccess(RestAction::queue);
    }

    public @NotNull Try<MessageCreateAction> answer(@NotNull Try<BotConfigMessage> message) {
        return message.map(discordMessageService::createMessage)
                .map(event.getChannel()::sendMessage)
                .onSuccess(RestAction::queue);
    }

    public @NotNull Try<MessageCreateAction> sendMessage(String message, @NotNull BotConfigServerChannel channel) {
        return botPrimitiveService.getChannel(event, channel).toTry().flatMap(
                textChannel -> Try.of(
                        () -> discordMessageService.createMessage(message)
                ).map(textChannel::sendMessage)
        ).onSuccess(RestAction::queue);
    }

    public @NotNull Try<MessageCreateAction> sendMessage(@NotNull Try<BotConfigMessage> message, @NotNull BotConfigServerChannel channel) {
        return botPrimitiveService.getChannel(event, channel).toTry().flatMap(
                textChannel -> message.map(discordMessageService::createMessage).map(textChannel::sendMessage)
        ).onSuccess(RestAction::queue);
    }

    public @NotNull Try<MessageCreateAction> sendPrivateMessage(@NotNull String message) {
        return event.getAuthor().openPrivateChannel().map(
                privateChannel -> Try.of(() -> discordMessageService.createMessage(message))
                        .map(privateChannel::sendMessage)
                        .onSuccess(RestAction::queue)
        ).complete();
    }

    public @NotNull Try<MessageCreateAction> sendPrivateMessage(@NotNull Try<BotConfigMessage> message) {
        return event.getAuthor().openPrivateChannel().map(
                privateChannel -> message.map(discordMessageService::createMessage)
                        .map(privateChannel::sendMessage)
                        .onSuccess(RestAction::queue)
        ).complete();
    }

    public @NotNull Member getOriginalAuthor() { return Option.of(event.getMember()).getOrElseThrow(); }

    public @NotNull Message getOriginalMessage() { return event.getMessage(); }

    public @NotNull TextChannel getOriginalChannel() { return event.getChannel().asTextChannel(); }

    public @NotNull String getJumpUrl() { return event.getMessage().getJumpUrl(); }

    public @NotNull Option<User> getUser(@NotNull String userId) {
        return botPrimitiveService.getUser(event, userId);
    }

    public @NotNull Option<Member> getMember(@NotNull String userId) {
        return botPrimitiveService.getMember(event, userId);
    }

    public @NotNull Option<Role> getRole(@NotNull String roleId) {
        return botPrimitiveService.getRole(event, roleId);
    }

    public @NotNull Option<TextChannel> getTextChannel(@NotNull String channelId) {
        return botPrimitiveService.getTextChannel(event, channelId);
    }

    public @NotNull Option<VoiceChannel> getVoiceChannel(@NotNull String channelId) {
        return botPrimitiveService.getVoiceChannel(event, channelId);
    }

    public @NotNull Option<Category> getCategory(@NotNull String categoryId) {
        return botPrimitiveService.getCategory(event, categoryId);
    }

    public @NotNull Option<Emoji> getEmoji(@NotNull String emojiId) {
        return botPrimitiveService.getEmoji(event, emojiId);
    }
}