// File: src/main/java/com/parabank/utils/ConfigReader.java
package com.parabank.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton utility class to load and expose properties from config.properties.
 * Centralizes configuration so that changing a URL or credential requires
 * editing only one file, not every test class.
 */
public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static final String CONFIG_PATH = "src/test/resources/config.properties";
    private static Properties properties;

    // Private constructor enforces singleton pattern
    private ConfigReader() {}

    /**
     * Lazily initializes and returns the properties object.
     * Thread-safe due to synchronized keyword — safe for parallel test runs.
     */
    public static synchronized Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
                properties.load(fis);
                logger.info("Configuration loaded from: {}", CONFIG_PATH);
            } catch (IOException e) {
                logger.error("FATAL: Cannot load config.properties from path: {}", CONFIG_PATH);
                throw new RuntimeException("Failed to load configuration file: " + CONFIG_PATH, e);
            }
        }
        return properties;
    }


    public static String getBaseUrl() {
        return getProperties().getProperty("base.url");
    }

    public static String getBrowser() {
        return getProperties().getProperty("browser", "chrome");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperties().getProperty("headless", "false"));
    }

    public static int getExplicitWait() {
        return Integer.parseInt(getProperties().getProperty("explicit.wait", "15"));
    }

    public static int getPageLoadTimeout() {
        return Integer.parseInt(getProperties().getProperty("page.load.timeout", "30"));
    }

    public static String getValidUsername() {
        return getProperties().getProperty("valid.username");
    }

    public static String getValidPassword() {
        return getProperties().getProperty("valid.password");
    }

    public static String getInvalidUsername() {
        return getProperties().getProperty("invalid.username");
    }

    public static String getInvalidPassword() {
        return getProperties().getProperty("invalid.password");
    }

    public static String getReportsDir() {
        return getProperties().getProperty("reports.dir", "test-output/ExtentReports");
    }

    public static String getScreenshotsDir() {
        return getProperties().getProperty("screenshots.dir", "test-output/screenshots");
    }
}
