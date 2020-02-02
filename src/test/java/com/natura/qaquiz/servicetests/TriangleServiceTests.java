package com.natura.qaquiz.servicetests;

import static com.natera.qaquiz.helpers.TriangleServiceUtils.cleanUpTriangles;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.createTriangle;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.deleteTriangle;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.getAllTriangles;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.getArea;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.getPerimeter;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.getTriangleById;
import static java.lang.Double.*;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.natera.qaquiz.models.TriangleRequest;
import com.natera.qaquiz.models.TriangleResponse;
import com.natura.qaquiz.BaseTest;
import java.util.Arrays;
import java.util.stream.Stream;
import lombok.val;
import lombok.var;
import org.hamcrest.Matchers;
import org.hamcrest.number.IsCloseTo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TriangleServiceTests extends BaseTest {

    public static final String SEPARATOR = ";";
    public static final String MESSAGE = "message";
    public static final String SIMPLE_INPUT = "2;3;3";
    public static final String LIMIT_EXCEEDED_MESSAGE = "Limit exceeded";
    public static final String FIRST_SIDE_FIELD = "firstSide";
    public static final String SECOND_SIDE_FIELD = "secondSide";
    public static final String THIRD_SIDE_FIELD = "thirdSide";
    public static final String NON_EXISTENT_ID = "1cf52d36-d56f-4a5d-b8ec";
    public static final String RESULT = "result";


    @BeforeEach
    void cleanUp() {
        cleanUpTriangles();
    }

    @Test
    void shouldCreateTriangle() {
        val response = createTestTriangle(SIMPLE_INPUT);
        val sides = getInputArray();

        assertAll(
                () -> assertEquals(sides[0], response.getFirstSide()),
                () -> assertEquals(sides[1], response.getSecondSide()),
                () -> assertEquals(sides[2], response.getThirdSide())
        );
    }

    @Test
    void shouldNotCreateMoreThanTenTriangles() {

        for (int i = 0; i < 11; i++) {
            createTestTriangle(SIMPLE_INPUT);
        }

        createTriangle(TriangleRequest
                .builder()
                .separator(SEPARATOR)
                .input(SIMPLE_INPUT)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body(MESSAGE, equalTo(LIMIT_EXCEEDED_MESSAGE));
    }

    @Test
    void shouldGetTriangleById() {
        val sides = getInputArray();
        getTriangleById(
                createTestTriangle(SIMPLE_INPUT).getId())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .body(FIRST_SIDE_FIELD, equalTo(sides[0]))
                .body(SECOND_SIDE_FIELD, equalTo(sides[1]))
                .body(THIRD_SIDE_FIELD, equalTo(sides[2]));
    }

    @Test
    void shouldNotGetTriangeWithWrongId() {
        getTriangleById(NON_EXISTENT_ID)
                .then()
                .assertThat()
                .statusCode(SC_NOT_FOUND);
    }

    @Test
    void shouldGetAllTriangles() {
        for (int i = 0; i < 2; i++) {
            createTestTriangle(SIMPLE_INPUT);
        }

        val trianglesCount = getAllTriangles()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract().as(TriangleResponse[].class);
        assertEquals(trianglesCount.length, 2);
    }


    @Test
    void shouldDeleteTriangle() {
        val triangleId = createTestTriangle(SIMPLE_INPUT).getId();

        deleteTriangle(triangleId)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        getTriangleById(triangleId)
                .then()
                .assertThat()
                .statusCode(SC_NOT_FOUND);
    }

    @Test
    void shouldNotDeleteTriangleWithWrongId() {
        val triangleId = createTestTriangle(SIMPLE_INPUT).getId();

        deleteTriangle(triangleId.substring(0, 35))
                .then()
                .assertThat()
                .statusCode(SC_OK);

        getTriangleById(triangleId)
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    @ParameterizedTest(name = "{index} {0}: input=\"{1}\" expected perimeter=\"{2}\"")
    @MethodSource("perimeterTestData")
    void shouldGetCorrectPerimeter(String description, String input, Double expectedPerimeter) {
        val triangleId = createTestTriangle(input).getId();

        val actualResult = getPerimeter(triangleId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract().jsonPath().get(RESULT);
        assertEquals(expectedPerimeter, actualResult);
    }

    @Test
    void shouldReturnErrorWhenPerimeterIsTooLarge() {
        var input = String.format("%s%s%s%s%s", MAX_VALUE, SEPARATOR, MAX_VALUE, SEPARATOR,
                MAX_VALUE);
        var expectedResult = "Infinity";
        val triangleId = createTestTriangle(input).getId();

        val actualResult = getPerimeter(triangleId)
                .then()
                .assertThat()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .extract().jsonPath().get(RESULT);
        assertEquals(expectedResult, actualResult);

    }

    @ParameterizedTest(name = "{index} {0}: input=\"{1}\" expected area=\"{2}\"")
    @MethodSource("areaTestData")
    void shouldGetArea(String description, String input, int expectedArea) {
        val triangleId = createTestTriangle(input).getId();

        val actualResult = getArea(triangleId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract().jsonPath().get(RESULT);
        assertEquals(new Float( 2.828427), actualResult);

    }

    private TriangleResponse createTestTriangle(String input) {
        return createTriangle(TriangleRequest
                .builder()
                .separator(SEPARATOR)
                .input(input)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract().as(TriangleResponse.class);
    }

    private Double[] getInputArray() {
        return Arrays.stream(SIMPLE_INPUT.split(SEPARATOR))
                .map(Double::valueOf).collect(toList()).toArray(new Double[] {});
    }

    private static Stream<Arguments> perimeterTestData() {
        return Stream.of(
                of("Positive case", "2;3;3", new Double( 8.0)),
                of("Big num", String.format("%s%s%s%s%s", MAX_VALUE + 1, SEPARATOR, MAX_VALUE, SEPARATOR,
                        MAX_VALUE), new Double(MAX_VALUE*3))
        );
    }

    private static Stream<Arguments> areaTestData() {
        return Stream.of(
                of("Valid separator and input", ",", "1,2,3", SC_OK),
                of("Missed second side", ",", "1,3", SC_UNPROCESSABLE_ENTITY)
        );
    }


}
