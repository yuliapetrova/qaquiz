package com.natura.qaquiz;

import com.natera.qaquiz.config.ConfigReader;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {

    @BeforeAll
    public static void setBaseUri() {
        RestAssured.baseURI = ConfigReader.getTestConfig().getApi().getBaseUrl();
        if (ConfigReader.getTestConfig().getRestAssuredSettings().isDebugMode()) {
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        }
    }
}
