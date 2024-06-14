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
import com.akjostudios.acsp.common.api.ExternalServiceClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import org.jetbrains.annotations.NotNull;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue", "java:S4968"})
public interface IBotCommandContext {
    void setArguments(List<? extends BotCommandArgument<?>> arguments);

    @NotNull Option<BotConfigCommand> getDefinition();

    @NotNull Option<BotConfigCommand.Subcommand> getSubcommandDefinition();

    boolean isSubcommand();

    Option<String> getSubcommandName();

    @NotNull List<BotConfigCommand.Argument> getArgumentDefinitions();


    Mono<Map<String, Object>> getCommandData();
    void setCommandData(Map<String, Object> commandData);


    <T> @NotNull Option<T> getArgument(@NotNull String id, @NotNull Class<T> type);

    @NotNull Map<String, String> getArgumentMap();

    @NotNull Try<BotConfigMessage> getMessage(
            @NotNull String label,
            String@NotNull... placeholders
    );

    @NotNull Try<BotConfigMessage> getMessage(
            @NotNull String label,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    );

    @NotNull Try<BotConfigMessage> getErrorMessage(
            @NotNull String errorTitle,
            @NotNull String errorDescription
    );

    @NotNull Try<BotConfigMessage> getErrorMessage(
            @NotNull String errorTitle,
            @NotNull String errorDescription,
            @NotNull List<String> placeholders
    );

    @NotNull Try<BotConfigMessage> getInternalErrorMessage(
            @NotNull String errorMessage
    );

    @NotNull Option<BotConfigMessageEmbed.Field> getField(
            @NotNull String label,
            String@NotNull... placeholders
    );

    @NotNull Option<BotConfigMessageEmbed.Field> getField(
            @NotNull String label,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    );

    <T extends BotComponent> @NotNull Option<T> getComponent(
            @NotNull String label,
            String@NotNull... placeholders
    );

    <T extends BotComponent> @NotNull Option<T> getComponent(
            @NotNull String label,
            @NotNull List<@NotNull String> labelPlaceholders,
            String@NotNull... placeholders
    );

    @NotNull Try<BotConfigMessage> injectFields(
            @NotNull Try<BotConfigMessage> message,
            Option<Integer> index,
            List<Option<BotConfigMessageEmbed.Field>> fields
    );

    @NotNull Try<BotConfigMessage> injectComponents(
            @NotNull Try<BotConfigMessage> message,
            List<Option<BotActionRowComponent>> rowComponents
    );

    @NotNull Option<BotActionRowComponent> createActionRow(
            @NotNull List<Option<? extends BotComponent>> components
    );

    @NotNull Option<BotButtonComponent> createButton(
            @NotNull String label,
            @NotNull Option<String> emoji,
            @NotNull String url,
            boolean disabled
    );

    @NotNull Option<BotButtonComponent> createButton(
            @NotNull String interactionId,
            @NotNull String label,
            @NotNull BotButtonComponent.Style style,
            @NotNull Option<String> emoji,
            boolean disabled
    );

    @NotNull Try<MessageCreateAction> sendMessage(String message);

    @NotNull Try<MessageCreateAction> sendMessage(
            String message,
            @NotNull BotConfigServerChannel channel
    );

    @NotNull Try<MessageCreateAction> sendMessage(
            String message,
            List<Option<BotActionRowComponent>> components
    );

    @NotNull Try<MessageCreateAction> sendMessage(
            String message,
            @NotNull BotConfigServerChannel channel,
            @NotNull List<Option<BotActionRowComponent>> components
    );

    @NotNull Try<MessageCreateAction> sendMessage(
            @NotNull Try<BotConfigMessage> message
    );

    @NotNull Try<MessageCreateAction> sendMessage(
            @NotNull Try<BotConfigMessage> message,
            @NotNull BotConfigServerChannel channel
    );

    @NotNull Try<MessageCreateAction> sendPrivateMessage(String message);

    @NotNull Try<MessageCreateAction> sendPrivateMessage(
            String message,
            @NotNull List<Option<BotActionRowComponent>> components
    );

    @NotNull Try<MessageCreateAction> sendPrivateMessage(
            @NotNull Try<BotConfigMessage> message
    );

    @NotNull Try<MessageEditAction> editMessage(
            @NotNull Message message,
            @NotNull String newMessage
    );

    @NotNull Try<MessageEditAction> editMessage(
            @NotNull Message message,
            @NotNull String newMessage,
            @NotNull List<Option<BotActionRowComponent>> components
    );

    @NotNull Try<MessageEditAction> editMessage(
            @NotNull Message message,
            @NotNull Try<BotConfigMessage> newMessage
    );

    boolean memberHasRole(
            @NotNull Member member,
            @NotNull BotConfigServerRole role
    );

    boolean memberInChannel(
            @NotNull Member member,
            @NotNull BotConfigServerChannel channel
    );

    @NotNull Map<BotConfigServerRole, Boolean> memberMatchesRoles(
            @NotNull Member member,
            @NotNull Collection<BotConfigCommand.RolePermission> requiredRoles
    );

    @NotNull Member getOriginalAuthor();

    @NotNull Message getOriginalMessage();

    @NotNull TextChannel getOriginalChannel();

    @NotNull String getJumpUrl();

    @NotNull Option<User> getUser(@NotNull String userId);

    @NotNull Option<Member> getMember(@NotNull String memberId);

    @NotNull Option<Role> getRole(@NotNull String roleId);

    @NotNull Option<TextChannel> getChannel(@NotNull String channelId);

    @NotNull Option<Category> getCategory(@NotNull String categoryId);

    @NotNull Option<Emoji> getEmoji(@NotNull String emojiId);

    @NotNull Option<BotConfigServerRole> getServerRole(@NotNull Long roleId);

    @NotNull Option<BotConfigServerChannel> getServerChannel(@NotNull Long channelId);

    @NotNull Option<BotConfigServerChannel> getOriginalServerChannel();

    @NotNull Option<BotConfigServerChannelCategory> getServerCategory(@NotNull Long categoryId);

    @NotNull ObjectMapper getMapper();

    long getExecutionId();

    @NotNull ExternalServiceClient getBackendClient();

    <T> @NotNull T getBean(@NotNull Class<T> clazz);

    <T> @NotNull T getBean(@NotNull String name, @NotNull Class<T> clazz);

    <T> @NotNull List<T> getBeans(@NotNull Class<T> clazz);

    @NotNull Option<String> getProperty(@NotNull String key);

    @NotNull String getProperty(@NotNull String key, @NotNull String defaultValue);

    <T> @NotNull Option<T> getProperty(@NotNull String key, @NotNull Class<T> targetType);

    <T> @NotNull T getProperty(@NotNull String key, @NotNull Class<T> targetType, @NotNull T defaultValue);
}