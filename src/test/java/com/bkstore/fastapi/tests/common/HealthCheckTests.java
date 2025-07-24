package com.bkstore.fastapi.tests.common;

import com.bkstore.fastapi.api.endpoints.Routes;
import com.bkstore.fastapi.common.BaseTest;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class HealthCheckTests extends BaseTest {

    @Test(description = "Verify API health check endpoint is reachable and returns 200 OK")
    public void testApiHealthCheck() {
        Response response = given()
                                .spec(requestSpec)
                            .when()
                                .get(Routes.GET_HEALTH);

        response.then()
                .statusCode(200)
                .log().ifValidationFails(); // Log response if validation fails
                // .log().body();

        // Assuming your health endpoint returns a JSON with "status": "OK"
        Assert.assertEquals(response.jsonPath().getString("status"), "up", "Health check status mismatch");
    }
}