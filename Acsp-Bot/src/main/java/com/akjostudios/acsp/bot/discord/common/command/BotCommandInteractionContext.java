package com.akjostudios.acsp.bot.discord.common.command;

import com.akjostudios.acsp.bot.discord.common.command.argument.BotCommandArgument;
import com.akjostudios.acsp.bot.discord.common.component.BotActionRowComponent;
import com.akjostudios.acsp.bot.discord.common.component.BotButtonComponent;
import com.akjostudios.acsp.bot.discord.common.component.BotComponent;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommand;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessage;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessageEmbed;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannel;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannelCategory;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerRole;
import com.akjostudios.acsp.bot.discord.service.*;
import com.akjostudios.acsp.common.api.ExternalServiceClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"unused", "UnusedReturnValue", "java:S4968"})
public class BotCommandInteractionContext implements IBotCommandContext {
    private static final String GOTO_PRIVATE_MESSAGE_COMPONENT = "goto-private-message";
    private static final String PRIVATE_MESSAGE_ERROR_TITLE = "$error.private_message.title$";
    private static final String PRIVATE_MESSAGE_ERROR_DESCRIPTION = "$error.private_message.description$";

    @Getter
    private final String commandName;

    private final Option<String> subcommandName;

    @Getter
    private final String interactionId;

    private final GenericComponentInteractionCreateEvent event;

    private final long executionId;

    private final long originalMessageId;

    private final long originalChannelId;

    private final long originalAuthorId;

    private DiscordMessageService discordMessageService;
    private BotPrimitiveService botPrimitiveService;

    private BotCommandContext commandContext;

    private boolean replied = false;

    /**
     * @apiNote Should not be called by the command implementation.
     */
    public BotCommandContext initialize(
            ApplicationContext applicationContext,
            BotDefinitionService botDefinitionService,
            DiscordMessageService discordMessageService,
            BotErrorMessageService errorMessageService,
            BotPrimitiveService botPrimitiveService,
            BotLayoutService botLayoutService,
            ObjectMapper objectMapper
    ) {
        this.discordMessageService = discordMessageService;
        this.botPrimitiveService = botPrimitiveService;

        this.commandContext = new BotCommandContext(
                commandName,
                subcommandName,
                null
        );
        this.commandContext.initialize(
                applicationContext,
                botDefinitionService,
                discordMessageService,
                errorMessageService,
                botPrimitiveService,
                botLayoutService,
                objectMapper
        );
        this.commandContext.setExecutionId(executionId);

        return this.commandContext;
    }

    /**
     * @apiNote Should not be called by the command implementation.
     */
    @Override
    public void setArguments(List<? extends BotCommandArgument<?>> arguments) {
        this.commandContext.setArguments(arguments);
    }

    @Override
    public @NotNull Option<BotConfigCommand> getDefinition() {
        return commandContext.getDefinition();
    }

    @Override
    public @NotNull Option<BotConfigCommand.Subcommand> getSubcommandDefinition() {
        return commandContext.getSubcommandDefinition();
    }

    @Override
    public boolean isSubcommand() {
        return commandContext.isSubcommand();
    }

    @Override
    public Option<String> getSubcommandName() {
        return commandContext.getSubcommandName();
    }

    @Override
    public @NotNull List<BotConfigCommand.Argument> getArgumentDefinitions() {
        return commandContext.getArgumentDefinitions();
    }

    @Override
    public Mono<Map<String, Object>> getCommandData() {
        return commandContext.getCommandData();
    }

    @Override
    public void setCommandData(Map<String, Object> commandData) {
        commandContext.setCommandData(commandData);
    }

    @Override
    public <T> @NotNull Option<T> getArgument(@NotNull String id, @NotNull Class<T> type) {
        return commandContext.getArgument(id, type);
    }

    @Override
    public @NotNull Map<String, String> getArgumentMap() {
        return commandContext.getArgumentMap();
    }

    @Override
    public @NotNull Try<BotConfigMessage> getMessage(
            @NotNull String label,
            String@NotNull... placeholders
    ) {
        return commandContext.getMessage(label, placeholders);
    }

    @Override
    public @NotNull Try<BotConfigMessage> getMessage(
            @NotNull String label,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    ) {
        return commandContext.getMessage(label, labelPlaceholders, placeholders);
    }

    @Override
    public @NotNull Try<BotConfigMessage> getErrorMessage(
            @NotNull String errorTitle,
            @NotNull String errorDescription
    ) {
        return commandContext.getErrorMessage(errorTitle, errorDescription);
    }

    @Override
    public @NotNull Try<BotConfigMessage> getErrorMessage(
            @NotNull String errorTitle,
            @NotNull String errorDescription,
            @NotNull List<String> placeholders
    ) {
        return commandContext.getErrorMessage(errorTitle, errorDescription, placeholders);
    }

    @Override
    public @NotNull Try<BotConfigMessage> getInternalErrorMessage(
            @NotNull String errorMessage
    ) {
        return commandContext.getInternalErrorMessage(errorMessage);
    }

    @Override
    public @NotNull Option<BotConfigMessageEmbed.Field> getField(
            @NotNull String label,
            String@NotNull... placeholders
    ) {
        return commandContext.getField(label, placeholders);
    }

    @Override
    public @NotNull Option<BotConfigMessageEmbed.Field> getField(
            @NotNull String label,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    ) {
        return commandContext.getField(label, labelPlaceholders, placeholders);
    }

    @Override
    public <T extends BotComponent> @NotNull Option<T> getComponent(
            @NotNull String label,
            String@NotNull... placeholders
    ) {
        return commandContext.getComponent(label, placeholders);
    }

    @Override
    public <T extends BotComponent> @NotNull Option<T> getComponent(
            @NotNull String label,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    ) {
        return commandContext.getComponent(label, labelPlaceholders, placeholders);
    }

    @Override
    public @NotNull Try<BotConfigMessage> injectFields(
            @NotNull Try<BotConfigMessage> message,
            Option<Integer> index,
            List<Option<BotConfigMessageEmbed.Field>> fields
    ) {
        return commandContext.injectFields(message, index, fields);
    }

    @Override
    public @NotNull Try<BotConfigMessage> injectComponents(
            @NotNull Try<BotConfigMessage> message,
            List<Option<BotActionRowComponent>> rowComponents
    ) {
        return commandContext.injectComponents(message, rowComponents);
    }

    @Override
    public @NotNull Option<BotActionRowComponent> createActionRow(
            @NotNull List<Option<? extends BotComponent>> components
    ) {
        return commandContext.createActionRow(components);
    }

    @Override
    public @NotNull Option<BotButtonComponent> createButton(
            @NotNull String label,
            @NotNull Option<String> emoji,
            @NotNull String url,
            boolean disabled
    ) {
        return commandContext.createButton(label, emoji, url, disabled);
    }

    @Override
    public @NotNull Option<BotButtonComponent> createButton(
            @NotNull String interactionId,
            @NotNull String label,
            @NotNull BotButtonComponent.Style style,
            @NotNull Option<String> emoji,
            boolean disabled
    ) {
        return commandContext.createButton(interactionId, label, style, emoji, disabled);
    }

    public @NotNull Try<ReplyCallbackAction> reply(
            @NotNull String message,
            boolean ephemeral
    ) {
        return Try.of(() -> discordMessageService.createMessage(message))
                .filter(msg -> !replied)
                .map(msg -> event.reply(msg).setEphemeral(ephemeral))
                .onSuccess(action -> {
                    replied = true;
                    action.queue();
                }).onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err));
    }

    public @NotNull Try<ReplyCallbackAction> reply(
            @NotNull String message,
            @NotNull List<Option<BotActionRowComponent>> components,
            boolean ephemeral
    ) {
        return Try.of(() -> discordMessageService.createMessage(message, components))
                .filter(msg -> !replied)
                .map(msg -> event.reply(msg).setEphemeral(ephemeral))
                .onSuccess(action -> {
                    replied = true;
                    action.queue();
                }).onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err));
    }

    public @NotNull Try<ReplyCallbackAction> reply(
            @NotNull BotConfigMessage message,
            boolean ephemeral
    ) {
        return Try.of(() -> discordMessageService.createMessage(message))
                .filter(msg -> !replied)
                .map(msg -> event.reply(msg).setEphemeral(ephemeral))
                .onSuccess(action -> {
                    replied = true;
                    action.queue();
                }).onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err));
    }

    public @NotNull Try<MessageEditCallbackAction> editReply(
            @NotNull String message
    ) {
        return Try.of(() -> discordMessageService.editMessage(message))
                .filter(msg -> !replied)
                .map(event::editMessage)
                .onSuccess(action -> {
                    replied = true;
                    action.queue();
                }).onFailure(err -> log.error(DiscordMessageService.MESSAGE_EDIT_ERROR, err));
    }

    public @NotNull Try<MessageEditCallbackAction> editReply(
            @NotNull String message,
            @NotNull List<Option<BotActionRowComponent>> components
    ) {
        return Try.of(() -> discordMessageService.editMessage(message, components))
                .filter(msg -> !replied)
                .map(event::editMessage)
                .onSuccess(action -> {
                    replied = true;
                    action.queue();
                }).onFailure(err -> log.error(DiscordMessageService.MESSAGE_EDIT_ERROR, err));
    }

    public @NotNull Try<MessageEditCallbackAction> editReply(
            @NotNull BotConfigMessage message
    ) {
        return Try.of(() -> discordMessageService.editMessage(message))
                .filter(msg -> !replied)
                .map(event::editMessage)
                .onSuccess(action -> {
                    replied = true;
                    action.queue();
                }).onFailure(err -> log.error(DiscordMessageService.MESSAGE_EDIT_ERROR, err));
    }

    public @NotNull Try<InteractionHook> deferReply(boolean ephemeral) {
        return Try.of(() -> event.deferReply(ephemeral))
                .map(__ -> event.getHook())
                .onSuccess(hook -> replied = true)
                .onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err));
    }

    public @NotNull Try<InteractionHook> deferEdit() {
        return Try.of(event::deferEdit)
                .map(__ -> event.getHook())
                .onSuccess(hook -> replied = true)
                .onFailure(err -> log.error(DiscordMessageService.MESSAGE_EDIT_ERROR, err));
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T extends MessageCreateRequest<T>> Try<T> answerOrReply(
            @NotNull String message
    ) {
        return (Try<T>) reply(message, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T extends MessageCreateRequest<T>> Try<T> answerOrReply(
            @NotNull String message,
            @NotNull List<Option<BotActionRowComponent>> components
    ) {
        return (Try<T>) reply(message, components, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T extends MessageCreateRequest<T>> Try<T> answerOrReply(
            @NotNull Try<BotConfigMessage> message
    ) {
        return (Try<T>) reply(message.getOrElseThrow(), false);
    }

    @Override
    public @NotNull Try<MessageCreateAction> sendMessage(String message) {
        return getOriginalServerChannel().flatMap(
                channel -> sendMessage(message, channel)
                        .onFailure(ex -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, ex))
                        .toOption()
        ).toTry();
    }

    @Override
    public @NotNull Try<MessageCreateAction> sendMessage(
            String message,
            @NotNull BotConfigServerChannel channel
    ) {
        return botPrimitiveService.getChannel(event, channel).toTry().flatMap(
                textChannel -> Try.of(
                        () -> discordMessageService.createMessage(message)
                ).map(textChannel::sendMessage)
        ).onSuccess(RestAction::queue).onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err));
    }

    /**
     * Be aware that the components don't support further interactions with the user. (Link Buttons are supported)
     */
    @Override
    public @NotNull Try<MessageCreateAction> sendMessage(
            String message,
            List<Option<BotActionRowComponent>> components
    ) {
        return getOriginalServerChannel().flatMap(
                channel -> sendMessage(message, channel, components)
                        .onFailure(ex -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, ex))
                        .toOption()
        ).toTry();
    }

    /**
     * Be aware that the components don't support further interactions with the user. (Link Buttons are supported)
     */
    @Override
    public @NotNull Try<MessageCreateAction> sendMessage(
            String message,
            @NotNull BotConfigServerChannel channel,
            @NotNull List<Option<BotActionRowComponent>> components
    ) {
        return botPrimitiveService.getChannel(event, channel).toTry().flatMap(
                textChannel -> Try.of(
                        () -> discordMessageService.createMessage(message, components)
                ).map(textChannel::sendMessage)
        ).onSuccess(RestAction::queue).onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err));
    }

    /**
     * Be aware that the components don't support further interactions with the user. (Link Buttons are supported)
     */
    @Override
    public @NotNull Try<MessageCreateAction> sendMessage(
            @NotNull Try<BotConfigMessage> message
    ) {
        return getOriginalServerChannel().flatMap(
                channel -> sendMessage(message, channel)
                        .onFailure(ex -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, ex))
                        .toOption()
        ).toTry();
    }

    /**
     * Be aware that the components don't support further interactions with the user. (Link Buttons are supported)
     */
    @Override
    public @NotNull Try<MessageCreateAction> sendMessage(
            @NotNull Try<BotConfigMessage> message,
            @NotNull BotConfigServerChannel channel
    ) {
        return botPrimitiveService.getChannel(event, channel).toTry().flatMap(
                textChannel -> message.map(discordMessageService::createMessage).map(textChannel::sendMessage)
        ).onSuccess(RestAction::queue).onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err));
    }

    @Override
    public @NotNull Try<MessageCreateAction> sendPrivateMessage(String message) {
        return getOriginalAuthor().getUser().openPrivateChannel().map(
                privateChannel -> Try.of(() -> discordMessageService.createMessage(message))
                        .map(privateChannel::sendMessage)
                        .onSuccess(RestAction::queue)
                        .onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err))
        ).complete();
    }

    @Override
    public @NotNull Try<MessageCreateAction> sendPrivateMessage(
            String message,
            @NotNull List<Option<BotActionRowComponent>> components
    ) {
        return getOriginalAuthor().getUser().openPrivateChannel().map(
                privateChannel -> Try.of(() -> discordMessageService.createMessage(message, components))
                        .map(privateChannel::sendMessage)
                        .onSuccess(RestAction::queue)
                        .onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err))
        ).complete();
    }

    @Override
    public @NotNull Try<MessageCreateAction> sendPrivateMessage(
            @NotNull Try<BotConfigMessage> message
    ) {
        return getOriginalAuthor().getUser().openPrivateChannel().map(
                privateChannel -> message.map(discordMessageService::createMessage)
                        .map(privateChannel::sendMessage)
                        .onSuccess(RestAction::queue)
                        .onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err))
        ).complete();
    }

    @Override
    public @NotNull Try<MessageEditAction> editMessage(
            @NotNull Message message,
            @NotNull String newMessage
    ) {
        return Try.of(() -> message.editMessage(discordMessageService.editMessage(newMessage)))
                .onSuccess(RestAction::queue)
                .onFailure(err -> log.error(DiscordMessageService.MESSAGE_EDIT_ERROR, err));
    }

    @Override
    public @NotNull Try<MessageEditAction> editMessage(
            @NotNull Message message,
            @NotNull String newMessage,
            @NotNull List<Option<BotActionRowComponent>> components
    ) {
        return Try.of(() -> message.editMessage(discordMessageService.editMessage(newMessage, components)))
                .onSuccess(RestAction::queue)
                .onFailure(err -> log.error(DiscordMessageService.MESSAGE_EDIT_ERROR, err));
    }

    @Override
    public @NotNull Try<MessageEditAction> editMessage(
            @NotNull Message message,
            @NotNull Try<BotConfigMessage> newMessage
    ) {
        return newMessage.map(discordMessageService::editMessage)
                .map(message::editMessage)
                .onSuccess(RestAction::queue)
                .onFailure(err -> log.error(DiscordMessageService.MESSAGE_EDIT_ERROR, err));
    }

    @Override
    public boolean memberHasRole(
            @NotNull Member member,
            @NotNull BotConfigServerRole role
    ) {
        return commandContext.memberHasRole(member, role);
    }

    @Override
    public boolean memberInChannel(
            @NotNull Member member,
            @NotNull BotConfigServerChannel channel
    ) {
        return commandContext.memberInChannel(member, channel);
    }

    @Override
    public @NotNull Map<BotConfigServerRole, Boolean> memberMatchesRoles(
            @NotNull Member member,
            @NotNull Collection<BotConfigCommand.RolePermission> requiredRoles
    ) {
        return commandContext.memberMatchesRoles(member, requiredRoles);
    }

    @Override
    public @NotNull Member getOriginalAuthor() {
        return getOriginalChannel().getGuild().retrieveMemberById(originalAuthorId).complete();
    }

    @Override
    public @NotNull Message getOriginalMessage() {
        return getOriginalChannel().retrieveMessageById(originalMessageId).complete();
    }

    @Override
    public @NotNull TextChannel getOriginalChannel() {
        return Option.of(event.getJDA().getTextChannelById(originalChannelId))
                .getOrElseThrow();
    }

    @Override
    public @NotNull String getJumpUrl() {
        return getOriginalMessage().getJumpUrl();
    }

    public @NotNull String getResponseJumpUrl() {
        return event.getMessage().getJumpUrl();
    }

    @Override
    public @NotNull Option<User> getUser(@NotNull String userId) {
        return botPrimitiveService.getUser(event, userId);
    }

    @Override
    public @NotNull Option<Member> getMember(@NotNull String memberId) {
        return botPrimitiveService.getMember(event, memberId);
    }

    @Override
    public @NotNull Option<Role> getRole(@NotNull String roleId) {
        return botPrimitiveService.getRole(event, roleId);
    }

    @Override
    public @NotNull Option<TextChannel> getChannel(@NotNull String channelId) {
        return botPrimitiveService.getChannel(event, channelId);
    }

    @Override
    public @NotNull Option<Category> getCategory(@NotNull String categoryId) {
        return botPrimitiveService.getCategory(event, categoryId);
    }

    @Override
    public @NotNull Option<Emoji> getEmoji(@NotNull String emojiId) {
        return botPrimitiveService.getEmoji(event, emojiId);
    }

    @Override
    public @NotNull Option<BotConfigServerRole> getServerRole(@NotNull Long roleId) {
        return commandContext.getServerRole(roleId);
    }

    @Override
    public @NotNull Option<BotConfigServerChannel> getServerChannel(@NotNull Long channelId) {
        return commandContext.getServerChannel(channelId);
    }

    @Override
    public @NotNull Option<BotConfigServerChannel> getOriginalServerChannel() {
        return getServerChannel(getOriginalChannel().getIdLong());
    }

    @Override
    public @NotNull Option<BotConfigServerChannelCategory> getServerCategory(@NotNull Long categoryId) {
        return commandContext.getServerCategory(categoryId);
    }

    @Override
    public @NotNull ObjectMapper getMapper() {
        return commandContext.getMapper();
    }

    @Override
    @SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression"})
    public long getExecutionId() {
        return executionId;
    }

    @Override
    public @NotNull ExternalServiceClient getBackendClient() {
        return commandContext.getBackendClient();
    }

    @Override
    public <T> @NotNull T getBean(
            @NotNull Class<T> clazz
    ) {
        return commandContext.getBean(clazz);
    }

    @Override
    public <T> @NotNull T getBean(
            @NotNull String name,
            @NotNull Class<T> clazz
    ) {
        return commandContext.getBean(name, clazz);
    }

    public <T> @NotNull List<T> getBeans(
            @NotNull Class<T> clazz
    ) {
        return commandContext.getBeans(clazz);
    }

    @Override
    public @NotNull Option<String> getProperty(
            @NotNull String key
    ) {
        return commandContext.getProperty(key);
    }

    @Override
    public @NotNull String getProperty(
            @NotNull String key,
            @NotNull String defaultValue
    ) {
        return commandContext.getProperty(key, defaultValue);
    }

    @Override
    public <T> @NotNull Option<T> getProperty(
            @NotNull String key,
            @NotNull Class<T> targetType
    ) {
        return commandContext.getProperty(key, targetType);
    }

    @Override
    public <T> @NotNull T getProperty(
            @NotNull String key,
            @NotNull Class<T> targetType,
            @NotNull T defaultValue
    ) {
        return commandContext.getProperty(key, targetType, defaultValue);
    }
}