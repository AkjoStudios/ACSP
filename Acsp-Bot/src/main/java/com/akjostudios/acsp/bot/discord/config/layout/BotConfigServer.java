package com.akjostudios.acsp.bot.discord.config.layout;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Getter
@Setter
@ToString
@Validated
public class BotConfigServer {
    @NotNull @PositiveOrZero private Long id;
    @NotNull private @NotNull Map<@NotNull BotConfigServerRole, @NotNull @PositiveOrZero Long> roles;
    @NotNull private @NotNull Map<@NotNull BotConfigServerChannel, @NotNull @PositiveOrZero Long> channels;
}