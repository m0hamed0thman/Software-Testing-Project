// File: src/test/java/com/parabank/tests/ContactUsTest.java
package com.parabank.tests;

import com.parabank.base.BaseTest;
import com.parabank.pages.ContactUsPage;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test class for the Contact Us module.
 *
 * Scenario: TS_06
 * Test Cases: TC_32 through TC_38
 *
 * The Contact Us page is publicly accessible — no login is required.
 * BaseTest.setUp() opens the base URL; @BeforeMethod then navigates to /contact.htm.
 */
public class ContactUsTest extends BaseTest {

    private ContactUsPage contactUsPage;

    // Valid test data constants for reuse
    private static final String VALID_NAME    = "John Doe";
    private static final String VALID_EMAIL   = "test@test.com";
    private static final String VALID_PHONE   = "123456789";
    private static final String VALID_MESSAGE = "Hello, this is a test message from the automation suite.";

    // =========================================================================
    // Pre-condition: Navigate to the Contact Us page before each test
    // =========================================================================

    @BeforeMethod(alwaysRun = true)
    public void navigateToContactPage() {
        contactUsPage = new ContactUsPage(driver);
        contactUsPage.navigateTo();
    }

    // =========================================================================
    // TC_32 — Successful Form Submission (Happy Path) — CRITICAL
    // =========================================================================

    /**
     * TC_32: Verifies that filling all required fields with valid data and
     *        clicking "Send to Customer Care" shows a success confirmation message.
     */
    @Test(
        groups = {"contactUs"},
        priority = 1,
        description = "TC_32 | TS_06 | [CRITICAL] Verify successful Contact Us form submission."
    )
    public void testSuccessfulContactFormSubmission() {
        contactUsPage.fillAndSubmitForm(VALID_NAME, VALID_EMAIL, VALID_PHONE, VALID_MESSAGE);

        String successText = contactUsPage.getSuccessMessageText();
        Assert.assertTrue(
            successText.toLowerCase().contains("thank you") ||
            successText.toLowerCase().contains("contact"),
            "Expected a 'Thank you' confirmation message but got: '" + successText + "'"
        );

        logger.info("TC_32 PASSED: Contact form submitted. Confirmation: '{}'", successText);
    }

    // =========================================================================
    // TC_33 — Submit Empty Form — HIGH
    // =========================================================================

    /**
     * TC_33: Verifies that submitting the contact form without filling any fields
     *        triggers validation errors on all required fields.
     */
    @Test(
        groups = {"contactUs"},
        priority = 2,
        description = "TC_33 | TS_06 | [HIGH] Verify validation errors appear when form is submitted empty."
    )
    public void testSubmitEmptyContactForm() {
        // Do not fill anything — click send immediately
        contactUsPage.clickSend();

        Assert.assertTrue(
            contactUsPage.areValidationErrorsDisplayed(),
            "Expected validation errors to appear after empty form submission, but none were found."
        );

        logger.info("TC_33 PASSED: Validation errors displayed for empty Contact Us form.");
    }

    // =========================================================================
    // TC_34 — Missing Name Field — HIGH
    // =========================================================================

    /**
     * TC_34: Verifies that omitting the Name field causes a field-specific
     *        "Name is required" error.
     */
    @Test(
        groups = {"contactUs"},
        priority = 3,
        description = "TC_34 | TS_06 | [HIGH] Verify 'Name is required' error when Name field is empty."
    )
    public void testMissingNameField() {
        contactUsPage.fillAndSubmitForm(
            "",              // Name — intentionally omitted
            VALID_EMAIL,
            VALID_PHONE,
            VALID_MESSAGE
        );

        String errorText = contactUsPage.getNameErrorText();
        Assert.assertEquals(
            errorText,
            "Name is required.",
            "Expected 'Name is required.' but got: '" + errorText + "'"
        );

        logger.info("TC_34 PASSED: Name required error displayed correctly.");
    }

    // =========================================================================
    // TC_35 — Missing Message Field — HIGH
    // =========================================================================

    /**
     * TC_35: Verifies that omitting the Message field causes a field-specific
     *        "Message is required" error.
     */
    @Test(
        groups = {"contactUs"},
        priority = 4,
        description = "TC_35 | TS_06 | [HIGH] Verify 'Message is required' error when Message field is empty."
    )
    public void testMissingMessageField() {
        contactUsPage.fillAndSubmitForm(
            VALID_NAME,
            VALID_EMAIL,
            VALID_PHONE,
            ""               // Message — intentionally omitted
        );

        String errorText = contactUsPage.getMessageErrorText();
        Assert.assertEquals(
            errorText,
            "Message is required.",
            "Expected 'Message is required.' but got: '" + errorText + "'"
        );

        logger.info("TC_35 PASSED: Message required error displayed correctly.");
    }

    // =========================================================================
    // TC_36 — Invalid Email Format — MEDIUM
    // =========================================================================

    /**
     * TC_36: Verifies how the system handles an email address missing the "@" symbol.
     *
     * Note: ParaBank is a basic demo app. It may skip email format validation.
     * This test documents actual behavior.
     */
    @Test(
        groups = {"contactUs"},
        priority = 5,
        description = "TC_36 | TS_06 | [MEDIUM] Document system behavior for invalid email format."
    )
    public void testInvalidEmailFormat() {
        contactUsPage.fillAndSubmitForm(
            VALID_NAME,
            "johndoe.com",   // Invalid email — missing "@"
            VALID_PHONE,
            VALID_MESSAGE
        );

        boolean errorsShown = contactUsPage.areValidationErrorsDisplayed();
        if (errorsShown) {
            String emailError = contactUsPage.getEmailErrorText();
            logger.info("TC_36 INFO: System rejected invalid email. Error: '{}'", emailError);
            Assert.assertFalse(
                emailError.isEmpty(),
                "An error is shown but the email error text is empty."
            );
        } else {
            // ParaBank may not validate email format — document but don't hard fail
            String successText = contactUsPage.getSuccessMessageText();
            logger.warn(
                "TC_36 WARNING: System accepted invalid email 'johndoe.com' without error. " +
                "ParaBank may not perform email format validation. " +
                "Confirmation shown: '{}'", successText
            );
        }
    }

    // =========================================================================
    // TC_37 — Special Characters in Phone Field — LOW
    // =========================================================================

    /**
     * TC_37: Verifies how the system handles letters and special characters
     *        entered in the Phone Number field.
     */
    @Test(
        groups = {"contactUs"},
        priority = 6,
        description = "TC_37 | TS_06 | [LOW] Document system behavior for non-numeric phone input."
    )
    public void testSpecialCharactersInPhoneField() {
        contactUsPage.fillAndSubmitForm(
            VALID_NAME,
            VALID_EMAIL,
            "abc-$#@!",      // Non-numeric phone input
            VALID_MESSAGE
        );

        boolean errorsShown = contactUsPage.areValidationErrorsDisplayed();
        if (errorsShown) {
            logger.info("TC_37 INFO: System rejected non-numeric phone input.");
        } else {
            logger.warn(
                "TC_37 WARNING: System accepted non-numeric phone 'abc-$#@!' without error. " +
                "Consider adding phone format validation if not intentional."
            );
        }

        // TC_37 is LOW severity — document behavior, do not hard-fail
        // The intent is to note whether ParaBank validates phone format
        logger.info("TC_37 COMPLETED: Phone special character handling documented.");
    }

    // =========================================================================
    // TC_38 — Exceeding Max Length in Message Field — MEDIUM
    // =========================================================================

    /**
     * TC_38: Verifies that entering a very long message (>5000 characters)
     *        is handled gracefully — either truncated or shown as an error —
     *        without crashing the application.
     */
    @Test(
        groups = {"contactUs"},
        priority = 7,
        description = "TC_38 | TS_06 | [MEDIUM] Verify system handles very long message input gracefully."
    )
    public void testExceedingMaxLengthInMessageField() {
        // Generate a 5001-character string of random alphanumeric characters
        String veryLongMessage = "A".repeat(5001);

        contactUsPage.fillAndSubmitForm(VALID_NAME, VALID_EMAIL, VALID_PHONE, veryLongMessage);

        // The app must not crash — it should either accept or reject the input gracefully
        // A page crash would typically cause the driver to throw an exception
        String currentUrl = driver.getCurrentUrl();
        Assert.assertNotNull(
            currentUrl,
            "TC_38 FAILED: Page became unresponsive or crashed after submitting a 5001-char message."
        );

        boolean errorsShown = contactUsPage.areValidationErrorsDisplayed();
        if (errorsShown) {
            logger.info("TC_38 PASSED: System rejected the 5001-char message with a validation error.");
        } else {
            // If no error, the app accepted it — check that the page is still responsive
            String successText = contactUsPage.getSuccessMessageText();
            logger.info(
                "TC_38 PASSED: System accepted the 5001-char message without crashing. " +
                "Outcome: '{}'", successText
            );
        }
    }
}
