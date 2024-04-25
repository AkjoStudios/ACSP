package com.akjostudios.acsp.bot.discord.common;

import com.github.tonivade.purefun.type.Option;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.automod.AutoModExecutionEvent;
import net.dv8tion.jda.api.events.automod.AutoModRuleCreateEvent;
import net.dv8tion.jda.api.events.automod.AutoModRuleDeleteEvent;
import net.dv8tion.jda.api.events.automod.AutoModRuleUpdateEvent;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.forum.ForumTagAddEvent;
import net.dv8tion.jda.api.events.channel.forum.ForumTagRemoveEvent;
import net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateEmojiEvent;
import net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateModeratedEvent;
import net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.update.*;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.events.emoji.EmojiRemovedEvent;
import net.dv8tion.jda.api.events.emoji.update.EmojiUpdateNameEvent;
import net.dv8tion.jda.api.events.emoji.update.EmojiUpdateRolesEvent;
import net.dv8tion.jda.api.events.guild.GuildAvailableEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnavailableEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.*;
import net.dv8tion.jda.api.events.guild.member.update.*;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideCreateEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideDeleteEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideUpdateEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserAddEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserRemoveEvent;
import net.dv8tion.jda.api.events.guild.update.*;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.events.http.HttpRequestEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.message.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEmojiEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.*;
import net.dv8tion.jda.api.events.session.*;
import net.dv8tion.jda.api.events.stage.StageInstanceCreateEvent;
import net.dv8tion.jda.api.events.stage.StageInstanceDeleteEvent;
import net.dv8tion.jda.api.events.stage.update.StageInstanceUpdatePrivacyLevelEvent;
import net.dv8tion.jda.api.events.stage.update.StageInstanceUpdateTopicEvent;
import net.dv8tion.jda.api.events.sticker.GuildStickerAddedEvent;
import net.dv8tion.jda.api.events.sticker.GuildStickerRemovedEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateAvailableEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateDescriptionEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateNameEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateTagsEvent;
import net.dv8tion.jda.api.events.thread.ThreadHiddenEvent;
import net.dv8tion.jda.api.events.thread.ThreadRevealedEvent;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberJoinEvent;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberLeaveEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.events.user.update.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@SuppressWarnings("unused")
public enum BotEventType {
    RAW(-100, RawGatewayEvent.class),
    OTHER(-10, GenericEvent.class),
    HTTP_REQUEST(-9, HttpRequestEvent.class),
    STATUS_CHANGE(-3, StatusChangeEvent.class),
    GATEWAY_PING(-2, GatewayPingEvent.class),
    EXCEPTION(-1, ExceptionEvent.class),
    READY(0, ReadyEvent.class),
    DISCONNECT(1, SessionDisconnectEvent.class),
    RECONNECT(2, SessionRecreateEvent.class),
    RESUME(3, SessionResumeEvent.class),
    INVALIDATE(4, SessionInvalidateEvent.class),
    SHUTDOWN(5, ShutdownEvent.class),
    USER_UPDATE_NAME(100, UserUpdateNameEvent.class),
    USER_UPDATE_GLOBAL_NAME(101, UserUpdateGlobalNameEvent.class),
    USER_UPDATE_AVATAR(102, UserUpdateAvatarEvent.class),
    USER_UPDATE_FLAGS(110, UserUpdateFlagsEvent.class),
    USER_UPDATE_STATUS(111, UserUpdateOnlineStatusEvent.class),
    USER_UPDATE_ACTIVITIES(112, UserUpdateActivitiesEvent.class),
    USER_UPDATE_ACTIVITY_ORDER(113, UserUpdateActivityOrderEvent.class),
    USER_UPDATE_TYPING(114, UserTypingEvent.class),
    GUILD_AVAILABLE(200, GuildAvailableEvent.class),
    GUILD_UNAVAILABLE(201, GuildUnavailableEvent.class),
    GUILD_UPDATE_NAME(210, GuildUpdateNameEvent.class),
    GUILD_UPDATE_ICON(211, GuildUpdateIconEvent.class),
    GUILD_UPDATE_SPLASH(212, GuildUpdateSplashEvent.class),
    GUILD_UPDATE_BANNER(213, GuildUpdateBannerEvent.class),
    GUILD_UPDATE_DESCRIPTION(214, GuildUpdateDescriptionEvent.class),
    GUILD_UPDATE_LOCALE(215, GuildUpdateLocaleEvent.class),
    GUILD_UPDATE_FEATURES(216, GuildUpdateFeaturesEvent.class),
    GUILD_UPDATE_OWNER(217, GuildUpdateOwnerEvent.class),
    GUILD_UPDATE_MAX_MEMBERS(218, GuildUpdateMaxMembersEvent.class),
    GUILD_UPDATE_MAX_PRESENCE(219, GuildUpdateMaxPresencesEvent.class),
    GUILD_UPDATE_VANITY(220, GuildUpdateVanityCodeEvent.class),
    GUILD_UPDATE_VERIFICATION_LEVEL(221, GuildUpdateVerificationLevelEvent.class),
    GUILD_UPDATE_MFA_LEVEL(222, GuildUpdateMFALevelEvent.class),
    GUILD_UPDATE_NOTIFICATION_LEVEL(223, GuildUpdateNotificationLevelEvent.class),
    GUILD_UPDATE_CONTENT_FILTER(224, GuildUpdateExplicitContentLevelEvent.class),
    GUILD_UPDATE_AFK_CHANNEL(225, GuildUpdateAfkChannelEvent.class),
    GUILD_UPDATE_SYSTEM_CHANNEL(226, GuildUpdateSystemChannelEvent.class),
    GUILD_UPDATE_RULES_CHANNEL(227, GuildUpdateRulesChannelEvent.class),
    GUILD_UPDATE_COMMUNITY_UPDATES_CHANNEL(228, GuildUpdateCommunityUpdatesChannelEvent.class),
    GUILD_UPDATE_AFK_TIMEOUT(229, GuildUpdateAfkTimeoutEvent.class),
    GUILD_UPDATE_BOOST_TIER(230, GuildUpdateBoostTierEvent.class),
    GUILD_UPDATE_BOOST_COUNT(231, GuildUpdateBoostCountEvent.class),
    GUILD_MEMBER_JOIN(240, GuildMemberJoinEvent.class),
    GUILD_MEMBER_UPDATE(241, GuildMemberUpdateEvent.class),
    GUILD_MEMBER_LEAVE(242, GuildMemberRemoveEvent.class),
    GUILD_MEMBER_BAN(243, GuildBanEvent.class),
    GUILD_MEMBER_UNBAN(244, GuildUnbanEvent.class),
    GUILD_MEMBER_ROLE_ADD(245, GuildMemberRoleAddEvent.class),
    GUILD_MEMBER_ROLE_REMOVE(246, GuildMemberRoleRemoveEvent.class),
    GUILD_MEMBER_UPDATE_NICK(250, GuildMemberUpdateNicknameEvent.class),
    GUILD_MEMBER_UPDATE_AVATAR(251, GuildMemberUpdateAvatarEvent.class),
    GUILD_MEMBER_UPDATE_FLAGS(252, GuildMemberUpdateFlagsEvent.class),
    GUILD_MEMBER_UPDATE_BOOST_TIME(253, GuildMemberUpdateBoostTimeEvent.class),
    GUILD_MEMBER_UPDATE_PENDING_STATE(254, GuildMemberUpdatePendingEvent.class),
    GUILD_MEMBER_UPDATE_TIMEOUT_STATE(255, GuildMemberUpdateTimeOutEvent.class),
    GUILD_INVITE_CREATE(260, GuildInviteCreateEvent.class),
    GUILD_INVITE_DELETE(261, GuildInviteDeleteEvent.class),
    GUILD_VOICE_UPDATE(270, GuildVoiceUpdateEvent.class),
    GUILD_VOICE_MUTE(271, GuildVoiceMuteEvent.class),
    GUILD_VOICE_DEAF(272, GuildVoiceDeafenEvent.class),
    GUILD_VOICE_GUILD_MUTE(273, GuildVoiceGuildMuteEvent.class),
    GUILD_VOICE_GUILD_DEAF(274, GuildVoiceGuildDeafenEvent.class),
    GUILD_VOICE_SELF_MUTE(275, GuildVoiceSelfMuteEvent.class),
    GUILD_VOICE_SELF_DEAF(276, GuildVoiceSelfDeafenEvent.class),
    GUILD_VOICE_SUPPRESS(277, GuildVoiceSuppressEvent.class),
    GUILD_VOICE_STREAM(278, GuildVoiceStreamEvent.class),
    GUILD_VOICE_VIDEO(279, GuildVoiceVideoEvent.class),
    GUILD_PERMISSION_OVERRIDE_CREATE(280, PermissionOverrideCreateEvent.class),
    GUILD_PERMISSION_OVERRIDE_UPDATE(281, PermissionOverrideUpdateEvent.class),
    GUILD_PERMISSION_OVERRIDE_DELETE(282, PermissionOverrideDeleteEvent.class),
    GUILD_EVENT_CREATE(290, ScheduledEventCreateEvent.class),
    GUILD_EVENT_USER_ADD(291, ScheduledEventUserAddEvent.class),
    GUILD_EVENT_USER_REMOVE(292, ScheduledEventUserRemoveEvent.class),
    GUILD_EVENT_DELETE(293, ScheduledEventDeleteEvent.class),
    CHANNEL_CREATE(300, ChannelCreateEvent.class),
    CHANNEL_DELETE(301, ChannelDeleteEvent.class),
    CHANNEL_UPDATE_NAME(310, ChannelUpdateNameEvent.class),
    CHANNEL_UPDATE_POSITION(311, ChannelUpdatePositionEvent.class),
    CHANNEL_UPDATE_PARENT(312, ChannelUpdateParentEvent.class),
    CHANNEL_UPDATE_TOPIC(313, ChannelUpdateTopicEvent.class),
    CHANNEL_UPDATE_TYPE(314, ChannelUpdateTypeEvent.class),
    CHANNEL_UPDATE_FLAGS(315, ChannelUpdateFlagsEvent.class),
    CHANNEL_UPDATE_SLOWMODE(316, ChannelUpdateSlowmodeEvent.class),
    CHANNEL_UPDATE_USER_LIMIT(317, ChannelUpdateUserLimitEvent.class),
    CHANNEL_UPDATE_NSFW(318, ChannelUpdateNSFWEvent.class),
    CHANNEL_UPDATE_INVITABLE(319, ChannelUpdateInvitableEvent.class),
    CHANNEL_VOICE_UPDATE_BITRATE(320, ChannelUpdateBitrateEvent.class),
    CHANNEL_VOICE_UPDATE_STATUS(321, ChannelUpdateVoiceStatusEvent.class),
    CHANNEL_VOICE_UPDATE_REGION(322, ChannelUpdateRegionEvent.class),
    CHANNEL_THREAD_DEFAULT_SLOWMODE(330, ChannelUpdateDefaultThreadSlowmodeEvent.class),
    CHANNEL_THREAD_UPDATE_ARCHIVED(331, ChannelUpdateArchivedEvent.class),
    CHANNEL_THREAD_UPDATE_ARCHIVE_TIMESTAMP(332, ChannelUpdateArchiveTimestampEvent.class),
    CHANNEL_THREAD_UPDATE_AUTO_ARCHIVE_DURATION(333, ChannelUpdateAutoArchiveDurationEvent.class),
    CHANNEL_FORUM_UPDATE_DEFAULT_LAYOUT(340, ChannelUpdateDefaultLayoutEvent.class),
    CHANNEL_FORUM_UPDATE_DEFAULT_REACTION(341, ChannelUpdateDefaultReactionEvent.class),
    CHANNEL_FORUM_UPDATE_DEFAULT_SORT_ORDER(342, ChannelUpdateDefaultSortOrderEvent.class),
    CHANNEL_FORUM_UPDATE_APPLIED_TAGS(343, ChannelUpdateAppliedTagsEvent.class),
    CHANNEL_FORUM_TAG_ADD(344, ForumTagAddEvent.class),
    CHANNEL_FORUM_TAG_REMOVE(345, ForumTagRemoveEvent.class),
    CHANNEL_FORUM_TAG_UPDATE_NAME(346, ForumTagUpdateNameEvent.class),
    CHANNEL_FORUM_TAG_UPDATE_EMOJI(347, ForumTagUpdateEmojiEvent.class),
    CHANNEL_FORUM_TAG_UPDATE_MODERATED(348, ForumTagUpdateModeratedEvent.class),
    MESSAGE_RECEIVED(400, MessageReceivedEvent.class),
    MESSAGE_UPDATE(401, MessageUpdateEvent.class),
    MESSAGE_DELETE(402, MessageDeleteEvent.class),
    MESSAGE_BULK_DELETE(403, MessageBulkDeleteEvent.class),
    MESSAGE_EMBED_SENT(404, MessageEmbedEvent.class),
    MESSAGE_REACTION_ADD(410, MessageReactionAddEvent.class),
    MESSAGE_REACTION_REMOVE(411, MessageReactionRemoveEvent.class),
    MESSAGE_REACTION_REMOVE_ALL(412, MessageReactionRemoveAllEvent.class),
    MESSAGE_REACTION_REMOVE_EMOJI(413, MessageReactionRemoveEmojiEvent.class),
    ROLE_CREATE(500, RoleCreateEvent.class),
    ROLE_DELETE(501, RoleDeleteEvent.class),
    ROLE_UPDATE_NAME(510, RoleUpdateNameEvent.class),
    ROLE_UPDATE_POSITION(511, RoleUpdatePositionEvent.class),
    ROLE_UPDATE_COLOR(512, RoleUpdateColorEvent.class),
    ROLE_UPDATE_ICON(513, RoleUpdateIconEvent.class),
    ROLE_UPDATE_PERMISSIONS(514, RoleUpdatePermissionsEvent.class),
    ROLE_UPDATE_MENTIONABLE(515, RoleUpdateMentionableEvent.class),
    ROLE_UPDATE_HOISTED(516, RoleUpdateHoistedEvent.class),
    EMOJI_ADD(600, EmojiAddedEvent.class),
    EMOJI_REMOVE(601, EmojiRemovedEvent.class),
    EMOJI_UPDATE_NAME(610, EmojiUpdateNameEvent.class),
    EMOJI_UPDATE_ROLES(611, EmojiUpdateRolesEvent.class),
    STICKER_ADD(700, GuildStickerAddedEvent.class),
    STICKER_REMOVE(701, GuildStickerRemovedEvent.class),
    STICKER_UPDATE_NAME(710, GuildStickerUpdateNameEvent.class),
    STICKER_UPDATE_DESCRIPTION(711, GuildStickerUpdateDescriptionEvent.class),
    STICKER_UPDATE_TAGS(712, GuildStickerUpdateTagsEvent.class),
    STICKER_UPDATE_AVAILABLE(713, GuildStickerUpdateAvailableEvent.class),
    THREAD_REVEAL(800, ThreadRevealedEvent.class),
    THREAD_HIDE(801, ThreadHiddenEvent.class),
    THREAD_MEMBER_JOIN(802, ThreadMemberJoinEvent.class),
    THREAD_MEMBER_LEAVE(803, ThreadMemberLeaveEvent.class),
    STAGE_INSTANCE_CREATE(900, StageInstanceCreateEvent.class),
    STAGE_INSTANCE_DELETE(901, StageInstanceDeleteEvent.class),
    STAGE_INSTANCE_UPDATE_TOPIC(910, StageInstanceUpdateTopicEvent.class),
    STAGE_INSTANCE_UPDATE_PRIVACY_LEVEL(911, StageInstanceUpdatePrivacyLevelEvent.class),
    AUTOMOD_EXECUTED(1000, AutoModExecutionEvent.class),
    AUTOMOD_RULE_CREATE(1001, AutoModRuleCreateEvent.class),
    AUTOMOD_RULE_UPDATE(1002, AutoModRuleUpdateEvent.class),
    AUTOMOD_RULE_DELETE(1003, AutoModRuleDeleteEvent.class),
    INTERACTION_EVENT(1100, GenericComponentInteractionCreateEvent.class);

    private final int id;
    private final Class<? extends GenericEvent> eventClass;

    private static final Map<Integer, BotEventType> ID_MAP = Arrays.stream(BotEventType.values())
            .collect(Collectors.toUnmodifiableMap(BotEventType::getId, type -> type));

    private static final Map<Class<? extends GenericEvent>, BotEventType> CLASS_MAP = Arrays.stream(BotEventType.values())
            .filter(type -> type != OTHER)
            .collect(Collectors.toUnmodifiableMap(BotEventType::getEventClass, type -> type));

    public static @NotNull Option<BotEventType> getById(int id) {
        return Option.of(ID_MAP.get(id));
    }

    public static @NotNull Option<BotEventType> getByClass(Class<? extends GenericEvent> eventClass) {
        Class<?> currentClass = eventClass;
        while (currentClass != null) {
            BotEventType type = CLASS_MAP.get(currentClass);
            if (type != null) { return Option.some(type); }
            currentClass = currentClass.getSuperclass();
        }
        return Option.none();
    }
}