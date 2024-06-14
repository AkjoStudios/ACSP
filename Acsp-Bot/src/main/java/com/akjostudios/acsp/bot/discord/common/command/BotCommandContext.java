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
import com.akjostudios.acsp.common.dto.SimpleExternalServiceResponse;
import com.akjostudios.acsp.common.dto.bot.log.command.CommandExecutionDataRequest;
import com.akjostudios.acsp.common.dto.bot.log.command.CommandExecutionGetResponse;
import com.akjostudios.acsp.common.dto.bot.log.command.CommandResponseCreateRequest;
import com.akjostudios.acsp.common.dto.bot.log.command.CommandResponseCreateResponse;
import com.akjostudios.acsp.common.model.bot.log.command.CommandExecutionDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"unused", "UnusedReturnValue", "java:S4968"})
public class BotCommandContext implements IBotCommandContext {
    private static final String GOTO_PRIVATE_MESSAGE_COMPONENT = "goto-private-message";
    private static final String PRIVATE_MESSAGE_ERROR_TITLE = "$error.private_message.title$";
    private static final String PRIVATE_MESSAGE_ERROR_DESCRIPTION = "$error.private_message.description$";

    @Getter private final String name;

    private final Option<String> subcommand;

    private final MessageReceivedEvent event;

    /**
     * @apiNote Should not be called by the command implementation.
     */
    @Setter(onMethod_={@Override})
    private List<? extends BotCommandArgument<?>> arguments;

    /**
     * @apiNote Should not be called by the command implementation.
     */
    @Setter
    private long executionId;

    private ApplicationContext applicationContext;
    private BotDefinitionService botDefinitionService;
    private DiscordMessageService discordMessageService;
    private BotErrorMessageService errorMessageService;
    private BotPrimitiveService botPrimitiveService;
    private BotLayoutService botLayoutService;
    private ObjectMapper objectMapper;

    /**
     * @apiNote Should not be called by the command implementation.
     */
    public void initialize(
            ApplicationContext applicationContext,
            BotDefinitionService botDefinitionService,
            DiscordMessageService discordMessageService,
            BotErrorMessageService errorMessageService,
            BotPrimitiveService botPrimitiveService,
            BotLayoutService botLayoutService,
            ObjectMapper objectMapper
    ) {
        this.applicationContext = applicationContext;
        this.botDefinitionService = botDefinitionService;
        this.discordMessageService = discordMessageService;
        this.errorMessageService = errorMessageService;
        this.botPrimitiveService = botPrimitiveService;
        this.botLayoutService = botLayoutService;
        this.objectMapper = objectMapper;
    }

    @Override
    public @NotNull Option<BotConfigCommand> getDefinition() {
        return botDefinitionService.getCommandDefinition(name)
                .filter(BotConfigCommand::isEnabled);
    }

    @Override
    public @NotNull Option<BotConfigCommand.Subcommand> getSubcommandDefinition() {
        return getDefinition().map(BotConfigCommand::getSubcommands)
                .filter(BotConfigCommand.Subcommands::isAvailable)
                .map(BotConfigCommand.Subcommands::getCommands)
                .flatMap(subcommands -> Option.from(subcommands.stream()
                        .filter(subcommandP -> subcommandP.getName().equals(subcommand.getOrElseNull()))
                        .findFirst())
                );
    }

    @Override
    public boolean isSubcommand() {
        return subcommand.isPresent();
    }

    @Override
    public Option<String> getSubcommandName() {
        return getSubcommandDefinition().map(BotConfigCommand.Subcommand::getName);
    }

    @Override
    public @NotNull List<BotConfigCommand.Argument> getArgumentDefinitions() {
        return isSubcommand()
                ? getSubcommandDefinition().flatMap(subcommandP -> Option.of(subcommandP::getArguments)).getOrElse(List.of())
                : getDefinition().flatMap(subcommandP -> Option.of(subcommandP::getArguments)).getOrElse(List.of());
    }

    @Override
    public Mono<Map<String, Object>> getCommandData() {
        return getBackendClient().exchangeGet("/api/bot/log/command/execution/exid/" + executionId, CommandExecutionGetResponse.class)
                .doOnError(error -> sendMessage(
                        getInternalErrorMessage("Failed to get command data! " + error.getMessage())
                )).map(CommandExecutionGetResponse::result)
                .map(CommandExecutionDao::commandData);
    }

    @Override
    public void setCommandData(Map<String, Object> commandData) {
        getBackendClient().exchangePut(
                "/api/bot/log/command/execution/" + executionId + "/data",
                new CommandExecutionDataRequest(commandData),
                SimpleExternalServiceResponse.class
        ).map(SimpleExternalServiceResponse::status)
                .doOnError(error -> sendMessage(
                        getInternalErrorMessage("Failed to set command data! " + error.getMessage())
                )).subscribe();
    }

    @Override
    public <T> @NotNull Option<T> getArgument(@NotNull String id, @NotNull Class<T> type) {
        return Option.from(arguments.stream()
                        .filter(argument -> argument.id().equals(id))
                        .findFirst())
                .map(argument -> type.cast(argument.value()));
    }

    @Override
    public @NotNull Map<String, String> getArgumentMap() {
        return arguments.stream()
                .collect(Collectors.toMap(BotCommandArgument::id, argument -> argument.value().toString()));
    }

    @Override
    public @NotNull Try<BotConfigMessage> getMessage(
            @NotNull String label,
            String@NotNull... placeholders
    ) {
        return botDefinitionService.getMessageDefinition(label, placeholders);
    }

    @Override
    public @NotNull Try<BotConfigMessage> getMessage(
            @NotNull String label,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    ) {
        return botDefinitionService.getMessageDefinition(label, labelPlaceholders, placeholders);
    }

    @Override
    public @NotNull Try<BotConfigMessage> getErrorMessage(
            @NotNull String errorTitle,
            @NotNull String errorDescription
    ) {
        return errorMessageService.getErrorMessage(errorTitle, errorDescription);
    }

    @Override
    public @NotNull Try<BotConfigMessage> getErrorMessage(
            @NotNull String errorTitle,
            @NotNull String errorDescription,
            @NotNull List<String> placeholders
    ) {
        return errorMessageService.getErrorMessage(errorTitle, errorDescription, placeholders);
    }

    @Override
    public @NotNull Try<BotConfigMessage> getInternalErrorMessage(
            @NotNull String errorMessage
    ) {
        return errorMessageService.getInternalErrorMessage(errorMessage);
    }

    @Override
    public @NotNull Option<BotConfigMessageEmbed.Field> getField(
            @NotNull String label,
            String@NotNull... placeholders
    ) {
        return botDefinitionService.getFieldDefinition(label, placeholders);
    }

    @Override
    public @NotNull Option<BotConfigMessageEmbed.Field> getField(
            @NotNull String label,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    ) {
        return botDefinitionService.getFieldDefinition(label, labelPlaceholders, placeholders);
    }

    @Override
    public <T extends BotComponent> @NotNull Option<T> getComponent(
            @NotNull String label,
            String@NotNull... placeholders
    ) {
        return botDefinitionService.getComponentDefinition(label, placeholders);
    }

    @Override
    public <T extends BotComponent> @NotNull Option<T> getComponent(
            @NotNull String label,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    ) {
        return botDefinitionService.getComponentDefinition(label, labelPlaceholders, placeholders);
    }

    @Override
    public @NotNull Try<BotConfigMessage> injectFields(
            @NotNull Try<BotConfigMessage> message,
            Option<Integer> index,
            List<Option<BotConfigMessageEmbed.Field>> fields
    ) {
        return discordMessageService.injectFields(message, index, fields);
    }

    @Override
    public @NotNull Try<BotConfigMessage> injectComponents(
            @NotNull Try<BotConfigMessage> message,
            List<Option<BotActionRowComponent>> rowComponents
    ) {
        return discordMessageService.injectComponents(message, rowComponents);
    }

    @Override
    public @NotNull Option<BotActionRowComponent> createActionRow(
            @NotNull List<Option<? extends BotComponent>> components
    ) {
        return discordMessageService.createActionRow(components);
    }

    @Override
    public @NotNull Option<BotButtonComponent> createButton(
            @NotNull String label,
            @NotNull Option<String> emoji,
            @NotNull String url,
            boolean disabled
    ) {
        return discordMessageService.createButton(label, emoji, url, disabled);
    }

    @Override
    public @NotNull Option<BotButtonComponent> createButton(
            @NotNull String interactionId,
            @NotNull String label,
            @NotNull BotButtonComponent.Style style,
            @NotNull Option<String> emoji,
            boolean disabled
    ) {
        return discordMessageService.createButton(interactionId, label, style, emoji, disabled);
    }

    public @NotNull Try<MessageCreateAction> answer(String message) {
        return Try.of(() -> discordMessageService.createMessage(message))
                .map(event.getChannel()::sendMessage)
                .onSuccess(this::handleAnswerAction)
                .onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err));
    }

    public @NotNull Try<MessageCreateAction> answer(
            String message,
            @NotNull List<Option<BotActionRowComponent>> components
    ) {
        return Try.of(() -> discordMessageService.createMessage(message, components))
                .map(event.getChannel()::sendMessage)
                .onSuccess(this::handleAnswerAction)
                .onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err));
    }

    public @NotNull Try<MessageCreateAction> answer(@NotNull Try<BotConfigMessage> message) {
        return message.map(discordMessageService::createMessage)
                .map(event.getChannel()::sendMessage)
                .onSuccess(this::handleAnswerAction)
                .onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err));
    }

    private void handleAnswerAction(@NotNull MessageCreateAction action) {
        action.setMessageReference(event.getMessageId())
                .map(ISnowflake::getIdLong)
                .onSuccess(id -> getBackendClient().exchangePost(
                        "/api/bot/log/command/execution/" + executionId + "/response",
                        new CommandResponseCreateRequest(
                                id, event.getChannel().getIdLong()
                        ), CommandResponseCreateResponse.class
                ).doOnError(error -> sendMessage(
                        getInternalErrorMessage("Failed to create response! " + error.getMessage())
                )).subscribe()).queue();
    }

    public @NotNull Try<MessageCreateAction> answerPrivately(String message) {
        return event.getAuthor().openPrivateChannel().map(
                privateChannel -> Try.of(() -> discordMessageService.createMessage(message))
                        .map(privateChannel::sendMessage)
                        .flatMap(action -> action
                                .map(Message::getJumpUrl)
                                .map(jumpUrl -> answer(
                                        "",
                                        List.of(createActionRow(List.of(
                                                getComponent(GOTO_PRIVATE_MESSAGE_COMPONENT, jumpUrl))
                                        ))
                                )).complete()
                        )
        ).onErrorMap(err -> sendMessage(
                getErrorMessage(PRIVATE_MESSAGE_ERROR_TITLE, PRIVATE_MESSAGE_ERROR_DESCRIPTION))
        ).complete();
    }

    public @NotNull Try<MessageCreateAction> answerPrivately(
            String message,
            @NotNull List<Option<BotActionRowComponent>> components
    ) {
        return event.getAuthor().openPrivateChannel().map(
                privateChannel -> Try.of(() -> discordMessageService.createMessage(message, components))
                        .map(privateChannel::sendMessage)
                        .flatMap(action -> action
                                .map(Message::getJumpUrl)
                                .map(jumpUrl -> answer(
                                        "",
                                        List.of(createActionRow(List.of(
                                                getComponent(GOTO_PRIVATE_MESSAGE_COMPONENT, jumpUrl))
                                        ))
                                )).complete()
                        )
        ).onErrorMap(err -> sendMessage(
                getErrorMessage(PRIVATE_MESSAGE_ERROR_TITLE, PRIVATE_MESSAGE_ERROR_DESCRIPTION))
        ).complete();
    }

    public @NotNull Try<MessageCreateAction> answerPrivately(@NotNull Try<BotConfigMessage> message) {
        return event.getAuthor().openPrivateChannel().map(
                privateChannel -> message.map(discordMessageService::createMessage)
                        .map(privateChannel::sendMessage)
                        .flatMap(action -> action
                                .map(Message::getJumpUrl)
                                .map(jumpUrl -> answer(
                                        "",
                                        List.of(createActionRow(List.of(
                                                getComponent(GOTO_PRIVATE_MESSAGE_COMPONENT, jumpUrl))
                                        ))
                                )).complete()
                        )
        ).onErrorMap(err -> sendMessage(
                getErrorMessage(PRIVATE_MESSAGE_ERROR_TITLE, PRIVATE_MESSAGE_ERROR_DESCRIPTION))
        ).complete();
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
     * Be aware that the components don't support interactions with the user. (Link Buttons are supported)
     * Use the {@link #answer(String, List)} method instead.
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
     * Be aware that the components don't support interactions with the user. (Link Buttons are supported)
     * Use the {@link #answer(String, List)} method instead.
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
     * Be aware that the components don't support interactions with the user. (Link Buttons are supported)
     * Use the {@link #answer(Try)} method instead.
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
     * Be aware that the components don't support interactions with the user. (Link Buttons are supported)
     * Use the {@link #answer(Try)} method instead.
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

    public @NotNull Try<MessageCreateAction> sendPrivateMessage(String message) {
        return event.getAuthor().openPrivateChannel().map(
                privateChannel -> Try.of(() -> discordMessageService.createMessage(message))
                        .map(privateChannel::sendMessage)
                        .onSuccess(RestAction::queue)
                        .onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err))
        ).complete();
    }

    public @NotNull Try<MessageCreateAction> sendPrivateMessage(
            String message,
            @NotNull List<Option<BotActionRowComponent>> components
    ) {
        return event.getAuthor().openPrivateChannel().map(
                privateChannel -> Try.of(() -> discordMessageService.createMessage(message, components))
                        .map(privateChannel::sendMessage)
                        .onSuccess(RestAction::queue)
                        .onFailure(err -> log.error(DiscordMessageService.MESSAGE_SEND_ERROR, err))
        ).complete();
    }

    public @NotNull Try<MessageCreateAction> sendPrivateMessage(
            @NotNull Try<BotConfigMessage> message
    ) {
        return event.getAuthor().openPrivateChannel().map(
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
        return botPrimitiveService.memberHasRole(member, role);
    }

    @Override
    public boolean memberInChannel(
            @NotNull Member member,
            @NotNull BotConfigServerChannel channel
    ) {
        return botPrimitiveService.memberInChannel(member, channel);
    }

    @Override
    public @NotNull Map<BotConfigServerRole, Boolean> memberMatchesRoles(
            @NotNull Member member,
            @NotNull Collection<BotConfigCommand.RolePermission> requiredRoles
    ) {
        return requiredRoles.stream()
                .map(rolePermission -> Map.entry(rolePermission.getRole(), switch (rolePermission.getType()) {
                    case SIMPLE -> memberHasRole(member, rolePermission.getRole());
                    case EXCLUSION -> !memberHasRole(member, rolePermission.getRole());
                })).collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> existing && replacement
                ));
    }

    @Override
    public @NotNull Member getOriginalAuthor() {
        return Option.of(event.getMember()).getOrElseThrow();
    }

    @Override
    public @NotNull Message getOriginalMessage() {
        return event.getMessage();
    }

    @Override
    public @NotNull TextChannel getOriginalChannel() {
        return event.getChannel().asTextChannel();
    }

    @Override
    public @NotNull String getJumpUrl() {
        return event.getMessage().getJumpUrl();
    }

    @Override
    public @NotNull Option<User> getUser(@NotNull String userId) {
        return botPrimitiveService.getUser(event, userId);
    }

    @Override
    public @NotNull Option<Member> getMember(@NotNull String userId) {
        return botPrimitiveService.getMember(event, userId);
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
        return botLayoutService.getServerLayout().flatMap(server -> botLayoutService.getRole(server, roleId));
    }

    @Override
    public @NotNull Option<BotConfigServerChannel> getServerChannel(@NotNull Long channelId) {
        return botLayoutService.getServerLayout().flatMap(server -> botLayoutService.getChannel(server, channelId));
    }

    @Override
    public @NotNull Option<BotConfigServerChannel> getOriginalServerChannel() {
        return getServerChannel(getOriginalChannel().getIdLong());
    }

    @Override
    public @NotNull Option<BotConfigServerChannelCategory> getServerCategory(@NotNull Long categoryId) {
        return botLayoutService.getServerLayout().flatMap(server -> botLayoutService.getCategory(server, categoryId));
    }

    @Override
    public @NotNull ObjectMapper getMapper() {
        return objectMapper;
    }

    @Override
    @SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression"})
    public long getExecutionId() {
        return executionId;
    }

    @Override
    public @NotNull ExternalServiceClient getBackendClient() {
        return getBean("client.service.backend", ExternalServiceClient.class);
    }

    @Override
    public <T> @NotNull T getBean(
            @NotNull Class<T> clazz
    ) {
        return applicationContext.getBean(clazz);
    }

    @Override
    public <T> @NotNull T getBean(
            @NotNull String name,
            @NotNull Class<T> clazz
    ) {
        return applicationContext.getBean(name, clazz);
    }

    public <T> @NotNull List<T> getBeans(
            @NotNull Class<T> clazz
    ) {
        return applicationContext.getBeansOfType(clazz).values().stream().toList();
    }

    @Override
    public @NotNull Option<String> getProperty(
            @NotNull String key
    ) {
        return Option.of(applicationContext.getEnvironment().getProperty(key));
    }

    @Override
    public @NotNull String getProperty(
            @NotNull String key,
            @NotNull String defaultValue
    ) {
        return applicationContext.getEnvironment().getProperty(key, defaultValue);
    }

    @Override
    public <T> @NotNull Option<T> getProperty(
            @NotNull String key,
            @NotNull Class<T> targetType
    ) {
        return Option.of(applicationContext.getEnvironment().getProperty(key, targetType));
    }

    @Override
    public <T> @NotNull T getProperty(
            @NotNull String key,
            @NotNull Class<T> targetType,
            @NotNull T defaultValue
    ) {
        return applicationContext.getEnvironment().getProperty(key, targetType, defaultValue);
    }
}