package com.bkstore.fastapi.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties;

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);

    // Static block to load properties once
    static {
        loadProperties(EnvironmentManager.getEnvironment());
    }

    public static void loadProperties(String environment) {
        properties = new Properties();
        String configFileName = "config-" + environment + ".properties";
        String configFilePath = "src/test/resources/configs/" + configFileName;

        try (FileInputStream fis = new FileInputStream(configFilePath)) {
            properties.load(fis);
            logger.info("Loaded properties from: " + configFilePath); // For debugging
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading " + configFileName + ": " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            // Optionally, try to read from system properties if not found in file
            value = System.getProperty(key);
        }
        if (value == null) {
            // System.err.println("Warning: Property '" + key + "' not found in current environment config.");
            logger.error("Warning: Property '" + key + "' not found in current environment config.");
        }
        return value;
    }
}