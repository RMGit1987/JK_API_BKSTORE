package com.bkstore.fastapi.api.services;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.bkstore.fastapi.api.endpoints.Routes;
import com.bkstore.fastapi.api.payloads.UserSignup;
import com.bkstore.fastapi.api.payloads.UserLogin;

import static io.restassured.RestAssured.given;

public class AuthService {

    public Response signupUser(RequestSpecification reqSpec, UserSignup userSignupPayload) {
        return given()
                .spec(reqSpec)
                .body(userSignupPayload)
            .when()
                .post(Routes.SIGNUP_USER);
    }

    public Response loginUser(RequestSpecification reqSpec, UserLogin userLoginPayload) {
        return given()
                .spec(reqSpec)
                .body(userLoginPayload)
            .when()
                .post(Routes.GET_LOGIN_TOKEN);
    }
}