// File: src/main/java/com/parabank/utils/ScreenshotUtil.java
package com.parabank.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility for capturing screenshots on test failures.
 * Screenshots are saved to the directory specified in config.properties
 * and are embedded into the ExtentReport HTML report.
 */
public class ScreenshotUtil {

    private static final Logger logger = LogManager.getLogger(ScreenshotUtil.class);

    private ScreenshotUtil() {}

    /**
     * Captures a screenshot and saves it to the screenshots directory.
     *
     * @param driver   The active WebDriver instance
     * @param testName Name of the test — used as the filename base
     * @return Absolute file path to the saved screenshot, or null on failure
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        // Sanitize testName to avoid illegal filesystem characters
        String sanitizedName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
        String fileName = sanitizedName + "_" + timestamp + ".png";

        String screenshotsDir = ConfigReader.getScreenshotsDir();
        String filePath = screenshotsDir + File.separator + fileName;

        try {
            // Ensure the output directory exists
            Files.createDirectories(Paths.get(screenshotsDir));

            // Cast driver to TakesScreenshot and capture
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshotFile.toPath(), Paths.get(filePath));

            logger.info("Screenshot saved: {}", filePath);
            return filePath;
        } catch (IOException e) {
            logger.error("Failed to save screenshot for test '{}': {}", testName, e.getMessage());
            return null;
        }
    }
}
