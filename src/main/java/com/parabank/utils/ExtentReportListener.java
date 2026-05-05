// File: src/main/java/com/parabank/utils/ExtentReportListener.java
package com.parabank.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TestNG ITestListener implementation that hooks into the test lifecycle
 * to automatically build a rich HTML report using ExtentReports.
 *
 * - Creates one ExtentTest entry per @Test method
 * - Attaches screenshots to failing tests automatically
 * - Produces a single HTML report at test-output/ExtentReports/
 *
 * Registered in testng.xml under <listeners>, so no annotation is needed
 * in individual test classes.
 */
public class ExtentReportListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(ExtentReportListener.class);

    // Static fields are shared across all test instances — intentional for a single report
    private static ExtentReports extentReports;

    // ThreadLocal so parallel tests don't overwrite each other's ExtentTest reference
    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();

    // -------------------------------------------------------------------------
    // Suite-level lifecycle
    // -------------------------------------------------------------------------

    @Override
    public void onStart(ITestContext context) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String reportsDir = ConfigReader.getReportsDir();
        String reportPath = reportsDir + File.separator + "ParaBank_Report_" + timestamp + ".html";

        try {
            Files.createDirectories(Paths.get(reportsDir));
        } catch (IOException e) {
            logger.error("Could not create reports directory: {}", e.getMessage());
        }

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("ParaBank Automation Report");
        sparkReporter.config().setReportName("ParaBank Full Regression Suite");
        sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);
        extentReports.setSystemInfo("Project", "ParaBank Testing Project");
        extentReports.setSystemInfo("Tester", "MFK Group");
        extentReports.setSystemInfo("Environment", "Production / Web");
        extentReports.setSystemInfo("Browser", ConfigReader.getBrowser());

        logger.info("ExtentReport initialized. Report will be saved to: {}", reportPath);
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extentReports != null) {
            extentReports.flush();
            logger.info("ExtentReport finalized and written to disk.");
        }
    }

    // -------------------------------------------------------------------------
    // Test-level lifecycle
    // -------------------------------------------------------------------------

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();

        ExtentTest test = extentReports.createTest(testName, description);
        extentTestThreadLocal.set(test);
        logger.info("Starting test: {}", testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest test = extentTestThreadLocal.get();
        if (test != null) {
            test.log(Status.PASS, "Test PASSED");
        }
        logger.info("PASSED: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = extentTestThreadLocal.get();
        String testName = result.getMethod().getMethodName();

        if (test != null) {
            // Log the exception stack trace
            test.log(Status.FAIL, "Test FAILED: " + result.getThrowable().getMessage());
            test.log(Status.FAIL, result.getThrowable());

            // Capture and attach screenshot
            try {
                WebDriver driver = DriverFactory.getDriver();
                String screenshotPath = ScreenshotUtil.captureScreenshot(driver, testName);
                if (screenshotPath != null) {
                    test.addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
                }
            } catch (Exception e) {
                logger.warn("Could not capture screenshot for failed test '{}': {}", testName, e.getMessage());
            }
        }
        logger.error("FAILED: {}", testName, result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = extentTestThreadLocal.get();
        if (test != null) {
            test.log(Status.SKIP, "Test SKIPPED: " + result.getThrowable().getMessage());
        }
        logger.warn("SKIPPED: {}", result.getMethod().getMethodName());
    }

    /**
     * Exposes the current thread's ExtentTest so BaseTest can log custom steps.
     */
    public static ExtentTest getExtentTest() {
        return extentTestThreadLocal.get();
    }
}
