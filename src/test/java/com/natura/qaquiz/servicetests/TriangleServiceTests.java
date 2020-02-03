package com.natura.qaquiz.servicetests;

import static com.natera.qaquiz.helpers.TriangleServiceUtils.cleanUpTriangles;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.createTriangle;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.deleteTriangle;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.getAllTriangles;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.getArea;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.getPerimeter;
import static com.natera.qaquiz.helpers.TriangleServiceUtils.getTriangleById;
import static java.lang.Double.MAX_VALUE;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import com.natera.qaquiz.models.ResultResponse;
import com.natera.qaquiz.models.TriangleRequest;
import com.natera.qaquiz.models.TriangleResponse;
import com.natura.qaquiz.BaseTest;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TriangleServiceTests extends BaseTest {

    public static final String SAMPLE_INPUT = "2;3;3";
    public static final String SAMPLE_SEPARATOR = ";";
    public static final String MESSAGE = "message";
    public static final String LIMIT_EXCEEDED_MESSAGE = "Limit exceeded";
    public static final String NON_EXISTENT_ID = "1cf52d36-d56f-4a5d-b8ec";

    @BeforeEach
    void cleanUp() {
        cleanUpTriangles();
    }

    @Test
    void shouldCreateTriangle() {
        val actualTriangle = createTestTriangle(SAMPLE_INPUT);
        val originalSideValues = parseInputField(SAMPLE_INPUT, SAMPLE_SEPARATOR);

        assertAll(
                () -> assertEquals(originalSideValues[0], actualTriangle.getFirstSide()),
                () -> assertEquals(originalSideValues[1], actualTriangle.getSecondSide()),
                () -> assertEquals(originalSideValues[2], actualTriangle.getThirdSide())
        );
    }

    @Test
    void shouldNotCreateMoreThanTenTriangles() {

        IntStream.range(0, 10)
                .forEach(index ->
                        createTestTriangle(SAMPLE_INPUT));

        createTriangle(TriangleRequest
                .builder()
                .separator(SAMPLE_SEPARATOR)
                .input(SAMPLE_INPUT)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .body(MESSAGE, equalTo(LIMIT_EXCEEDED_MESSAGE));
    }

    @Test
    void shouldGetTriangleById() {
        val originalSideValues = parseInputField(SAMPLE_INPUT, SAMPLE_SEPARATOR);
        val actualTriangle =
                getTriangleById(createTestTriangle(SAMPLE_INPUT).getId())
                        .then()
                        .assertThat()
                        .statusCode(SC_OK)
                        .extract().as(TriangleResponse.class);

        assertAll(
                () -> assertEquals(originalSideValues[0], actualTriangle.getFirstSide()),
                () -> assertEquals(originalSideValues[1], actualTriangle.getSecondSide()),
                () -> assertEquals(originalSideValues[2], actualTriangle.getThirdSide())
        );
    }

    @Test
    void shouldNotGetTriangleWithNonExistentId() {
        getTriangleById(NON_EXISTENT_ID)
                .then()
                .assertThat()
                .statusCode(SC_NOT_FOUND);
    }

    @Test
    void shouldGetAllTriangles() {
        IntStream.range(0, 2)
                .forEach(index ->
                        createTestTriangle(SAMPLE_INPUT));

        val triangles = getAllTriangles()
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract().as(TriangleResponse[].class);

        assertEquals(triangles.length, 2);
    }


    @Test
    void shouldDeleteTriangle() {
        val triangleId = createTestTriangle(SAMPLE_INPUT).getId();

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
    void shouldNotDeleteTriangleWithNonExistentId() {
        val triangleId = createTestTriangle(SAMPLE_INPUT).getId();
        val nonExistentId = triangleId.substring(0, 35);

        deleteTriangle(nonExistentId)
                .then()
                .assertThat()
                .statusCode(SC_OK);

        getTriangleById(triangleId)
                .then()
                .assertThat()
                .statusCode(SC_OK);
    }

    @ParameterizedTest(name = "{index} {0}: input=\"{1}\"  separator=\"{2}\"")
    @MethodSource("triangleTestData")
    void shouldGetPerimeter(String description, String input, String separator) {
        val originalSideValues = parseInputField(input, separator);
        val triangleId = createTestTriangle(input).getId();

        val actualResult = getPerimeter(triangleId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract().as(ResultResponse.class).getResult();

        assertEquals(getExpectedPerimeter(originalSideValues), actualResult);
    }

    @ParameterizedTest(name = "{index} {0}: input=\"{1}\"  separator=\"{2}\"")
    @MethodSource("triangleTestData")
    void shouldGetArea(String description, String input, String separator) {
        val originalSideValues = parseInputField(input, separator);
        val triangleId = createTestTriangle(input).getId();

        val actualResult = getArea(triangleId)
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract().as(ResultResponse.class).getResult();

        assertEquals(getExpectedArea(originalSideValues), actualResult);

    }

    private Double getExpectedPerimeter(Double[] originalSideValues) {
        return IntStream.of(0, 1, 2).mapToDouble(i -> originalSideValues[i]).sum();
    }

    private Double getExpectedArea(Double[] originalSideValues) {
        Double a = originalSideValues[0];
        Double b = originalSideValues[1];
        Double c = originalSideValues[2];
        double p = (a + b + c) / 2;
        return Math.sqrt(p * (p - a) * (p - b) * (p - c));
    }

    private TriangleResponse createTestTriangle(String input) {
        return createTriangle(TriangleRequest
                .builder()
                .separator(SAMPLE_SEPARATOR)
                .input(input)
                .build())
                .then()
                .assertThat()
                .statusCode(SC_OK)
                .extract().as(TriangleResponse.class);
    }

    private Double[] parseInputField(String input, String separator) {
        return Arrays.stream(input.split(separator))
                .map(Double::valueOf).collect(toList()).toArray(new Double[] {});
    }

    private static Stream<Arguments> triangleTestData() {
        return Stream.of(
                of("Side values are positive integer", "2;3;3", ";"),
                of("Side values are max double values",
                        String.format("%s%s%s%s%s", MAX_VALUE, SAMPLE_SEPARATOR, MAX_VALUE, SAMPLE_SEPARATOR,
                                MAX_VALUE), ";")
        );
    }
}
