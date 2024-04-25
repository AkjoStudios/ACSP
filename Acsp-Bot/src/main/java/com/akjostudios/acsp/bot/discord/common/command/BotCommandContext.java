package com.akjostudios.acsp.bot.discord.common.command;

import com.akjostudios.acsp.bot.discord.common.command.argument.BotCommandArgument;
import com.akjostudios.acsp.bot.discord.common.component.BotActionRowComponent;
import com.akjostudios.acsp.bot.discord.common.component.BotComponent;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigCommand;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessage;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigMessageEmbed;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannel;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerChannelCategory;
import com.akjostudios.acsp.bot.discord.config.layout.BotConfigServerRole;
import com.akjostudios.acsp.bot.discord.service.*;
import com.akjostudios.acsp.common.api.ExternalServiceClient;
import com.akjostudios.acsp.common.dto.bot.log.command.CommandResponseCreateRequest;
import com.akjostudios.acsp.common.dto.bot.log.command.CommandResponseCreateResponse;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"unused", "UnusedReturnValue", "java:S4968"})
public class BotCommandContext {
    private static final String GOTO_PRIVATE_MESSAGE_COMPONENT = "goto-private-message";
    private static final String PRIVATE_MESSAGE_ERROR_TITLE = "$error.private_message.title$";
    private static final String PRIVATE_MESSAGE_ERROR_DESCRIPTION = "$error.private_message.description$";
    private static final String MESSAGE_SEND_ERROR = "Failed to send message!";

    @Getter private final String name;

    private final Option<String> subcommand;

    private final MessageReceivedEvent event;

    /**
     * @apiNote Should not be called by the command implementation.
     */
    @Setter
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

    public boolean isSubcommand() {
        return subcommand.isPresent();
    }

    public Option<String> getSubcommandName() {
        return getSubcommandDefinition().map(BotConfigCommand.Subcommand::getName);
    }

    public @NotNull List<BotConfigCommand.Argument> getArgumentDefinitions() {
        return isSubcommand()
                ? getSubcommandDefinition().flatMap(subcommandP -> Option.of(subcommandP::getArguments)).getOrElse(List.of())
                : getDefinition().flatMap(subcommandP -> Option.of(subcommandP::getArguments)).getOrElse(List.of());
    }

    @SuppressWarnings("unchecked")
    public <T> @NotNull T getArgument(@NotNull String id, @Nullable T defaultValue) {
        return Option.from(arguments.stream()
                .filter(argument -> argument.id().equals(id))
                .findFirst())
                .map(argument -> (T) argument.value())
                .getOrElse(defaultValue);
    }

    public @NotNull Map<String, String> getArgumentMap() {
        return arguments.stream()
                .collect(Collectors.toMap(BotCommandArgument::id, argument -> argument.value().toString()));
    }

    public @NotNull Try<BotConfigMessage> getMessage(
            @NotNull String label,
            String@NotNull... placeholders
    ) {
        return botDefinitionService.getMessageDefinition(label, placeholders);
    }

    public @NotNull Try<BotConfigMessage> getMessage(
            @NotNull String label,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    ) {
        return botDefinitionService.getMessageDefinition(label, labelPlaceholders, placeholders);
    }

    public @NotNull Try<BotConfigMessage> getErrorMessage(
            @NotNull String errorTitle,
            @NotNull String errorDescription
    ) {
        return errorMessageService.getErrorMessage(errorTitle, errorDescription);
    }

    public Try<BotConfigMessage> getErrorMessage(
            @NotNull String errorTitle,
            @NotNull String errorDescription,
            @NotNull List<String> placeholders
    ) {
        return errorMessageService.getErrorMessage(errorTitle, errorDescription, placeholders);
    }

    public @NotNull Try<BotConfigMessage> getInternalErrorMessage(
            @NotNull String errorMessage
    ) {
        return errorMessageService.getInternalErrorMessage(errorMessage);
    }

    public @NotNull Option<BotConfigMessageEmbed.Field> getField(
            @NotNull String label,
            String@NotNull... placeholders
    ) {
        return botDefinitionService.getFieldDefinition(label, placeholders);
    }

    public @NotNull Option<BotConfigMessageEmbed.Field> getField(
            @NotNull String label,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    ) {
        return botDefinitionService.getFieldDefinition(label, labelPlaceholders, placeholders);
    }

    public <T extends BotComponent> @NotNull Option<T> getComponent(
            @NotNull String label,
            String@NotNull... placeholders
    ) {
        return botDefinitionService.getComponentDefinition(label, placeholders);
    }

    public <T extends BotComponent> @NotNull Option<T> getComponent(
            @NotNull String label,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    ) {
        return botDefinitionService.getComponentDefinition(label, labelPlaceholders, placeholders);
    }

    public @NotNull Try<BotConfigMessage> injectFields(
            @NotNull Try<BotConfigMessage> message,
            Option<Integer> index,
            List<Option<BotConfigMessageEmbed.Field>> fields
    ) {
        return discordMessageService.injectFields(message, index, fields);
    }

    public @NotNull Try<BotConfigMessage> injectComponents(
            @NotNull Try<BotConfigMessage> message,
            List<Option<BotActionRowComponent>> rowComponents
    ) {
        return discordMessageService.injectComponents(message, rowComponents);
    }

    public @NotNull Option<BotActionRowComponent> createActionRow(
            @NotNull List<Option<BotComponent>> components
    ) {
        return discordMessageService.createActionRow(components);
    }

    public @NotNull Try<MessageCreateAction> answer(String message) {
        return Try.of(() -> discordMessageService.createMessage(message))
                .map(event.getChannel()::sendMessage)
                .onSuccess(this::handleAnswerAction)
                .onFailure(err -> log.error(MESSAGE_SEND_ERROR, err));
    }

    public @NotNull Try<MessageCreateAction> answer(
            String message,
            @NotNull List<Option<BotActionRowComponent>> components
    ) {
        return Try.of(() -> discordMessageService.createMessage(message, components))
                .map(event.getChannel()::sendMessage)
                .onSuccess(this::handleAnswerAction)
                .onFailure(err -> log.error(MESSAGE_SEND_ERROR, err));
    }

    public @NotNull Try<MessageCreateAction> answer(@NotNull Try<BotConfigMessage> message) {
        return message.map(discordMessageService::createMessage)
                .map(event.getChannel()::sendMessage)
                .onSuccess(this::handleAnswerAction)
                .onFailure(err -> log.error(MESSAGE_SEND_ERROR, err));
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

    public @NotNull Try<MessageCreateAction> sendMessage(String message) {
        return getOriginalServerChannel().flatMap(
                channel -> sendMessage(message, channel)
                        .onFailure(ex -> log.error(MESSAGE_SEND_ERROR, ex))
                        .toOption()
        ).toTry();
    }

    public @NotNull Try<MessageCreateAction> sendMessage(String message, @NotNull BotConfigServerChannel channel) {
        return botPrimitiveService.getChannel(event, channel).toTry().flatMap(
                textChannel -> Try.of(
                        () -> discordMessageService.createMessage(message)
                ).map(textChannel::sendMessage)
        ).onSuccess(RestAction::queue).onFailure(err -> log.error(MESSAGE_SEND_ERROR, err));
    }

    /**
     * Be aware that the components don't support interactions with the user. (Link Buttons are supported)
     * Use the {@link #answer(String, List)} method instead.
     */
    public @NotNull Try<MessageCreateAction> sendMessage(
            String message,
            List<Option<BotActionRowComponent>> components
    ) {
        return getOriginalServerChannel().flatMap(
                channel -> sendMessage(message, channel, components)
                        .onFailure(ex -> log.error(MESSAGE_SEND_ERROR, ex))
                        .toOption()
        ).toTry();
    }

    /**
     * Be aware that the components don't support interactions with the user. (Link Buttons are supported)
     * Use the {@link #answer(String, List)} method instead.
     */
    public @NotNull Try<MessageCreateAction> sendMessage(
            String message,
            @NotNull BotConfigServerChannel channel,
            @NotNull List<Option<BotActionRowComponent>> components
    ) {
        return botPrimitiveService.getChannel(event, channel).toTry().flatMap(
                textChannel -> Try.of(
                        () -> discordMessageService.createMessage(message, components)
                ).map(textChannel::sendMessage)
        ).onSuccess(RestAction::queue).onFailure(err -> log.error(MESSAGE_SEND_ERROR, err));
    }

    /**
     * Be aware that the components don't support interactions with the user. (Link Buttons are supported)
     * Use the {@link #answer(Try)} method instead.
     */
    public @NotNull Try<MessageCreateAction> sendMessage(
            @NotNull Try<BotConfigMessage> message
    ) {
        return getOriginalServerChannel().flatMap(
                channel -> sendMessage(message, channel)
                        .onFailure(ex -> log.error(MESSAGE_SEND_ERROR, ex))
                        .toOption()
        ).toTry();
    }

    /**
     * Be aware that the components don't support interactions with the user. (Link Buttons are supported)
     * Use the {@link #answer(Try)} method instead.
     */
    public @NotNull Try<MessageCreateAction> sendMessage(@NotNull Try<BotConfigMessage> message, @NotNull BotConfigServerChannel channel) {
        return botPrimitiveService.getChannel(event, channel).toTry().flatMap(
                textChannel -> message.map(discordMessageService::createMessage).map(textChannel::sendMessage)
        ).onSuccess(RestAction::queue).onFailure(err -> log.error(MESSAGE_SEND_ERROR, err));
    }

    public @NotNull Try<MessageCreateAction> sendPrivateMessage(String message) {
        return event.getAuthor().openPrivateChannel().map(
                privateChannel -> Try.of(() -> discordMessageService.createMessage(message))
                        .map(privateChannel::sendMessage)
                        .onSuccess(RestAction::queue)
                        .onFailure(err -> log.error(MESSAGE_SEND_ERROR, err))
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
                        .onFailure(err -> log.error(MESSAGE_SEND_ERROR, err))
        ).complete();
    }

    public @NotNull Try<MessageCreateAction> sendPrivateMessage(@NotNull Try<BotConfigMessage> message) {
        return event.getAuthor().openPrivateChannel().map(
                privateChannel -> message.map(discordMessageService::createMessage)
                        .map(privateChannel::sendMessage)
                        .onSuccess(RestAction::queue)
                        .onFailure(err -> log.error(MESSAGE_SEND_ERROR, err))
        ).complete();
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

    public boolean memberHasRole(
            @NotNull Member member,
            @NotNull BotConfigServerRole role
    ) {
        return botPrimitiveService.memberHasRole(member, role);
    }

    public boolean memberInChannel(
            @NotNull Member member,
            @NotNull BotConfigServerChannel channel
    ) {
        return botPrimitiveService.memberInChannel(member, channel);
    }

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

    public @NotNull Member getOriginalAuthor() {
        return Option.of(event.getMember()).getOrElseThrow();
    }

    public @NotNull Message getOriginalMessage() {
        return event.getMessage();
    }

    public @NotNull TextChannel getOriginalChannel() {
        return event.getChannel().asTextChannel();
    }

    public @NotNull String getJumpUrl() {
        return event.getMessage().getJumpUrl();
    }

    public @NotNull Option<User> getUser(@NotNull String userId) {
        return botPrimitiveService.getUser(event, userId);
    }

    public @NotNull Option<Member> getMember(@NotNull String userId) {
        return botPrimitiveService.getMember(event, userId);
    }

    public @NotNull Option<Role> getRole(@NotNull String roleId) {
        return botPrimitiveService.getRole(event, roleId);
    }

    public @NotNull Option<TextChannel> getChannel(@NotNull String channelId) {
        return botPrimitiveService.getChannel(event, channelId);
    }

    public @NotNull Option<Category> getCategory(@NotNull String categoryId) {
        return botPrimitiveService.getCategory(event, categoryId);
    }

    public @NotNull Option<Emoji> getEmoji(@NotNull String emojiId) {
        return botPrimitiveService.getEmoji(event, emojiId);
    }

    public @NotNull Option<BotConfigServerRole> getServerRole(@NotNull Long roleId) {
        return botLayoutService.getServerLayout().flatMap(server -> botLayoutService.getRole(server, roleId));
    }

    public @NotNull Option<BotConfigServerChannel> getServerChannel(@NotNull Long channelId) {
        return botLayoutService.getServerLayout().flatMap(server -> botLayoutService.getChannel(server, channelId));
    }

    public @NotNull Option<BotConfigServerChannel> getOriginalServerChannel() {
        return getServerChannel(getOriginalChannel().getIdLong());
    }

    public @NotNull Option<BotConfigServerChannelCategory> getServerCategory(@NotNull Long categoryId) {
        return botLayoutService.getServerLayout().flatMap(server -> botLayoutService.getCategory(server, categoryId));
    }

    public @NotNull ObjectMapper getMapper() {
        return objectMapper;
    }

    @SuppressWarnings({"LombokGetterMayBeUsed", "RedundantSuppression"})
    public long getExecutionId() {
        return executionId;
    }

    public @NotNull ExternalServiceClient getBackendClient() {
        return getBean("client.service.backend", ExternalServiceClient.class);
    }

    public <T> @NotNull T getBean(@NotNull Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public <T> @NotNull T getBean(@NotNull String name, @NotNull Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

    public @NotNull Option<String> getProperty(@NotNull String key) {
        return Option.of(applicationContext.getEnvironment().getProperty(key));
    }

    public @NotNull String getProperty(@NotNull String key, @NotNull String defaultValue) {
        return applicationContext.getEnvironment().getProperty(key, defaultValue);
    }

    public <T> @NotNull Option<T> getProperty(@NotNull String key, @NotNull Class<T> targetType) {
        return Option.of(applicationContext.getEnvironment().getProperty(key, targetType));
    }

    public <T> @NotNull T getProperty(@NotNull String key, @NotNull Class<T> targetType, @NotNull T defaultValue) {
        return applicationContext.getEnvironment().getProperty(key, targetType, defaultValue);
    }
}