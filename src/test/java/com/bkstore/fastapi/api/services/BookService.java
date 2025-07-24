package com.bkstore.fastapi.api.services;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.bkstore.fastapi.api.endpoints.Routes;
import com.bkstore.fastapi.api.payloads.Book;

import static io.restassured.RestAssured.given;

public class BookService {

    // Now accepts authToken parameter to be applied dynamically
    public Response getAllBooks(RequestSpecification reqSpec, String authToken) {
        return given()
                .spec(reqSpec)
                .header("Authorization", "Bearer " + authToken) // Add auth header here
            .when()
                .get(Routes.GET_BOOKS);
    }

    // Now accepts authToken parameter
    public Response createBook(RequestSpecification reqSpec, Book bookPayload, String authToken) {
        return given()
                .spec(reqSpec)
                .header("Authorization", "Bearer " + authToken) // Add auth header here
                .body(bookPayload)
            .when()
                .post(Routes.POST_BOOKS);
    }

    // Now accepts authToken parameter
    public Response getBook(RequestSpecification reqSpec, int bookId, String authToken) {
        return given()
                .spec(reqSpec)
                .header("Authorization", "Bearer " + authToken) // Add auth header here
                .pathParam("id", bookId)
            .when()
                .get(Routes.GET_BOOK_BY_ID);
    }

    // Now accepts authToken parameter
    public Response updateBook(RequestSpecification reqSpec, int bookId, Book bookPayload, String authToken) {
        return given()
                .spec(reqSpec)
                .header("Authorization", "Bearer " + authToken) // Add auth header here
                .pathParam("id", bookId)
                .body(bookPayload)
            .when()
                .put(Routes.UPDATE_BOOK_BY_ID);
    }

    // Now accepts authToken parameter
    public Response deleteBook(RequestSpecification reqSpec, int bookId, String authToken) {
        return given()
                .spec(reqSpec)
                .header("Authorization", "Bearer " + authToken) // Add auth header here
                .pathParam("id", bookId)
            .when()
                .delete(Routes.DELETE_BOOK_BY_ID);
    }
}