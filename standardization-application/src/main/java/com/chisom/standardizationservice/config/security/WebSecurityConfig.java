package com.chisom.standardizationservice.config.security;

import com.chisom.commons.util.CryptoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.chisom.standardizationservice.constants.GeneralConstants.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    @Value("${security.secret}")
    private String cypherMsg;
    @Value("${security.aes-key}")
    private String aesKey;
    private final ObjectMapper objectMapper;
    public static final String PREFIX = "SCOPE_";
    private static final String INTERNAL_OPERATION = PREFIX + "internal-operation";

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler(accessDeniedHandler())
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint()))
                .csrf(CsrfConfigurer::disable)
                .formLogin(FormLoginConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                        .httpStrictTransportSecurity(HeadersConfigurer.HstsConfig::disable))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 ->
                        oauth2.accessDeniedHandler(accessDeniedHandler())
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint())
                                .jwt(jwt -> jwt.decoder(jwtDecoder())));

        http.authorizeHttpRequests(matchers ->
                matchers
                        .requestMatchers(POST, "oauth/token").permitAll()
                        .requestMatchers(POST, "provider-alpha/feed").hasAuthority(INTERNAL_OPERATION)
                        .requestMatchers(POST, "provider-beta/feed").hasAuthority(INTERNAL_OPERATION)
                        .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new RestAccessDeniedHandler(objectMapper);
    }

    @Bean
    public LimitFlexJwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new LimitFlexJwtAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedMethods(Arrays.asList(
                HEAD.name(),
                GET.name(),
                POST.name(),
                PUT.name(),
                PATCH.name(),
                DELETE.name()
        ));
        corsConfiguration.setAllowedHeaders(Arrays.asList(AUTHORIZATION, CACHE_CONTROL,
                CONTENT_TYPE, ACCEPT, ACCESS_CONTROL_ALLOW_HEADERS, ACCESS_CONTROL_ALLOW_ORIGIN,
                ACCESS_CONTROL_REQUEST_METHOD, ACCESS_CONTROL_REQUEST_HEADERS, ORIGIN
        ));
        corsConfiguration.setAllowCredentials(false);
        corsConfiguration.setMaxAge(3600L);
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

    @Bean
    public JwtDecoder jwtDecoder() {

        // Build the symmetric key from the configured secret.
        String key;
        try {
            key = CryptoUtil.aesDecrypt(cypherMsg, aesKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SecretKey secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        //To keep things simple, We Skipped the audience and issuer validation; usually this would be validated in prod

        return NimbusJwtDecoder.withSecretKey(secretKey)
               .build();
    }
}
