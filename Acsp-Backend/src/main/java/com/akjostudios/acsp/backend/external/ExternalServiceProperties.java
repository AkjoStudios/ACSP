package com.akjostudios.acsp.backend.external;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.services")
@Getter
@Setter
public class ExternalServiceProperties {
    private String authApiKey;
    private Integer tokenRefreshInterval;
    private String supertokensUrl;
    private String botUrl;
}