package com.natura.qaquiz;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.natera.qaquiz.config.ConfigReader;
import com.natera.qaquiz.models.TriangleRequest;
import lombok.val;
import org.junit.jupiter.api.Test;

public class FrameworkTests extends BaseTest {

    @Test
    void configTest() {
        val config = ConfigReader.getTestConfig();
        assertAll(
                () -> assertEquals("edaf007f-bd12-424e-bca3-e1361e0d6860", config.getApi().getToken()),
                () -> assertEquals("https://qa-quiz.natera.com/", config.getApi().getBaseUrl()),
                () -> assertEquals(true, config.getRestAssuredSettings().isDebugMode())
        );
    }

    @Test
    void requestModelTest() {
        val request = TriangleRequest
                .builder()
                .input("3,2,4")
                .separator(",")
                .build();

        assertAll(
                () -> assertEquals("3,2,4", request.getInput()),
                () -> assertEquals(",", request.getSeparator())
        );
    }
}
