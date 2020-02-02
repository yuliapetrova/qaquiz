package com.natura.qaquiz.servicetests;

import static com.natera.qaquiz.helpers.AuthUtils.X_AUTH_TOKEN;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

import com.natera.qaquiz.helpers.UrlConsts;
import com.natura.qaquiz.BaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class AuthenticationTests extends BaseTest {

    @ParameterizedTest
    @ValueSource(strings = {EMPTY, "invalidToken"})
    void shouldNotAuthenticateWithInvalidTokens(String token) {
        given()
                .contentType(JSON)
                .header(X_AUTH_TOKEN, token)
                .when()
                .get(UrlConsts.ALL)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    void shouldNotAuthenticateWithoutTokenHeader() {
        given()
                .contentType(JSON)
                .when()
                .get(UrlConsts.ALL)
                .then()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED);
    }
}
