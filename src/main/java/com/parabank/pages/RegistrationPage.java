// File: src/main/java/com/parabank/pages/RegistrationPage.java
package com.parabank.pages;

import com.parabank.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the ParaBank Registration Page.
 *
 * Encapsulates all locators and actions for the user registration form.
 *
 * Maps to Test Scenario: TS_02 (Registration)
 * Covers: TC_06 through TC_12
 */
public class RegistrationPage extends BasePage {

    // =========================================================================
    // Locators
    // =========================================================================

    // Registration form fields
    private static final By FIRST_NAME_INPUT    = By.id("customer.firstName");
    private static final By LAST_NAME_INPUT     = By.id("customer.lastName");
    private static final By ADDRESS_INPUT       = By.id("customer.address.street");
    private static final By CITY_INPUT          = By.id("customer.address.city");
    private static final By STATE_INPUT         = By.id("customer.address.state");
    private static final By ZIP_CODE_INPUT      = By.id("customer.address.zipCode");
    private static final By PHONE_INPUT         = By.id("customer.phoneNumber");
    private static final By SSN_INPUT           = By.id("customer.ssn");
    private static final By USERNAME_INPUT      = By.id("customer.username");
    private static final By PASSWORD_INPUT      = By.id("customer.password");
    private static final By CONFIRM_INPUT       = By.id("repeatedPassword");
    private static final By REGISTER_BUTTON     = By.cssSelector("input[value='Register']");

    // Result messages
    private static final By SUCCESS_MESSAGE     = By.cssSelector(".title");
    private static final By ERROR_USERNAME      = By.id("customer.username.errors");
    private static final By ERROR_PASSWORD      = By.id("repeatedPassword.errors");
    private static final By ERROR_FIRST_NAME    = By.id("customer.firstName.errors");
    private static final By ERROR_LAST_NAME     = By.id("customer.lastName.errors");
    private static final By ERROR_SSN           = By.id("customer.ssn.errors");
    // Generic error container — covers any validation span on the form
    private static final By ANY_ERROR_MESSAGE   = By.cssSelector("span.error");
    // Server-side duplicate username error (returned in a paragraph after submit)
    private static final By DUPLICATE_USER_ERROR = By.cssSelector(".error");

    public RegistrationPage(WebDriver driver) {
        super(driver);
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    /**
     * Navigates directly to the registration page using a relative URL.
     */
    public void navigateTo() {
        // Direct navigation is safer and less prone to regex errors
        driver.get("https://parabank.parasoft.com/parabank/register.htm");
        logger.info("Navigated to registration page.");
    }

    // =========================================================================
    // Action Methods
    // =========================================================================

    public void enterFirstName(String firstName)   { typeText(FIRST_NAME_INPUT, firstName); }
    public void enterLastName(String lastName)     { typeText(LAST_NAME_INPUT, lastName); }
    public void enterAddress(String address)       { typeText(ADDRESS_INPUT, address); }
    public void enterCity(String city)             { typeText(CITY_INPUT, city); }
    public void enterState(String state)           { typeText(STATE_INPUT, state); }
    public void enterZipCode(String zipCode)       { typeText(ZIP_CODE_INPUT, zipCode); }
    public void enterPhone(String phone)           { typeText(PHONE_INPUT, phone); }
    public void enterSsn(String ssn)               { typeText(SSN_INPUT, ssn); }
    public void enterUsername(String username)     { typeText(USERNAME_INPUT, username); }
    public void enterPassword(String password)     { typeText(PASSWORD_INPUT, password); }
    public void enterConfirmPassword(String pass)  { typeText(CONFIRM_INPUT, pass); }

    /**
     * Clicks the Register button to submit the form.
     */
    public void clickRegister() {
        click(REGISTER_BUTTON);
    }

    /**
     * Fills all required registration fields with provided data, then submits.
     * Used for the Happy Path (TC_06) and negative tests needing a baseline.
     *
     * @param firstName   First name
     * @param lastName    Last name
     * @param address     Street address
     * @param city        City
     * @param state       State/province abbreviation
     * @param zipCode     Postal code
     * @param phone       Phone number
     * @param ssn         Social Security Number
     * @param username    Desired account username
     * @param password    Desired password
     * @param confirmPass Confirmation of the password
     */
    public void fillAndSubmitForm(String firstName, String lastName, String address,
                                  String city, String state, String zipCode,
                                  String phone, String ssn, String username,
                                  String password, String confirmPass) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterAddress(address);
        enterCity(city);
        enterState(state);
        enterZipCode(zipCode);
        enterPhone(phone);
        enterSsn(ssn);
        enterUsername(username);
        enterPassword(password);
        enterConfirmPassword(confirmPass);
        clickRegister();
    }

    // =========================================================================
    // Query / State Methods
    // =========================================================================

    /**
     * Returns the success confirmation heading text after a valid registration (TC_06).
     */
    public String getSuccessMessageText() {
        return getText(SUCCESS_MESSAGE);
    }

    /**
     * Returns the error message for the First Name field (TC_08).
     */
    public String getFirstNameErrorText() {
        return getText(ERROR_FIRST_NAME);
    }

    /**
     * Returns the error for the Last Name field (TC_11).
     */
    public String getLastNameErrorText() {
        return getText(ERROR_LAST_NAME);
    }

    /**
     * Returns the error for the SSN field (TC_12).
     */
    public String getSsnErrorText() {
        return getText(ERROR_SSN);
    }

    /**
     * Returns the "Passwords did not match" error for the confirm field (TC_09).
     */
    public String getPasswordMismatchErrorText() {
        return getText(ERROR_PASSWORD);
    }

    /**
     * Returns the duplicate username error message (TC_10).
     * This error appears at the top or in a paragraph after form submission.
     */
    public String getDuplicateUsernameErrorText() {
        return getText(DUPLICATE_USER_ERROR);
    }

    /**
     * Returns true if ANY validation error span is present on the form (TC_07).
     */
    public boolean areValidationErrorsDisplayed() {
        return isElementDisplayed(ANY_ERROR_MESSAGE);
    }
}
