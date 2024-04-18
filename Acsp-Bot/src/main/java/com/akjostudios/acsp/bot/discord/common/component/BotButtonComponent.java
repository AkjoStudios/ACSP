package com.akjostudios.acsp.bot.discord.common.component;

import com.akjostudios.acsp.bot.discord.config.definition.BotConfigComponent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@Getter
public class BotButtonComponent extends BotComponent {
    @Nullable private final String interactionId;
    @NotNull private final String label;
    @NotNull private final Style style;
    @Nullable private final String emoji;
    @Nullable private final String url;
    private final boolean disabled;

    public BotButtonComponent(
            @NotNull String label,
            @Nullable String emoji,
            @NotNull String url,
            boolean disabled
    ) {
        super(BotConfigComponent.Type.BUTTON, true);
        this.interactionId = null;
        this.label = label;
        this.style = Style.LINK;
        this.emoji = emoji;
        this.url = url;
        this.disabled = disabled;
    }

    public BotButtonComponent(
            @NotNull String interactionId,
            @NotNull String label,
            @NotNull Style style,
            @Nullable String emoji,
            boolean disabled
    ) {
        super(BotConfigComponent.Type.BUTTON, true);
        this.interactionId = interactionId;
        this.label = label;
        this.style = style;
        this.emoji = emoji;
        this.url = null;
        this.disabled = disabled;
    }

    @Getter
    @RequiredArgsConstructor
    @SuppressWarnings("unused")
    public enum Style {
        PRIMARY(ButtonStyle.PRIMARY),
        SECONDARY(ButtonStyle.SECONDARY),
        SUCCESS(ButtonStyle.SUCCESS),
        DANGER(ButtonStyle.DANGER),
        LINK(ButtonStyle.LINK);

        private final ButtonStyle buttonStyle;

        public static Style fromString(String style) {
            return Arrays.stream(Style.values())
                    .filter(s -> s.name().equalsIgnoreCase(style))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid button style: " + style));
        }
    }
}