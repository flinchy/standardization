package com.chisom.authservice.config;

import com.chisom.authservice.service.OAuth2AccessTokenService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(AuthProperties.class)
public class AuthConfig {

    private final AuthProperties properties;

    public AuthConfig(AuthProperties properties) {
        this.properties = properties;
    }

    @Bean
    public OAuth2AccessTokenService oAuth2AccessTokenService() {
        return new OAuth2AccessTokenService(properties);
    }
}
