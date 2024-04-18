package com.akjostudios.acsp.bot.discord.common.component.conversion;

import com.akjostudios.acsp.bot.discord.common.component.BotActionRowComponent;
import com.akjostudios.acsp.bot.discord.common.component.BotButtonComponent;
import com.akjostudios.acsp.bot.discord.common.component.BotComponent;
import com.akjostudios.acsp.bot.discord.common.component.conversion.config.BotConfigComponentConverter;
import com.akjostudios.acsp.bot.discord.common.component.conversion.config.BotConfigComponentConverterProvider;
import com.akjostudios.acsp.bot.discord.common.component.conversion.discord.BotDiscordComponentConverter;
import com.akjostudios.acsp.bot.discord.common.component.conversion.discord.BotDiscordComponentConverterProvider;
import com.akjostudios.acsp.bot.discord.common.component.conversion.discord.DiscordComponentType;
import com.akjostudios.acsp.bot.discord.config.definition.BotConfigComponent;
import com.akjostudios.acsp.bot.discord.service.BotStringsService;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.purefun.type.Try;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class BotComponentConverters {
    @Contract(pure = true)
    private BotComponentConverters() {}

    public static class BotActionRowComponentConverter implements BotConfigComponentConverter<BotActionRowComponent> {
        @Override
        @SuppressWarnings("unchecked")
        public @NotNull Option<BotActionRowComponent> convert(
                @NotNull BotStringsService stringsService,
                @NotNull BotConfigComponent component,
                @NotNull Option<Locale> locale,
                @NotNull List<@NotNull String> labelPlaceholders,
                String@NotNull... placeholders
        ) {
            return Try.of(() -> (
                    (List<BotConfigComponent>) component.getData().getOrDefault("components", List.of())
            ).stream()
                    .map(c -> BotComponentConverters.forConfig(c.getType()).convert(
                            stringsService, c, locale, labelPlaceholders, placeholders
                    )).filter(Option::isPresent).map(Option::getOrElseThrow).toList()
            ).toOption().map(BotActionRowComponent::new);
        }
    }

    public static class BotButtonComponentConverter implements
            BotConfigComponentConverter<BotButtonComponent>,
            BotDiscordComponentConverter<Button, BotButtonComponent> {
        @Override
        public @NotNull Option<BotButtonComponent> convert(
                @NotNull BotStringsService stringsService,
                @NotNull BotConfigComponent component,
                @NotNull Option<Locale> locale,
                @NotNull List<@NotNull String> labelPlaceholders,
                String@NotNull... placeholders
        ) {
            return Try.of(() -> {
                BotButtonComponent.Style style = BotButtonComponent.Style.fromString(
                        component.getData().getOrDefault("style", "primary").toString()
                );
                String label = stringsService.getString(
                        component.getData().getOrDefault("label", "Unnamed Button").toString(),
                        locale, labelPlaceholders, placeholders
                ).getOrElse("Unnamed Button");
                String emoji = component.getData().getOrDefault("emoji", "").toString();
                boolean disabled = Boolean.parseBoolean(
                        component.getData().getOrDefault("disabled", "false").toString()
                );

                return style.equals(BotButtonComponent.Style.LINK) ?
                        new BotButtonComponent(
                                label, emoji,
                                component.getData().getOrDefault("url", "").toString(),
                                disabled
                        ) : new BotButtonComponent(
                                component.getData().get("interaction-id").toString(),
                                label, style, emoji, disabled
                        );
            }).toOption();
        }

        @Override
        public @NotNull Button convert(@NotNull BotButtonComponent component) {
            return (switch (component.getStyle()) {
                case PRIMARY -> Button.primary(
                        Objects.requireNonNull(component.getInteractionId()),
                        component.getLabel()
                );
                case SECONDARY -> Button.secondary(
                        Objects.requireNonNull(component.getInteractionId()),
                        component.getLabel()
                );
                case SUCCESS -> Button.success(
                        Objects.requireNonNull(component.getInteractionId()),
                        component.getLabel()
                );
                case DANGER -> Button.danger(
                        Objects.requireNonNull(component.getInteractionId()),
                        component.getLabel()
                );
                case LINK -> Button.link(
                        Objects.requireNonNull(component.getUrl()),
                        component.getLabel()
                );
            }).withEmoji(
                    component.getEmoji() == null
                            ? null
                            : Emoji.fromFormatted(component.getEmoji())
            ).withDisabled(component.isDisabled());
        }
    }

    public static <T extends BotComponent> @NotNull BotConfigComponentConverter<T> forConfig(
            @NotNull BotConfigComponent.Type type
    ) {
        return (BotConfigComponentConverter<T>) (switch (type) {
            case ACTION_ROW -> BotConfigComponentConverterProvider.ACTION_ROW;
            case BUTTON -> BotConfigComponentConverterProvider.BUTTON;
        }).provide();
    }

    @SuppressWarnings({"unchecked", "java:S119"})
    public static <IC extends ItemComponent, BC extends BotComponent>
    @NotNull BotDiscordComponentConverter<IC, BC> forDiscord(
            @NotNull DiscordComponentType type
    ) {
        return (BotDiscordComponentConverter<IC, BC>) (switch (type) {
            case BUTTON -> BotDiscordComponentConverterProvider.BUTTON;
        }).provide();
    }
}