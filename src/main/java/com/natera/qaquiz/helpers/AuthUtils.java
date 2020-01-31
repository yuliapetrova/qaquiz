package com.natera.qaquiz.helpers;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

import com.natera.qaquiz.config.ConfigReader;
import io.restassured.specification.RequestSpecification;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class AuthUtils {

    public static final String X_AUTH_TOKEN = "X-User";

    public static RequestSpecification getAuthRequestSpecification() {
        val request = given()
                .contentType(JSON)
                .header(X_AUTH_TOKEN, ConfigReader.getTestConfig().getApi().getToken());
        return ConfigReader.getTestConfig().getRestAssuredSettings().isDebugMode() ? request.log().all() : request;
    }
}
