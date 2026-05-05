// File: src/main/java/com/parabank/base/BaseTest.java
package com.parabank.base;

import com.parabank.utils.ConfigReader;
import com.parabank.utils.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * Abstract base class for all test classes in the framework.
 *
 * Responsibilities:
 *  - Initialize and tear down WebDriver via DriverFactory (ThreadLocal-safe)
 *  - Navigate to the base URL before every test
 *  - Provide a protected 'driver' reference to subclasses
 *
 * Every test class MUST extend this class. No other class should touch
 * WebDriver lifecycle or configuration directly.
 */
public abstract class BaseTest {

    protected static final Logger logger = LogManager.getLogger(BaseTest.class);

    // Protected so subclass test classes can access the driver directly
    protected WebDriver driver;

    /**
     * Runs before each @Test method.
     * 1. Creates a fresh browser session
     * 2. Navigates to the application's base URL
     *
     * @param method Injected by TestNG — used to log the test method name
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        logger.info("======= Setting up test: {} =======", method.getName());

        // Initialize the driver for this thread using the configured browser
        DriverFactory.initDriver(ConfigReader.getBrowser());
        driver = DriverFactory.getDriver();

        // Open the application
        String baseUrl = ConfigReader.getBaseUrl();
        driver.get(baseUrl);
        logger.info("Navigated to base URL: {}", baseUrl);
    }

    /**
     * Runs after each @Test method, regardless of pass/fail/skip.
     * Quits the browser and cleans up the ThreadLocal driver reference.
     *
     * @param method Injected by TestNG — used to log the test method name
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(Method method) {
        logger.info("======= Tearing down test: {} =======", method.getName());
        DriverFactory.quitDriver();
    }
}
