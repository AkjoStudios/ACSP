package com.akjostudios.acsp.backend.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
@Getter
@Setter
public class SecurityProperties {
    private Prometheus prometheus;

    @Getter
    @Setter
    public static class Prometheus {
        private String username;
        private String password;
    }
}