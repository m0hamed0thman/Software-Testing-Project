// File: src/test/java/com/parabank/tests/RegistrationTest.java
package com.parabank.tests;

import com.parabank.base.BaseTest;
import com.parabank.pages.RegistrationPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Test class for the Registration module.
 *
 * Scenario: TS_02
 * Test Cases: TC_06 through TC_12
 *
 * Note: TC_10 uses a pre-existing username ("john"). If that user does not
 * exist in the environment, the test may behave differently — update the
 * EXISTING_USERNAME constant to match your environment.
 */
public class RegistrationTest extends BaseTest {

    private RegistrationPage registrationPage;

    // A timestamp suffix ensures the happy path registration uses a unique username
    private static final String UNIQUE_USERNAME =
            "" + (System.currentTimeMillis());

    // Pre-existing user for TC_10 — must already exist in ParaBank
    private static final String EXISTING_USERNAME = "john";

    // =========================================================================
    // Pre-condition: Navigate to the registration page before each test
    // =========================================================================

    @BeforeMethod(alwaysRun = true)
    public void navigateToRegistrationPage() {
        // BaseTest.setUp() already opened the base URL; navigate further to /register.htm
        registrationPage = new RegistrationPage(driver);
        registrationPage.navigateTo();
    }

    // =========================================================================
    // TC_06 — Happy Path — HIGH
    // =========================================================================

    /**
     * TC_06: Verifies that a user can register successfully when all fields
     *        are filled with valid data.
     */
    @Test(
        groups = {"registration"},
        priority = 1,
        description = "TC_06 | TS_02 | [HIGH] Verify successful registration with all valid fields."
    )
    public void testSuccessfulRegistration() {
        registrationPage.fillAndSubmitForm(
            "Automation",          // First Name
            "Tester",              // Last Name
            "123 QA Street",       // Address
            "Test City",           // City
            "NY",                  // State
            "10001",               // Zip Code
            "5551234567",          // Phone
            "123-45-6789",         // SSN
            UNIQUE_USERNAME,       // Username — unique per run
            "Test@1234",           // Password
            "Test@1234"            // Confirm Password
        );

        org.openqa.selenium.support.ui.WebDriverWait wait =
                new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(30));

        // في Parabank، الرابط بيفضل register.htm بس المحتوى بيتغير، فهنستنى لحد ما النص القديم يختفي
        // (يفضل استبدال "By.tagName("h1")" بالـ Locator الصحيح للـ Heading عندك)
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOfElementWithText(
                org.openqa.selenium.By.cssSelector("h1.title"), "Signing up is easy!"
        ));


        String successText = registrationPage.getSuccessMessageText();
        Assert.assertTrue(
            successText.contains("created successfully") || successText.contains("logged in") || successText.contains("Welcome " + UNIQUE_USERNAME),
            "Registration success message was not displayed. Actual heading: '" + successText + "'"
        );

        logger.info("TC_06 PASSED: Registration succeeded for username '{}'.", UNIQUE_USERNAME);
    }

    // =========================================================================
    // TC_07 — Empty Form Submission — HIGH
    // =========================================================================

    /**
     * TC_07: Verifies that submitting a completely empty form shows validation errors.
     */
    @Test(
        groups = {"registration"},
        priority = 2,
        description = "TC_07 | TS_02 | [HIGH] Verify validation errors appear when form is empty."
    )
    public void testRegistrationWithEmptyForm() {
        // Submit without filling anything in
        registrationPage.clickRegister();

        Assert.assertTrue(
            registrationPage.areValidationErrorsDisplayed(),
            "Expected validation errors to appear after empty form submission, but none were found."
        );

        logger.info("TC_07 PASSED: Validation errors displayed for empty form submission.");
    }

    // =========================================================================
    // TC_08 — Missing First Name — HIGH
    // =========================================================================

    /**
     * TC_08: Verifies that omitting First Name prevents registration and shows the
     *        field-specific error message.
     */
    @Test(
        groups = {"registration"},
        priority = 3,
        description = "TC_08 | TS_02 | [HIGH] Verify error when First Name is missing."
    )
    public void testRegistrationMissingFirstName() {
        registrationPage.fillAndSubmitForm(
            "",                         // First Name — intentionally omitted
            "Tester",
            "123 QA Street",
            "Test City",
            "NY",
            "10001",
            "5551234567",
            "123-45-6789",
            "user_no_firstname",
            "Test@1234",
            "Test@1234"
        );

        String errorText = registrationPage.getFirstNameErrorText();
        Assert.assertEquals(
            errorText,
            "First name is required.",
            "Expected 'First name is required.' but got: '" + errorText + "'"
        );

        logger.info("TC_08 PASSED: First name error displayed correctly.");
    }

    // =========================================================================
    // TC_09 — Password Mismatch — CRITICAL
    // =========================================================================

    /**
     * TC_09: Verifies that mismatched Password and Confirm Password fields
     *        prevent registration and show the appropriate error.
     */
    @Test(
        groups = {"registration"},
        priority = 4,
        description = "TC_09 | TS_02 | [CRITICAL] Verify error when passwords do not match."
    )
    public void testRegistrationPasswordMismatch() {
        registrationPage.fillAndSubmitForm(
            "Auto",
            "Tester",
            "123 QA Street",
            "Test City",
            "NY",
            "10001",
            "5551234567",
            "123-45-6789",
            "user_pw_mismatch",
            "Test123",       // Password
            "Test456"        // Confirm — DIFFERENT from Password
        );

        String errorText = registrationPage.getPasswordMismatchErrorText();
        Assert.assertEquals(
            errorText,
            "Passwords did not match.",
            "Expected 'Passwords did not match.' but got: '" + errorText + "'"
        );

        logger.info("TC_09 PASSED: Password mismatch error displayed correctly.");
    }

    // =========================================================================
    // TC_10 — Existing Username — CRITICAL
    // =========================================================================

    /**
     * TC_10: Verifies that registering with a username that is already taken
     *        is rejected with a descriptive error.
     */
    @Test(
        groups = {"registration"},
        priority = 5,
        description = "TC_10 | TS_02 | [CRITICAL] Verify error when registering with an existing username."
    )
    public void testRegistrationWithExistingUsername() {
        registrationPage.fillAndSubmitForm(
            "Existing",
            "User",
            "123 Main St",
            "Anytown",
            "CA",
            "90001",
            "5559876543",
            "987-65-4321",
            EXISTING_USERNAME,   // Username that already exists in ParaBank
            "Password1",
            "Password1"
        );

        String errorText = registrationPage.getDuplicateUsernameErrorText();
        Assert.assertTrue(
            errorText.toLowerCase().contains("already exists") ||
            errorText.toLowerCase().contains("username"),
            "Expected a 'username already exists' error but got: '" + errorText + "'"
        );

        logger.info("TC_10 PASSED: Duplicate username error displayed correctly.");
    }

    // =========================================================================
    // TC_11 — Whitespace in Last Name — MEDIUM
    // =========================================================================

    /**
     * TC_11: Verifies how the system handles a Last Name consisting only of spaces.
     *        ParaBank may accept this (basic dummy app behavior) — the test
     *        documents actual behavior rather than asserting a strict outcome.
     */
    @Test(
        groups = {"registration"},
        priority = 6,
        description = "TC_11 | TS_02 | [MEDIUM] Document behavior when Last Name contains only whitespace."
    )
    public void testRegistrationWhitespaceLastName() {
        registrationPage.fillAndSubmitForm(
            "Auto",
            "     ",            // Last Name — whitespace only
            "123 QA Street",
            "Test City",
            "NY",
            "10001",
            "5551234567",
            "123-45-6789",
            "user_ws_lastname",
            "Test@1234",
            "Test@1234"
        );

        // Document actual behavior: the system may either reject or accept whitespace
        boolean errorsShown = registrationPage.areValidationErrorsDisplayed();
        if (errorsShown) {
            String errorText = registrationPage.getLastNameErrorText();
            logger.info("TC_11 INFO: System rejected whitespace last name. Error: '{}'", errorText);
            // If the system rejects whitespace, the error should mention last name
            Assert.assertTrue(
                errorText.toLowerCase().contains("last name"),
                "Validation error does not mention 'last name'. Got: '" + errorText + "'"
            );
        } else {
            logger.warn(
                "TC_11 WARNING: System accepted a whitespace-only Last Name. " +
                "This may be a known limitation of the ParaBank demo application."
            );
            // Soft assertion: note but don't fail — the test documents the behavior
        }
    }

    // =========================================================================
    // TC_12 — Missing SSN — HIGH
    // =========================================================================

    /**
     * TC_12: Verifies that omitting the Social Security Number field prevents
     *        registration and shows the required field error.
     */
    @Test(
        groups = {"registration"},
        priority = 7,
        description = "TC_12 | TS_02 | [HIGH] Verify error when SSN is missing."
    )
    public void testRegistrationMissingSSN() {
        registrationPage.fillAndSubmitForm(
            "Auto",
            "Tester",
            "123 QA Street",
            "Test City",
            "NY",
            "10001",
            "5551234567",
            "",             // SSN — intentionally omitted
            "user_no_ssn",
            "Test@1234",
            "Test@1234"
        );

        String errorText = registrationPage.getSsnErrorText();
        Assert.assertEquals(
            errorText,
            "Social Security Number is required.",
            "Expected 'Social Security Number is required.' but got: '" + errorText + "'"
        );

        logger.info("TC_12 PASSED: SSN required error displayed correctly.");
    }
}
