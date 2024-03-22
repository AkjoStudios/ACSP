package com.akjostudios.acsp.bot.web.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
@Getter
@Setter
public class SecurityProperties {
    private User actuator;
    private User prometheus;

    @Getter
    @Setter
    public static class User {
        private String username;
        private String password;
    }
}