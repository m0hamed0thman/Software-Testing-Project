// File: src/test/java/com/parabank/tests/LoginTest.java
package com.parabank.tests;

import com.parabank.base.BaseTest;
import com.parabank.pages.LoginPage;
import com.parabank.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for the Login / Logout module.
 *
 * Scenario: TS_01
 * Test Cases: TC_01, TC_02, TC_03, TC_04, TC_05
 *
 * ALL assertions live here — never inside LoginPage.
 */
public class LoginTest extends BaseTest {

    // Expected error messages defined as constants for maintainability
    private static final String ERROR_INVALID_CREDENTIALS =
            "The username and password could not be verified.";
    private static final String ERROR_INTERNAL            =
            "An internal error has occurred and has been logged.";
    private static final String ERROR_EMPTY_FIELDS        =
            "Please enter a username and password.";

    // =========================================================================
    // TC_01 — Successful Login (Happy Path) — CRITICAL
    // =========================================================================

    /**
     * TC_01: Verifies that a user with valid credentials is successfully
     *        redirected to the Account Services page.
     */
    @Test(
        groups = {"login"},
        priority = 1,
        description = "TC_01 | TS_01 | [CRITICAL] Verify successful login with valid credentials."
    )
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(
            ConfigReader.getValidUsername(),
            ConfigReader.getValidPassword()
        );

        // Assert that the user is now on the authenticated home page
        Assert.assertTrue(
            loginPage.isLoggedInSuccessfully(),
            "Login failed: Account Services panel was not visible after login with valid credentials."
        );

        logger.info("TC_01 PASSED: Successful login verified.");
    }

    // =========================================================================
    // TC_02, TC_03, TC_04, TC_05 — Negative Login Tests — driven by DataProvider
    // =========================================================================

    /**
     * DataProvider supplies [username, password, expectedErrorMessage] for each
     * negative login scenario, keeping test data cleanly separated from logic.
     *
     * Row 0 → TC_02: Valid username + invalid password
     * Row 1 → TC_03: Invalid username + valid password
     * Row 2 → TC_04: Invalid username + invalid password
     * Row 3 → TC_05: Empty username + empty password
     */
    @DataProvider(name = "invalidLoginData")
    public Object[][] provideInvalidLoginData() {
        return new Object[][] {
            // TC_02: Invalid password
            {
                ConfigReader.getValidUsername(),
                ConfigReader.getInvalidPassword(),
                ERROR_INVALID_CREDENTIALS
            },
            // TC_03: Invalid username
            {
                ConfigReader.getInvalidUsername(),
                ConfigReader.getValidPassword(),
                ERROR_INVALID_CREDENTIALS
            },
            // TC_04: Both invalid — ParaBank returns a different error for this combination
            {
                "suiiiiii",
                "suiiiiiiii",
                ERROR_INTERNAL
            },
            // TC_05: Empty credentials — the form-level validation error
            {
                "",
                "",
                ERROR_EMPTY_FIELDS
            }
        };
    }

    /**
     * TC_02, TC_03, TC_04, TC_05:
     * Verifies that attempting to log in with invalid or empty credentials
     * displays the correct error message and does NOT proceed to the home page.
     *
     * @param username        The username to enter
     * @param password        The password to enter
     * @param expectedError   The exact error message string expected
     */
    @Test(
        groups = {"login"},
        priority = 2,
        dataProvider = "invalidLoginData",
        description = "TC_02/03/04/05 | TS_01 | [HIGH] Verify login fails with invalid/empty credentials."
    )
    public void testLoginWithInvalidCredentials(String username, String password, String expectedError) {
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(username, password);

        // Assert 1: An error message must be visible
        String actualError = loginPage.getErrorMessage();
        Assert.assertFalse(
            actualError.isEmpty(),
            "Expected an error message after failed login, but none was displayed."
        );

        // Assert 2: The error message text must match the expected value exactly
        Assert.assertEquals(
            actualError,
            expectedError,
            String.format(
                "Error message mismatch for credentials [user='%s', pass='%s']. " +
                "Expected: '%s' | Got: '%s'",
                username, password, expectedError, actualError
            )
        );

        // Assert 3: The user must NOT be logged in (no Account Services panel)
        Assert.assertFalse(
            loginPage.isLoggedInSuccessfully(),
            "User appears to be logged in after providing invalid credentials — security breach!"
        );

        logger.info("Negative login test PASSED | Username: '{}' | Error shown: '{}'",
                    username, actualError);
    }
}
