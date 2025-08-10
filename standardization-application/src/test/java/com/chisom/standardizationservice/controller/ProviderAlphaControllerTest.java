package com.chisom.standardizationservice.controller;

import com.chisom.standardizationservice.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;

class ProviderAlphaControllerTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Should ingest alpha odds change feed successfully")
    void should_ingest_alpha_odds_change_happy_path() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", JwtUtils.INTERNAL_OPERATION_TOKEN)
                .body(oddsChangeRequest())
                .when()
                .post("/provider-alpha/feed")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @DisplayName("Should ingest alpha settlement feed successfully")
    void should_ingest_alpha_settlement_happy_path() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", JwtUtils.INTERNAL_OPERATION_TOKEN)
                .body(settlementRequest())
                .when()
                .post("/provider-alpha/feed")
                .then()
                .assertThat()
                .statusCode(200);
    }

    private String oddsChangeRequest() {
        return """
                {
                    "msg_type": "odds_update",
                    "event_id": "ev123",
                    "values": {
                        "1": 2.0,
                        "X": 3.1,
                        "2": 3.8
                    }
                }
                """;
    }

    private String settlementRequest() {
        return """
                {
                    "msg_type": "settlement",
                    "event_id": "ev123",
                    "outcome": "1"
                }
                """;
    }
}
