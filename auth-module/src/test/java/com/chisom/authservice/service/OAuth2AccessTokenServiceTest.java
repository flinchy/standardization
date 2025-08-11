package com.chisom.authservice.service;

import com.chisom.authservice.config.AuthProperties;
import com.chisom.authservice.dto.OAuth2AccessTokenRequest;
import com.chisom.authservice.dto.OAuth2AccessTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.chisom.authservice.service.TestProperties.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class OAuth2AccessTokenServiceTest {

    @InjectMocks
    private OAuth2AccessTokenService service;

    @Mock
    private AuthProperties properties;

    @BeforeEach
    void setUp() {
        properties = new AuthProperties();
        properties.setClientId(CLIENT_ID);
        properties.setClientSecret(CLIENT_SECRET);
        properties.setAesKey(AES_KEY);
        properties.setSecret(SECRET);

        service = new OAuth2AccessTokenService(properties);
    }

    @Test
    void generateJwtTest() {

        //Arrange & Act
        long expectedExpiration = 60L * 365 * 24 * 60 * 60;
        OAuth2AccessTokenResponse response = service.generateJWT(request());
        //Assert
        assertNotNull(response.access_token());
        assertEquals("Bearer", response.token_type());
        assertEquals("internal-operation", response.scope());
        assertEquals(expectedExpiration, response.expiresIn(), 5); // Allow 5 second tolerance
    }

    private OAuth2AccessTokenRequest request() {
        return new OAuth2AccessTokenRequest(
                "client_credentials", "ARwdsvCGmeerDq9xeQg9MtGAnky4eHhL",
                "f1fLjcTMkh-WsRP0efBNpQGQB3191B-ihmmYTyOJnstESYGuE1CZuGN9OOFUvup",
                "https://standardization-service.com"
        );
    }
}
