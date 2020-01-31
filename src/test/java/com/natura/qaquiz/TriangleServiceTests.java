package com.natura.qaquiz;

import static com.natera.qaquiz.helpers.TriangleServiceUtils.cleanUpTriangles;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.createTriangle;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.getAllTriangles;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.getArea;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.getPerimeter;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.getTriangleById;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.natera.qaquiz.models.TriangleRequest;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TriangleServiceTests extends BaseTest {

    @AfterEach
    void cleanUp() {
        cleanUpTriangles();
    }

    @Test
    void authorizationTest() {
    }

    @Test
    void myTest() {
//        createTriangle();
        getAllTriangles()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .log().all();
        getTriangleById("2")
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .log().all();
        getPerimeter("2");
        getArea("2");

        cleanUpTriangles();
    }

    @ParameterizedTest
    @MethodSource("validData")
    void newTest(String separator, String input, int expectedCode) {
        createTriangle(TriangleRequest.builder()
                .separator(separator)
                .input(input)
                .build())
                .then()
                .assertThat()
                .statusCode(expectedCode)
                .log().all();
    }

    private static Stream<Arguments> validData() {
        return Stream.of(
                of(",", "1,2,3", SC_OK),
                of(",", "1,3", SC_UNPROCESSABLE_ENTITY),
                of(",", "1,,3", SC_UNPROCESSABLE_ENTITY),
                of(",", "0,1,1", SC_UNPROCESSABLE_ENTITY),
                of(",", "2,2,10", SC_UNPROCESSABLE_ENTITY),
                of(",", "-2,2,-3", SC_UNPROCESSABLE_ENTITY),
                of(",", "1,2,3,4", SC_UNPROCESSABLE_ENTITY),
                of("", "1,2,3", SC_UNPROCESSABLE_ENTITY),
                of(";", "1;2;3", SC_OK),
                of("", "1;2;3", SC_OK),
                of(",", " ", SC_UNPROCESSABLE_ENTITY),
                of("and", "1and2and3", SC_OK),
                of(";", "1.5;2.5;3.6", SC_OK),
                of(";", "1,5;2,5;3,6", SC_UNPROCESSABLE_ENTITY),
                of(".", "1.5.2.5.3.6", SC_UNPROCESSABLE_ENTITY)
                //big numbers
                //small numbers
                //letters
                //numeric systems
                //characters
        );
    }
}
