package com.chisom.standardizationservice.controller;

import com.chisom.standardizationservice.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;

class ProviderBetaControllerTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Should ingest beta odds change feed successfully")
    void should_ingest_beta_odds_change_happy_path() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", JwtUtils.INTERNAL_OPERATION_TOKEN)
                .body(oddsChangeRequest())
                .when()
                .post("/provider-beta/feed")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @DisplayName("Should ingest beta settlement feed successfully")
    void should_ingest_beta_settlement_happy_path() {
        given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", JwtUtils.INTERNAL_OPERATION_TOKEN)
                .body(settlementRequest())
                .when()
                .post("/provider-beta/feed")
                .then()
                .assertThat()
                .statusCode(200);
    }

    private String oddsChangeRequest() {
        return """
                {
                    "type": "ODDS",
                    "event_id": "ev456",
                    "odds": {
                        "home": 1.95,
                        "draw": 3.2,
                        "away": 4.0
                    }
                 }
                """;
    }

    private String settlementRequest() {
        return """
                {
                    "type": "SETTLEMENT",
                    "event_id": "ev456",
                    "result": "away"
                }
                """;
    }
}
