package com.bkstore.fastapi.common;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeSuite;
import com.bkstore.fastapi.utilities.ConfigReader;

public class BaseTest {

    protected static RequestSpecification requestSpec;
    protected static ResponseSpecification responseSpec;

    @BeforeSuite
    public void setup() {
        // Read base URI and base path from config.properties
        String baseUri = ConfigReader.getProperty("base.uri");

        // Build Request Specification
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON) // Or other content types if needed
                .log(LogDetail.ALL) // Log all request details for debugging
                .build();

        // Build Response Specification (optional, but good for common validations)
        responseSpec = new ResponseSpecBuilder()
                .expectStatusCode(200) // Default expected status code
                .expectContentType(ContentType.JSON) // Default expected content type
                .log(LogDetail.ALL) // Log all response details for debugging
                .build();
    }
}