package com.bkstore.fastapi.tests;

import com.bkstore.fastapi.api.payloads.AuthToken;
import com.bkstore.fastapi.api.payloads.UserLogin;
import com.bkstore.fastapi.api.payloads.UserSignup;
import com.bkstore.fastapi.api.services.AuthService;
import com.bkstore.fastapi.common.BaseTest; // Extend BaseTest

import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.SkipException; // For conditional skipping

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadLocalRandom;
import java.util.UUID;

import static org.hamcrest.Matchers.*;

// Renamed class to reflect comprehensive auth/user testing
public class UserTests extends BaseTest {

    private static final Logger logger = LogManager.getLogger(UserTests.class); // Logger for this class
    private AuthService authService; // Service layer for auth operations

    // Static variables to store details of the user for request chaining
    private static Integer signedUpUserId = null;
    private static String signedUpUserEmail = null;
    private static String signedUpUserPassword = null;
    private static String authTokenFromLogin = null; // Stores the access token after successful chained login

    @BeforeClass // Runs once before any test method in this class
    public void setupAuthService() {
        // BaseTest's @BeforeSuite already handles requestSpec initialization.
        // No need to call super.setup() here.
        authService = new AuthService(); // Initialize the authentication service
        logger.info("AuthAndUserTests setup: AuthService initialized.");
    }


    // --- POST /signup Test Cases ---

    @Test(description = "Positive: Verify POST /signup creates a new user with random ID", priority = 1)
    public void testUserSignup_Positive() {
        logger.info("Executing testUserSignup_Positive: Creating a new user.");

        Integer randomId = ThreadLocalRandom.current().nextInt(1000, 100000); // Generate a random ID
        String uniqueEmail = "user_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com"; // Generate unique email
        String password = "UserPassword@123"; // Strong password

        // Create UserSignup payload
        UserSignup user = new UserSignup(randomId, uniqueEmail, password);
        logger.info("Attempting signup for ID: " + user.getId() + ", Email: " + user.getEmail());

        // Call the signup API via AuthService
        Response response = authService.signupUser(requestSpec, user);

        // Assertions for positive signup
        response.then()
            .log().all() // Log entire request and response for debugging
            .statusCode(200) // Assuming 200 OK for successful creation as per your previous logs
            .body("message", equalTo("User created successfully")); // Adjust based on actual API response

        // Store details for subsequent chained tests
        signedUpUserId = randomId;
        signedUpUserEmail = uniqueEmail;
        signedUpUserPassword = password;

        logger.info("User signup successful. User ID: " + signedUpUserId + ", Email: " + signedUpUserEmail);
        logger.info("testUserSignup_Positive PASSED. Details stored for chaining.");
    }

    @Test(description = "Negative: POST /signup with existing email returns 400 status", priority = 2, dependsOnMethods = {"testUserSignup_Positive"})
    public void testUserSignup_ExistingEmail_Negative() {
        // Skip this test if the prerequisite positive signup failed
        if (signedUpUserEmail == null) {
            logger.warn("Skipping testUserSignup_ExistingEmail_Negative: Pre-requisite user email not available.");
            throw new SkipException("Pre-requisite user signup failed or email not stored.");
        }

        logger.info("Executing testUserSignup_ExistingEmail_Negative with email: " + signedUpUserEmail);

        Integer randomId = ThreadLocalRandom.current().nextInt(1000, 100000); // New random ID for the attempt
        String password = "AnotherPassword@456";

        // Create signup payload using the existing email
        UserSignup user = new UserSignup(randomId, signedUpUserEmail, password);

        // Call the signup API via AuthService
        Response response = authService.signupUser(requestSpec, user);

        // Assertions for existing email signup (negative)
        response.then()
            .log().all()
            .statusCode(400) // Expect 400 Conflict if email already registered
            .body("detail", containsString("Email already registered")); // Adjust message if different
        logger.info("testUserSignup_ExistingEmail_Negative PASSED.");
    }

    @Test(description = "Negative: POST /signup with missing email returns 500 internal server error", priority = 3)
    public void testUserSignup_MissingEmail_Negative() {
        logger.info("Executing testUserSignup_MissingEmail_Negative.");

        Integer randomId = ThreadLocalRandom.current().nextInt(1000, 100000);
        String password = "AnotherPassword@456";

        // Create signup payload with null email
        UserSignup user = new UserSignup(randomId, password);

        // Call the signup API via AuthService
        Response response = authService.signupUser(requestSpec, user);

        // Assertions for missing email
        response.then()
            .log().all()
            .statusCode(500) // FastAPI default for validation errors
            .body(equalTo("Internal Server Error"));
        logger.info("testUserSignup_MissingEmail_Negative PASSED.");
    }

    @Test(description = "Negative: POST /signup with null password returns 500", priority = 4)
    public void testUserSignup_MissingPassword_Negative() {
        logger.info("Executing testUserSignup_MissingPassword_Negative.");

        Integer randomId = ThreadLocalRandom.current().nextInt(1000, 100000);
        String uniqueEmail = "no_pass_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        // Create signup payload with null password
        UserSignup user = new UserSignup(randomId, uniqueEmail, null);

        // Call the signup API via AuthService
        Response response = authService.signupUser(requestSpec, user);

        // Assertions for missing password
        response.then()
            .log().all()
            .statusCode(500) // FastAPI default for validation errors
            .body(equalTo("Internal Server Error"));
        logger.info("testUserSignup_MissingPassword_Negative PASSED.");
    }

    @Test(description = "Negative: POST /signup with invalid email format returns 500", priority = 5)
    public void testUserSignup_InvalidEmailFormat_Negative() {
        logger.info("Executing testUserSignup_InvalidEmailFormat_Negative.");

        Integer randomId = ThreadLocalRandom.current().nextInt(1000, 100000);
        String invalidEmail = "invalid-email"; // Invalid format
        String password = "ValidPassword@123";

        // Create signup payload with invalid email
        UserSignup user = new UserSignup(randomId, invalidEmail, password);

        // Call the signup API via AuthService
        Response response = authService.signupUser(requestSpec, user);

        // Assertions for invalid email format
        response.then()
            .log().all()
            .statusCode(500) // FastAPI default for validation errors
            .body(equalTo("Internal Server Error")); // Adjust based on actual API response
        logger.info("testUserSignup_InvalidEmailFormat_Negative PASSED.");
    }

    // --- POST /login Test Cases ---

    @Test(description = "Positive: Verify POST /login logs in the previously signed up user (Chained)", priority = 6, dependsOnMethods = {"testUserSignup_Positive"})
    public void testUserLogin_Positive_Chained() {
        logger.info("Executing testUserLogin_Positive_Chained: Logging in previously signed-up user.");

        // Skip test if prerequisite signup details are not available
        if (signedUpUserId == null || signedUpUserEmail == null || signedUpUserPassword == null) {
            logger.error("Skipping testUserLogin_Positive_Chained: Signup details not available.");
            throw new SkipException("Signup details not available for chained login.");
        }

        // Create UserLogin payload using the details from the successful signup test
        UserLogin loginPayload = new UserLogin(signedUpUserId, signedUpUserEmail, signedUpUserPassword);
        logger.info("Attempting to log in user: ID: " + loginPayload.getId() + ", Email: " + loginPayload.getEmail());

        // Call the login API via AuthService
        Response loginResponse = authService.loginUser(requestSpec, loginPayload);

        // Assertions for positive login
        loginResponse.then()
            .log().all()
            .statusCode(200) // Expect 200 OK for successful login
            .body("access_token", notNullValue()) // Assert that an access token is returned
            .body("token_type", equalTo("bearer")); // Assert token type

        // Store the obtained token for potential future chained requests (e.g., BookTests)
        AuthToken tokenObject = loginResponse.as(AuthToken.class);
        authTokenFromLogin = tokenObject.getAccessToken();

        logger.info("User login successful. Access Token: " + authTokenFromLogin.substring(0, Math.min(authTokenFromLogin.length(), 10)) + "...");
        logger.info("testUserLogin_Positive_Chained PASSED.");
    }

    @Test(description = "Negative: POST /login with invalid credentials returns 400 Bad request", priority = 7)
    public void testUserLogin_InvalidCredentials_Negative() {
        logger.info("Executing testUserLogin_InvalidCredentials_Negative.");

        // Integer randomId = ThreadLocalRandom.current().nextInt(1000, 100000); // Random ID for the invalid attempt
        String invalidEmail = "invalid_login_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        // String wrongPassword = "WrongPassword123!";

        // Create login payload with invalid credentials
        UserLogin loginPayload = new UserLogin(signedUpUserId, invalidEmail, signedUpUserPassword);

        // Call the login API via AuthService
        Response loginResponse = authService.loginUser(requestSpec, loginPayload);

        // Assertions for invalid credentials
        loginResponse.then()
            .log().all()
            .statusCode(400) // Expect 401 Unauthorized
            .body("detail", containsString("Incorrect email or password")); // Adjust message if different
        logger.info("testUserLogin_InvalidCredentials_Negative PASSED.");
    }
}