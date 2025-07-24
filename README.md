# FastAPI API Automation Framework

## 1) Project Overview

This project implements an API automation test framework for a Bookstore App (designed using FastAPI), focusing on comprehensive test coverage for user authentication (signup, login) and book management (Create, Read, Update, Delete operations). The framework is built using Java, leveraging RestAssured for fluent API interactions and TestNG as the testing framework. It is designed with a strong emphasis on maintainability, reliability, and simulating realistic end-to-end user flows.

## 2) Prerequisites

Before you begin, ensure your system has the following software installed:

* **Java Development Kit (JDK) 17 or higher:** This is required to compile and run the Java-based test suite.
    * [Download JDK](https://www.oracle.com/java/technologies/downloads/)
* **Apache Maven 3.6.3 or higher:** Maven is used for project build automation and dependency management.
    * [Download Maven](https://maven.apache.org/download.cgi)
* **FastAPI Application:** The target FastAPI application must be running and accessible. By default, the tests expect it to be available at `http://127.0.0.1:8000`. Please refer to the FastAPI project's own `README.md` for instructions on how to set up and launch the API server.

## 3) Framework Setup

To set up the automation framework on your local machine:

1.  **Clone the Repository:**
    Navigate to your desired directory and clone this project's GitHub repository:
    ```bash
    git clone <Your-GitHub-Repo-Link>
    cd <your-project-folder>
    ```

2.  **Install Dependencies:**
    Maven will automatically download all necessary dependencies defined in `pom.xml` when you build the project for the first time.

## 4) Pre-Test Run Procedures

Before executing the tests, ensure the following:

* **FastAPI Application Status:** Verify that your FastAPI application is running successfully and is accessible at the `base.uri` specified in your configuration.
* **Configuration File (`config.properties`):**
    * Open the `src/test/com/bkstore/fastapi/resources/configs/config.properties` file.
    * Confirm that the `base.uri` property is set correctly to your FastAPI application's endpoint (e.g., `base.uri=http://127.0.0.1:8000`).

## 5) Test Execution Flow

The test suite is executed using Maven and TestNG, following a structured flow to ensure dependencies are met and a logical sequence of tests is followed.

1.  **Navigate to Project Root:** Open your terminal or command prompt and change the directory to the root of this project (where `pom.xml` is located).
2.  **Execute Tests:** Run the following Maven command:
    ```bash
    mvn clean install
    ```
    * `clean`: Cleans the `target` directory from any previous build artifacts.
    * `install`: Compiles the source code, runs the test suite, and packages the project.

    Alternatively, to only execute tests without building the artifact:
    ```bash
    mvn test
    ```

### Test Execution Sequence:

The `testng.xml` file orchestrates the execution order of the test classes:

* **`HealthCheckTests.java`**: Runs first to verify the basic availability and health of the API.
* **`UserTests.java`**: Executes authentication-related tests (user signup, login), ensuring a user is created and an auth token is obtained for subsequent tests.
* **`BookTests.java`**: Runs the CRUD operations for books, utilizing the authentication token from `UserTests`.

## 6) Framework Structure Description

The framework is organized into a modular and intuitive structure, promoting maintainability and reusability. Below is a visual representation of the project's key directories and packages, followed by a description of each component.

* Framework Folder-level Structure Diagram : 
├───.github
│   └───workflows
├───logs
├───reports
├───src
│   └───test
│       ├───java
│       │   └───com
│       │       └───bkstore
│       │           └───fastapi
│       │               ├───api
│       │               │   ├───endpoints
│       │               │   ├───payloads
│       │               │   └───services
│       │               ├───common
│       │               ├───listeners
│       │               ├───tests
│       │               │   └───common
│       │               └───utilities
│       └───resources
│           ├───configs
│           └───testsuites
└───target
    ├───generated-sources
    │   └───annotations
    ├───generated-test-sources
    │   └───test-annotations
    ├───maven-status
    │   └───maven-compiler-plugin
    │       └───testCompile
    │           └───default-testCompile
    ├───surefire-reports
    │   ├───junitreports
    │   └───RestAssuredSuite
    └───test-classes
        ├───com
        │   └───bkstore
        │       └───fastapi
        │           ├───api
        │           │   ├───endpoints
        │           │   ├───payloads
        │           │   └───services
        │           ├───common
        │           ├───listeners
        │           ├───tests
        │           │   └───common
        │           └───utilities
        ├───configs
        └───testsuites

* Framework project files details:

* **`src/main/java`**:
    * Typically for application source code. In a test automation project, it might contain utilities if they are built as part of the main project.
* **`src/main/resources`**:
    * Contains non-code resources, primarily `config.properties` for environment-specific configurations.
* **`src/test/java`**: This is the core of the automation framework, housing all test-related Java code.
    * **`com.bkstore.fastapi.tests`**: Contains the actual test classes (`HealthCheckTests.java`, `UserTests.java`, `BookTests.java`). These classes define the test scenarios and assertions for the API endpoints.
    * **`com.bkstore.fastapi.api`**: Houses components directly related to interacting with the API.
        * **`payloads`**: Contains Plain Old Java Objects (POJOs) like `UserSignup.java`, `UserLogin.java`, `Book.java`, `AuthToken.java`. These classes represent the request and response bodies (or parts of them) for various API endpoints, facilitating JSON serialization/deserialization.
        * **`services`**: Contains service layer classes (`AuthService.java`, `BookService.java`). These classes encapsulate the HTTP request logic for specific API domains, abstracting away the low-level RestAssured details from the test classes.
        * **`endpoints`**: Contains the `Routes.java` class, which defines constant strings for all API endpoint paths. This centralizes endpoint management and makes updates easier.
    * **`com.bkstore.fastapi.common`**: Contains common utilities and base classes.
        * **`BaseTest.java`**: A base class that all test classes extend. It handles common setup like initializing `RequestSpecification` and shared service instances, ensuring consistency and reducing code duplication.
    * **`com.bkstore.fastapi.utilities`**: Contains utility classes that provide helper functions.
        * **`ConfigReader.java`**: Responsible for reading properties from `config.properties`, allowing externalization of configurations.
    * **`com.bkstore.fastapi.listeners`**: Contains custom TestNG listeners.
        * **`ExtentReporterNG.java`**: A custom listener that integrates ExtentReports to generate detailed, interactive HTML test reports.
* **`pom.xml`**: The Maven Project Object Model file. It defines project dependencies (e.g., RestAssured, TestNG, Jackson, Log4j), build plugins, and project metadata.
* **`testng.xml`**: The TestNG suite XML file. It defines the test suite, includes listeners, and specifies which test classes to execute and in what order.
* **`target`**: The directory where compiled classes, test results, and generated reports are stored after a Maven build.

## 7) Test Design Strategy

Our API automation framework employs a comprehensive testing strategy designed for thorough coverage, reliability, and maintainability.

* **Endpoint-Centric Organization:** Test classes are logically grouped by API domain (e.g., `UserTests.java` for authentication, `BookTests.java` for book management). This modular structure enhances clarity, eases navigation, and simplifies maintenance.
* **Positive and Negative Scenarios:** Each critical API endpoint is rigorously validated through both positive and negative test cases:
    * **Positive Flows:** These verify the API's intended behavior under valid conditions, ensuring successful operations (e.g., valid user signup, successful book creation).
    * **Negative Flows:** These are crucial for validating the API's error handling capabilities. They cover scenarios with invalid inputs, missing required fields, attempts at unauthorized access, and operations on non-existent resources. This also includes specific tests to confirm expected error messages or status codes (e.g., `500 Internal Server Error` for invalid email formats, `400 Bad Request` for incorrect login credentials).
* **Request Chaining:** We simulate realistic user interactions by chaining API calls:
    * A user is first registered via the `/signup` endpoint.
    * Immediately following, the same user's credentials are used to log in via the `/login` endpoint.
    * The `access_token` obtained from this successful login is then stored statically and dynamically utilized by all subsequent book-related operations in `BookTests.java` to ensure authorized access. This validates the entire end-to-end user flow, from authentication to resource management.
* **Service Layer Abstraction:** All direct API calls are encapsulated within dedicated service classes (`AuthService`, `BookService`). This abstraction separates the HTTP request construction logic from the actual test scenarios, making test methods cleaner, more readable, and focused purely on verifying API behavior.
* **Robust Assertions:** RestAssured's fluent assertion API in conjunction with Hamcrest matchers is used for precise and expressive validations on HTTP status codes, response body content, and specific error messages.
* **TestNG Features for Control:** The framework leverages key TestNG annotations such as `@BeforeClass` (for setup), `@Test` (to define test methods), `priority` (to define execution order within a class), and `dependsOnMethods` (to manage inter-test dependencies). These features contribute significantly to a stable and repeatable test suite.

## 8) Test Execution Environments

The framework is designed to support different API environments (e.g., development, QA, production) through a simple configuration mechanism.

* **Configuration Management (`ConfigReader`):** The `src/main/resources/config.properties` file acts as the central place for environment-specific configurations, such as the `base.uri` of the API.
* **Switching Environments:** To test against a different environment, you would simply update the `base.uri` value in `config.properties` to point to the desired API endpoint. For more complex setups involving multiple environments, you could expand `ConfigReader` to load environment-specific property files (e.g., `dev.properties`, `qa.properties`) based on a system property or Maven profile passed during test execution.

## 9) Challenges Encountered & Its Resolution

During the development of this framework, several challenges were addressed to ensure its robustness and accuracy:

* **Dynamic `id` Handling in User Payloads:**
    * **Challenge:** The API's user signup/login endpoints sometimes generated IDs dynamically, and initial payload POJOs implicitly expected an `id` field in requests. This led to issues when creating new users where `id` is auto-assigned by the server.
    * **Resolution:** POJOs were refactored to include overloaded constructors. This allowed requests to be made without explicitly providing an `id`, aligning with the API's behavior of auto-generating user IDs. The generated `id` is then extracted from the response for validation.
* **Authentication Token Propagation for Chained Tests:**
    * **Challenge:** Subsequent API calls (especially in `BookTests.java`) required an authentication token, which is obtained only after a successful user login. Re-logging in for every test was inefficient and unrealistic.
    * **Resolution:** A `public static String authTokenFromLogin` variable was introduced in `UserTests.java`. After a successful user login (in `testUserLogin_Positive_Chained`), this variable stores the obtained `access_token`. `BookTests.java` then accesses this static variable in its `@BeforeClass` setup, effectively propagating the token across different test classes without needing re-authentication for each API call.
* **Variations in API Error Response Structures:**
    * **Challenge:** The FastAPI application returned different error response structures for various negative scenarios. For instance, Pydantic validation errors (often 422 status) provided detailed JSON structures with `loc` and `msg` fields, while other errors (like invalid email format in signup) returned a simpler, direct "Internal Server Error" string with a 500 status.
    * **Resolution:** Test assertions were tailored to match the expected error response format for each specific negative test case. For structured errors, JSONPath assertions were used. For direct string responses, a simple `body(equalTo("Expected Error String"))` assertion was employed, ensuring accurate validation regardless of the API's error response format.

## 10) Sample Test Report

After the test execution (`mvn clean install` or `mvn test`), a detailed and interactive HTML test report is generated by ExtentReports as well as by default Maven-Surefire reports is also getting captured.

* **Location:** You can find the ExtentReports at : `reports/Test-Report-yyyy.mm.dd.hh.mm.ss.html` and SureFire reports is located at : `target/surefire-reports/emailable-report.html`
* **To view the report:** Open the `.html` file in any web browser.