package com.akjostudios.acsp.bot.discord.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;
import org.springframework.web.server.i18n.LocaleContextResolver;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BotLocalizationConfig {
    @Bean
    @Primary
    public LocaleContextResolver localeResolver(
            @NotNull BotConfigProperties configProperties
    ) {
        AcceptHeaderLocaleContextResolver localeResolver = new AcceptHeaderLocaleContextResolver();
        localeResolver.setDefaultLocale(Locale.forLanguageTag(configProperties.getDefaultLocale()));
        return localeResolver;
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("BotStrings");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Bean
    public List<Locale> availableLocales(
            @NotNull BotConfigProperties configProperties
    ) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            List<Locale> locales = Arrays.stream(resolver.getResources("classpath*:/BotStrings*.properties"))
                    .map(resource -> Objects.requireNonNull(resource.getFilename()).substring(
                            "BotStrings_".length(), resource.getFilename().length() - ".properties".length()
                    )).map(Locale::forLanguageTag).toList();
            log.info("Available locales: {}", locales);
            return locales;
        } catch (Exception ex) {
            log.warn("Failed to load available locales!", ex);
            return List.of(Locale.forLanguageTag(configProperties.getDefaultLocale()));
        }
    }
}