package com.akjostudios.acsp.bot.web.external;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.services")
@Getter
@Setter
public class ExternalServiceProperties {
    private String authApiKey;
    private String supertokensUrl;
    private String backendUrl;
}