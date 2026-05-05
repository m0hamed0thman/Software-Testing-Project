// File: src/main/java/com/parabank/pages/LoginPage.java
package com.parabank.pages;

import com.parabank.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the ParaBank Login Page.
 *
 * Encapsulates all locators and user interactions specific to the login form.
 * The test class should ONLY call these methods — it must not access By locators directly.
 *
 * Maps to Test Scenario: TS_01 (Login / Logout)
 * Covers: TC_01 through TC_05
 */
public class LoginPage extends BasePage {

    // =========================================================================
    // Locators — private, never exposed to test classes
    // =========================================================================

    // Login form inputs
    private static final By USERNAME_INPUT  = By.name("username");
    private static final By PASSWORD_INPUT  = By.name("password");
    private static final By LOGIN_BUTTON    = By.cssSelector("input[value='Log In']");

    // Error message displayed for invalid credentials
    private static final By ERROR_MESSAGE   = By.cssSelector(".error");

    // Element visible ONLY after successful login (The most reliable check)
    private static final By LOGOUT_LINK = By.linkText("Log Out");

    // Welcome message on the home page after login
    private static final By WELCOME_MESSAGE = By.cssSelector(".title");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // =========================================================================
    // Action Methods
    // =========================================================================

    /**
     * Types a value into the username field.
     *
     * @param username The username string to enter (can be empty string for TC_05)
     */
    public void enterUsername(String username) {
        typeText(USERNAME_INPUT, username);
    }

    /**
     * Types a value into the password field.
     *
     * @param password The password string to enter (can be empty string for TC_05)
     */
    public void enterPassword(String password) {
        typeText(PASSWORD_INPUT, password);
    }

    /**
     * Clicks the Login button to submit the form.
     */
    public void clickLogin() {
        click(LOGIN_BUTTON);
    }

    /**
     * Convenience method: performs the complete login sequence in one call.
     * Used by other test modules that need to log in as a precondition.
     *
     * @param username Valid or invalid username
     * @param password Valid or invalid password
     */
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    // =========================================================================
    // Query / State Methods
    // =========================================================================

    /**
     * Returns the error message text shown after a failed login attempt.
     * Expected by TC_02, TC_03, TC_04, TC_05.
     */
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    /**
     * Checks whether the Log Out link is displayed, confirming a
     * successful login redirect (TC_01).
     */
    public boolean isLoggedInSuccessfully() {
        // تم التعديل هنا لاستخدام عنصر لا يظهر إلا بعد تسجيل الدخول
        return isElementDisplayed(LOGOUT_LINK);
    }

    /**
     * Returns the page title/welcome heading text shown after login.
     */
    public String getWelcomeMessageText() {
        return getText(WELCOME_MESSAGE);
    }
}