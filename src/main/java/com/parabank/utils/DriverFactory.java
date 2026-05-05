// File: src/main/java/com/parabank/utils/DriverFactory.java
package com.parabank.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * Factory class responsible for creating and managing WebDriver instances.
 *
 * Uses ThreadLocal<WebDriver> to ensure each test thread gets its own
 * isolated driver instance — this is critical for safe parallel test execution.
 */
public class DriverFactory {

    private static final Logger logger = LogManager.getLogger(DriverFactory.class);

    // ThreadLocal ensures each thread (test) has its own driver — parallel-safe
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverFactory() {}

    /**
     * Initializes the WebDriver based on the browser specified in config.properties.
     * WebDriverManager auto-downloads the correct driver binary — no manual setup needed.
     *
     * @param browser Browser name: "chrome", "firefox", or "edge"
     */
    public static void initDriver(String browser) {
        WebDriver driver;
        boolean headless = ConfigReader.isHeadless();

        logger.info("Initializing '{}' browser. Headless mode: {}", browser, headless);

        switch (browser.toLowerCase().trim()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) firefoxOptions.addArguments("--headless");
                driver = new FirefoxDriver(firefoxOptions);
                break;

            case "edge":
                WebDriverManager.edgedriver().setup();
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) edgeOptions.addArguments("--headless");
                driver = new EdgeDriver(edgeOptions);
                break;

            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                }
                chromeOptions.addArguments("--window-size=1920,1080");
                chromeOptions.addArguments("--disable-extensions");
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        // Configure global timeouts
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(ConfigReader.getPageLoadTimeout())
        );

        driverThreadLocal.set(driver);
        logger.info("WebDriver initialized successfully for thread: {}", Thread.currentThread().getId());
    }

    /**
     * Returns the WebDriver instance for the current thread.
     * Throws an exception if the driver has not been initialized (fail-fast).
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException(
                "WebDriver not initialized for this thread. Call DriverFactory.initDriver() first."
            );
        }
        return driver;
    }

    /**
     * Quits the browser and removes the driver from ThreadLocal to prevent memory leaks.
     * Must be called in the @AfterMethod of BaseTest.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            logger.info("Quitting WebDriver for thread: {}", Thread.currentThread().getId());
            driver.quit();
            driverThreadLocal.remove();
        }
    }
}
