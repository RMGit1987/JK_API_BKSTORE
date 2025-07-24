package com.bkstore.fastapi.utilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnvironmentManager {
    private static final String DEFAULT_ENVIRONMENT = "dev"; // Default if not specified
    private static String currentEnvironment;

    // Logger for logging events
    private static final Logger logger = LogManager.getLogger(EnvironmentManager.class);

    public static String getEnvironment() {
        if (currentEnvironment == null) {
            // 1. Try to get from System Property (e.g., -Denv=qa)
            currentEnvironment = System.getProperty("env");
            if (currentEnvironment == null) {
                // 2. Try to get from Environment Variable (e.g., export TEST_ENV=prod)
                currentEnvironment = System.getenv("TEST_ENV");
            }
            if (currentEnvironment == null) {
                // 3. Fallback to default
                currentEnvironment = DEFAULT_ENVIRONMENT;
            }
            logger.info("Active Environment: " + currentEnvironment.toUpperCase());
        }
        return currentEnvironment.toLowerCase(); // Ensure lowercase for filename matching
    }
}