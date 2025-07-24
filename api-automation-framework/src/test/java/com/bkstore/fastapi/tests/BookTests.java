package com.bkstore.fastapi.tests;

import com.bkstore.fastapi.api.endpoints.Routes;
import com.bkstore.fastapi.api.payloads.AuthToken;
import com.bkstore.fastapi.api.payloads.Book;
import com.bkstore.fastapi.api.payloads.UserLogin;
import com.bkstore.fastapi.api.payloads.UserSignup;
import com.bkstore.fastapi.api.services.AuthService;
import com.bkstore.fastapi.api.services.BookService;
import com.bkstore.fastapi.common.BaseTest;
import com.bkstore.fastapi.utilities.ConfigReader;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.SkipException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

public class BookTests extends BaseTest {

    private static final Logger logger = LogManager.getLogger(BookTests.class);
    private AuthService authService;
    private BookService bookService;

    private static String currentAuthToken = null; // Stores authentication token after successful login
    private static String currentUserEmail = null; // Stores the email of the user who authenticated
    private static int createdBookId; // Stores ID of the book created in positive test for chaining

    @BeforeClass // Runs once before all test methods in this class
    public void setupAuthenticationAndServices() {
        // Initialize service classes
        authService = new AuthService();
        bookService = new BookService();

        logger.info("BookTests setup: Initiating user signup and login to obtain authentication token.");

        // Generate unique user details for signup
        Integer signupUserId = ThreadLocalRandom.current().nextInt(1000, 100000);
        String signupUserEmail = "book_test_user_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        String signupUserPassword = "BookTestPassword123!";
        currentUserEmail = signupUserEmail; // Store the email for test data

        // Create signup payload
        UserSignup signupPayload = new UserSignup(signupUserId, signupUserEmail, signupUserPassword);
        logger.info("Attempting to sign up user: " + signupUserEmail);
        // Call signup API using the base (unauthenticated) requestSpec
        Response signupResponse = authService.signupUser(requestSpec, signupPayload);

        // Handle signup response
        if (signupResponse.statusCode() == 200) {
            logger.info("User signed up successfully: " + signupUserEmail);
        } else if (signupResponse.statusCode() == 409) {
            logger.warn("User '" + signupUserEmail + "' already exists. Proceeding to login.");
        } else {
            logger.error("User signup failed with status: " + signupResponse.statusCode() + ". Response: " + signupResponse.asString());
            // Skip all tests in this class if signup fails
            throw new SkipException("Failed to sign up user for BookTests authentication. Skipping all Book tests.");
        }

        // Create login payload using signup details
        UserLogin loginPayload = new UserLogin(signupUserId, signupUserEmail, signupUserPassword);
        logger.info("Attempting to log in user: " + signupUserEmail);
        // Call login API using the base (unauthenticated) requestSpec
        Response loginResponse = authService.loginUser(requestSpec, loginPayload);

        // Handle login response
        if (loginResponse.statusCode() == 200) {
            // Extract token from response
            AuthToken tokenObject = loginResponse.as(AuthToken.class);
            currentAuthToken = tokenObject.getAccessToken();
            // Check if token is valid
            if (currentAuthToken != null && !currentAuthToken.isEmpty()) {
                logger.info("Successfully obtained Auth Token for " + signupUserEmail + ". Token (first 10 chars): " + currentAuthToken.substring(0, Math.min(currentAuthToken.length(), 10)) + "...");
                // IMPORTANT: Do NOT modify the static 'requestSpec' here.
                // The token will be passed to BookService methods for each authenticated request.
            } else {
                logger.error("Auth Token is null or empty after login.");
                throw new SkipException("Failed to obtain a valid authentication token. Skipping all Book tests.");
            }
        } else {
            logger.error("User login failed with status: " + loginResponse.statusCode() + ". Response: " + loginResponse.asString());
            throw new SkipException("Failed to log in user for BookTests authentication. Skipping all Book tests.");
        }
        logger.info("Authentication setup for BookTests completed successfully.");
    }

    // --- Book CRUD Test Cases ---

    @Test(description = "Verify POST /books creates a new book with authentication", priority = 1)
    public void testCreateBook_Positive() {
        logger.info("Executing testCreateBook_Positive");
        // Skip test if authentication token is not available
        if (currentAuthToken == null) {
            throw new SkipException("Authentication token not available. Skipping test.");
        }

        // Generate dynamic book data for payload
        String bookName = "Test Book " + UUID.randomUUID().toString().substring(0, 6);
        // Use currentUserEmail as the author
        String author = currentUserEmail;
        Integer publishedYear = ThreadLocalRandom.current().nextInt(1900, 2025);
        String bookSummary = "This is a summary of the automated test book for " + bookName + " by " + author;
        // Create Book object with new fields (name, published_year, book_summary)
        Book newBookDetails = new Book(bookName, author, publishedYear, bookSummary);

        // Call create book API, passing the base requestSpec and the authentication token
        Response response = bookService.createBook(requestSpec, newBookDetails, currentAuthToken);

        // Assertions for successful book creation
        response.then()
            .log().all()
            .spec(responseSpec) // Apply common response validations (e.g., status 200, JSON content)
            .body("id", notNullValue()) // Assert 'id' is present
            .body("name", equalTo(bookName)) // Assert 'name' matches
            .body("author", equalTo(author)) // Assert 'auth    or' matches
            .body("published_year", equalTo(publishedYear)) // Assert 'published_year' matches
            .body("book_summary", equalTo(bookSummary)); // Assert 'book_summary' matches

        // Store the created book's ID for chaining in subsequent tests
        createdBookId = response.path("id");
        logger.info("Book created with ID: " + createdBookId + ". Name: " + bookName);
        logger.info("testCreateBook_Positive PASSED.");
    }

    @Test(description = "Verify GET /books/{id} retrieves the created book with authentication", priority = 2, dependsOnMethods = {"testCreateBook_Positive"})
    public void testGetBookById_Positive() {
        logger.info("Executing testGetBookById_Positive for Book ID: " + createdBookId);
        // Skip test if authentication token or created book ID is not available
        if (currentAuthToken == null || createdBookId == 0) {
            throw new SkipException("Authentication token or createdBookId not available. Skipping test.");
        }

        // Call get book by ID API, passing base requestSpec, book ID, and authentication token
        Response response = bookService.getBook(requestSpec, createdBookId, currentAuthToken);

        // Assertions for successful book retrieval
        response.then()
            .log().all()
            .spec(responseSpec) // Apply common response validations
            .body("id", equalTo(createdBookId)) // Assert ID matches
            .body("name", notNullValue()); // Assert 'name' is present
        logger.info("testGetBookById_Positive PASSED.");
    }

    @Test(description = "Verify PUT /books/{id} updates an existing book with authentication", priority = 3, dependsOnMethods = {"testCreateBook_Positive"})
    public void testUpdateBookById_Positive() {
        logger.info("Executing testUpdateBookById_Positive for Book ID: " + createdBookId);
        // Skip test if authentication token or created book ID is not available
        if (currentAuthToken == null || createdBookId == 0) {
            throw new SkipException("Authentication token or createdBookId not available. Skipping test.");
        }

        // Generate updated book data
        String updatedBookName = "Updated Test Book " + UUID.randomUUID().toString().substring(0, 6);
        // Use currentUserEmail as the author
        String updatedAuthor = currentUserEmail;
        Integer updatedPublishedYear = ThreadLocalRandom.current().nextInt(2000, 2024);
        String updatedBookSummary = "This is an updated summary of the automated test book " + updatedBookName + " by " + updatedAuthor;

        // Create updated Book object payload (including ID if API expects it for PUT)
        Book updatedBookPayload = new Book(createdBookId, updatedBookName, updatedAuthor, updatedPublishedYear, updatedBookSummary);

        // Call update book API, passing base requestSpec, book ID, payload, and authentication token
        Response response = bookService.updateBook(requestSpec, createdBookId, updatedBookPayload, currentAuthToken);

        // Assertions for successful book update
        response.then()
            .log().all()
            .spec(responseSpec) // Apply common response validations
            .body("id", equalTo(createdBookId)) // Assert ID matches
            .body("name", equalTo(updatedBookName)) // Assert 'name' matches
            .body("author", equalTo(updatedAuthor)) // Assert 'author' matches
            .body("published_year", equalTo(updatedPublishedYear)) // Assert 'published_year' matches
            .body("book_summary", equalTo(updatedBookSummary)); // Assert 'book_summary' matches
        logger.info("testUpdateBookById_Positive PASSED.");
    }

    @Test(description = "Verify DELETE /books/{id} deletes a book with authentication", priority = 4, dependsOnMethods = {"testCreateBook_Positive"})
    public void testDeleteBookById_Positive() {
        logger.info("Executing testDeleteBookById_Positive for Book ID: " + createdBookId);
        // Skip test if authentication token or created book ID is not available
        if (currentAuthToken == null || createdBookId == 0) {
            throw new SkipException("Authentication token or createdBookId not available. Skipping test.");
        }

        // Call delete book API, passing base requestSpec, book ID, and authentication token
        Response response = bookService.deleteBook(requestSpec, createdBookId, currentAuthToken);

        // Assertions for successful book deletion
        response.then()
            .log().all()
            .statusCode(200)
            .body("message",equalTo("Book deleted successfully")); // Expect 204 No Content for successful deletion

        logger.info("Verifying book is deleted by attempting GET all books and checking for absence of ID: " + createdBookId);
        // Get all books after deletion to verify the deleted book is no longer in the list
        Response allBooksResponse = bookService.getBook(requestSpec, createdBookId, currentAuthToken);

        allBooksResponse.then()
            .log().all()
            .statusCode(404) // Expect 200 OK for getting a valid book
            .body("detail",equalTo("Book not found")); // Assert that the list of IDs does NOT contain the deleted book's ID
        logger.info("testDeleteBookById_Positive PASSED.");
    }

    @Test(description = "Verify GET /books/ retrieves all books with authentication", priority = 5)
    public void testGetAllBooks_Positive() {
        logger.info("Executing testGetAllBooks_Positive");
        // Skip test if authentication token is not available
        if (currentAuthToken == null) {
            throw new SkipException("Authentication token not available. Skipping test.");
        }

        // Call get all books API, passing base requestSpec and authentication token
        Response response = bookService.getAllBooks(requestSpec, currentAuthToken);

        // Assertions for successful retrieval of all books
        response.then()
            .log().all()
            .spec(responseSpec) // Apply common response validations
            .body("$", instanceOf(java.util.List.class)) // Assert response is a JSON array
            .body("size()", greaterThanOrEqualTo(0)); // Assert array is not empty or has expected size
        logger.info("testGetAllBooks_Positive PASSED.");
    }

    // --- Negative Scenarios for Book Operations (No Auth, Invalid ID, etc.) ---

    @Test(description = "Verify POST /books without authentication returns 401 Unauthorized", priority = 6)
    public void testCreateBook_NoAuth_Negative() {
        logger.info("Executing testCreateBook_NoAuth_Negative");

        // Generate dynamic book data for payload - the unauthorized request
        String bookName = "Test Book " + UUID.randomUUID().toString().substring(0, 6);
        // Use currentUserEmail as the author
        String author = currentUserEmail;
        Integer publishedYear = ThreadLocalRandom.current().nextInt(1900, 2025);
        String bookSummary = "This is a summary of the automated test book for " + bookName + " by " + author;

        Book newBook = new Book(bookName, author, publishedYear, bookSummary);

        // Create a new RequestSpecification *without* the Authorization header for this specific test
        RequestSpecification unauthenticatedRequestSpec = new RequestSpecBuilder()
            .setBaseUri(ConfigReader.getProperty("base.uri"))
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

        // Call create book API, explicitly passing null for the token to simulate no authentication
        // The BookService method will attempt to not supply Auth in header, which should result in 401.
                Response response = given()
                    .spec(unauthenticatedRequestSpec)
                    .body(newBook)
                .when()
                    .post(Routes.POST_BOOKS);
                
                response.then()
                    .log().all()
                    .statusCode(403) // Expect 403 Forbidden status
                    .body("detail", equalTo("Not authenticated")); // Assert specific error message         
       

        // bookService.createBook(unauthenticatedRequestSpec, newBook, null)
        //     .then()
        //     .log().all()
        //     .statusCode(403) // Expect 403 Forbidden status
        //     .body("detail", equalTo("Not authenticated")); // Assert specific error message
        logger.info("testCreateBook_NoAuth_Negative PASSED.");
    }

    @Test(description = "Verify GET /books/{id} with non-existent ID returns 404 Not Found", priority = 7)
    public void testGetBookById_NotFound_Negative() {
        logger.info("Executing testGetBookById_NotFound_Negative");
        // Skip test if authentication token is not available
        if (currentAuthToken == null) {
            throw new SkipException("Authentication token not available. Skipping test.");
        }
        int nonExistentId = 99999999; // An ID that is highly unlikely to exist

        // Call get book by ID API for a non-existent ID, passing authentication token
        Response response = bookService.getBook(requestSpec, nonExistentId, currentAuthToken);

        response.then()
            .log().all()
            .statusCode(404) // Expect 404 Not Found status
            .body("detail", containsString("Book not found")); // Assert specific error message
        logger.info("testGetBookById_NotFound_Negative PASSED.");
    }

    @Test(description = "Verify DELETE /books/{id} with non-existent ID returns 404 Not Found", priority = 8)
    public void testDeleteBookById_NotFound_Negative() {
        logger.info("Executing testDeleteBookById_NotFound_Negative");
        // Skip test if authentication token is not available
        if (currentAuthToken == null) {
            throw new SkipException("Authentication token not available. Skipping test.");
        }
        int nonExistentId = 99999998; // Another ID unlikely to exist

        // Call delete book API for a non-existent ID, passing authentication token
        Response response = bookService.deleteBook(requestSpec, nonExistentId, currentAuthToken);

        response.then()
            .log().all()
            .statusCode(404) // Expect 404 Not Found status
            .body("detail", containsString("Book not found")); // Assert specific error message
        logger.info("testDeleteBookById_NotFound_Negative PASSED.");
    }
}