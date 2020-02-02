package com.natura.qaquiz.servicetests;

import static com.natera.qaquiz.helpers.TriangleServiceUtils.cleanUpTriangles;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.createTriangle;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.natera.qaquiz.models.TriangleRequest;
import com.natura.qaquiz.BaseTest;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class InputValidationTests extends BaseTest {

    @AfterEach
    void cleanUp() {
        cleanUpTriangles();
    }

    @ParameterizedTest(name = "{index} {0}: separator=\"{1}\" input=\"{2}\" expectedStatus=\"{3}\"")
    @MethodSource("triangleTestData")
    void shouldValidateTriangleInputDataCorrectly(String description, String separator, String input, int expectedCode) {
        createTriangle(TriangleRequest.builder()
                .separator(separator)
                .input(input)
                .build())
                .then()
                .assertThat()
                .statusCode(expectedCode)
                .log().all();
    }

    private static Stream<Arguments> triangleTestData() {
        return Stream.of(
                of("Valid separator and input", ",", "1,2,3", SC_OK),
                of("Missed second side", ",", "1,3", SC_UNPROCESSABLE_ENTITY),
                of("Missed third side", ",", "1,,3", SC_UNPROCESSABLE_ENTITY),
                of("Zero value side", ",", "0,1,1", SC_UNPROCESSABLE_ENTITY),
                of("Impossible triangle", ",", "2,2,10", SC_UNPROCESSABLE_ENTITY),
                of("Negative sides", ",", "-2,2,-3", SC_UNPROCESSABLE_ENTITY),
                of("Rectangle", ",", "1,2,3,4", SC_UNPROCESSABLE_ENTITY),
                of("Empty separator", "", "1;2;3", SC_OK),
                of("Semicolon separator", ";", "1;2;3", SC_OK),
                of("Empty input", ",", "", SC_UNPROCESSABLE_ENTITY),
                of("Several characters as separator", "and", "1and2and3", SC_OK),
                of("Non-integer side values", ";", "1.5;2.5;3.6", SC_OK),
                of("Wrong decimal separator", ";", "1,5;2,5;3,6", SC_UNPROCESSABLE_ENTITY),
                of("Separator equals to decimal separator", ".", "1.5.2.5.3.6", SC_UNPROCESSABLE_ENTITY),
                of("Big numbers as side values", ",", "1000000000,2000000000,3000000000", SC_OK),
                of("Big numbers Java7 format as side values", ",", "1_000_000_000,2_000_000_000,3_000_000_000", SC_UNPROCESSABLE_ENTITY),
                of("Whitespaces", ",", "1, 2 , 3", SC_OK),
                of("Whitespaces in numbers", ",", "1000 000 000,2000 000 000,3000 000 000", SC_UNPROCESSABLE_ENTITY),
                of("Whitespace as separator", " ", "1 2 3", SC_OK),
                of("Whitespace as separator", " ", "1  2  3", SC_UNPROCESSABLE_ENTITY)

                //small numbers
                //letters
                //numeric systems
                //characters
        );
    }

    @Test
    void shouldCreateTriangleWithDefaultSeparator() {
        createTriangle("{\"input\": \"3;4;5\"}")
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .log().all();
    }

    @ParameterizedTest(name = "{index} {0}: body=\"{1}\"")
    @MethodSource("invalidFormatTestData")
    void shouldNotCreateTriangleIfWrongFormat(String description, String body, int status) {
        createTriangle(body)
                .then()
                .assertThat()
                .statusCode(status)
                .log().all();
    }

    private static Stream<Arguments> invalidFormatTestData() {
        return Stream.of(
                of("No input field", "{\"separator\": \";\"}", SC_UNPROCESSABLE_ENTITY),
                of("Wrong format", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<root>\n"
                        + "<input>3;4;5</input>\n"
                        + "<separator>;</separator>\n"
                        + "</root>", SC_UNPROCESSABLE_ENTITY),
                of("Input field as Object", "{\"separator\": \";\", \"input\": {\"value\": \"3;4;5\"}}", SC_UNPROCESSABLE_ENTITY),
                of("Separator field as Object", "{\"separator\": {\"value\": \";\"}, \"input\": \"3;4;5\"}", SC_UNPROCESSABLE_ENTITY),
                of("Array of data", "[{\"separator\": \";\", \"input\": \"3;4;5\"}]", SC_UNPROCESSABLE_ENTITY)
        );
    }
}
