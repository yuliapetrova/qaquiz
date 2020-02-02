package com.natera.qaquiz.helpers;

import static com.natera.qaquiz.helpers.AuthUtils.getAuthRequestSpecification;
import static com.natera.qaquiz.helpers.UrlConsts.TRIANGLE;
import static com.natera.qaquiz.helpers.UrlConsts.TRIANGLE_ID;
import static org.apache.http.HttpStatus.SC_OK;

import com.natera.qaquiz.models.TriangleRequest;
import com.natera.qaquiz.models.TriangleResponse;
import io.restassured.response.Response;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class TriangleServiceUtils {

    public static Response getAllTriangles() {
        return getAuthRequestSpecification()
                .when()
                .get(UrlConsts.ALL);
    }

    public static Response getTriangleById(String id) {
        return getAuthRequestSpecification()
                .pathParam(UrlConsts.ID, id)
                .when()
                .get(TRIANGLE_ID);
    }

    public static Response getPerimeter(String id) {
        return getAuthRequestSpecification()
                .pathParam(UrlConsts.ID, id)
                .when()
                .get(UrlConsts.PERIMETER);
    }

    public static Response getArea(String id) {
        return getAuthRequestSpecification()
                .pathParam(UrlConsts.ID, id)
                .when()
                .get(UrlConsts.AREA);
    }

    public static Response deleteTriangle(String id) {
        return getAuthRequestSpecification()
                .pathParam(UrlConsts.ID, id)
                .when()
                .delete(TRIANGLE_ID);
    }

    public static void cleanUpTriangles() {
        val allTriangles = getAllTriangles();
        TriangleResponse[] triangles = allTriangles.as(TriangleResponse[].class);
        Stream.of(triangles)
                .forEach(item -> deleteTriangle(item.getId())
                        .then()
                        .statusCode(SC_OK));
    }

    public static Response createTriangle(TriangleRequest body) {
        return getAuthRequestSpecification()
                .body(body)
                .post(TRIANGLE);
    }

    public static Response createTriangle(String body) {
        return getAuthRequestSpecification()
                .body(body)
                .post(TRIANGLE);
    }
}

