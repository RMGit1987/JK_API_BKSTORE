package com.bkstore.fastapi.api.endpoints;

public class Routes {

    // No BASE_URI constant needed here if set in BaseTest
    public static final String GET_HEALTH = "/health";
    public static final String SIGNUP_USER = "/signup";
    public static final String GET_LOGIN_TOKEN = "/login";
    public static final String GET_BOOKS = "/books/";
    public static final String POST_BOOKS = "/books/";
    public static final String UPDATE_BOOK_BY_ID = "/books/{id}";
    public static final String DELETE_BOOK_BY_ID = "/books/{id}";
    public static final String GET_BOOK_BY_ID = "/books/{id}";
    // ... other direct routes like /items, /orders

}
