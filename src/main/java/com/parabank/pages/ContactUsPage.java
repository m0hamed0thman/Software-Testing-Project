// File: src/main/java/com/parabank/pages/ContactUsPage.java
package com.parabank.pages;

import com.parabank.base.BasePage;
import com.parabank.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the ParaBank Contact Us Page.
 *
 * Maps to Test Scenario: TS_06 (Contact Us)
 * Covers: TC_32 through TC_38
 */
public class ContactUsPage extends BasePage {

    // =========================================================================
    // Locators
    // =========================================================================

    // Navigation link to the Contact page
    private static final By CONTACT_LINK             = By.linkText("contact");

    // Form input fields
    private static final By NAME_INPUT               = By.id("name");
    private static final By EMAIL_INPUT              = By.id("email");
    private static final By PHONE_INPUT              = By.id("phone");
    private static final By MESSAGE_TEXTAREA         = By.id("message");

    // Submit button
    private static final By SEND_BUTTON              = By.cssSelector("input[value='Send to Customer Care']");

    // Success message shown after valid submission
    private static final By SUCCESS_MESSAGE          =  By.cssSelector("#rightPanel > p:nth-of-type(1)");

    // Field-level validation errors
    private static final By ERROR_NAME               = By.id("name.errors");
    private static final By ERROR_EMAIL              = By.id("email.errors");
    private static final By ERROR_PHONE              = By.id("phone.errors");
    private static final By ERROR_MESSAGE_FIELD      = By.id("message.errors");

    // Generic selector — any error element on the page
    private static final By ANY_ERROR                = By.cssSelector("span.error");

    public ContactUsPage(WebDriver driver) {
        super(driver);
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    /**
     * Navigates directly to the Contact Us page via URL.
     * The "contact" link is in the site footer and reliably available.
     */
    public void navigateTo() {
        // Assuming ConfigManager reads from a properties file based on your environment
        String baseUrl = ConfigReader.getBaseUrl();
        String contactUrl = "https://parabank.parasoft.com/parabank/contact.htm";

        driver.get(contactUrl);
        logger.info("Navigated to Contact Us page.");
    }
    // =========================================================================
    // Action Methods
    // =========================================================================

    public void enterName(String name)         { typeText(NAME_INPUT, name); }
    public void enterEmail(String email)       { typeText(EMAIL_INPUT, email); }
    public void enterPhone(String phone)       { typeText(PHONE_INPUT, phone); }
    public void enterMessage(String message)   { typeText(MESSAGE_TEXTAREA, message); }

    /**
     * Clicks the "Send to Customer Care" submit button.
     */
    public void clickSend() {
        click(SEND_BUTTON);
    }

    /**
     * Fills all contact form fields and submits.
     * Used for the Happy Path test (TC_32).
     *
     * @param name    Sender's full name
     * @param email   Sender's email address
     * @param phone   Sender's phone number
     * @param message The message body
     */
    public void fillAndSubmitForm(String name, String email, String phone, String message) {
        enterName(name);
        enterEmail(email);
        enterPhone(phone);
        enterMessage(message);
        clickSend();
    }

    // =========================================================================
    // Query / State Methods
    // =========================================================================

    /**
     * Returns the success confirmation message text (TC_32).
     * Expected: "Thank you {name}" or similar confirmation heading.
     */
    public String getSuccessMessageText() {
        return getText(SUCCESS_MESSAGE);
    }

    /**
     * Returns the "Name is required" error text (TC_34).
     */
    public String getNameErrorText() {
        return getText(ERROR_NAME);
    }

    /**
     * Returns the email validation error text (TC_36).
     */
    public String getEmailErrorText() {
        return getText(ERROR_EMAIL);
    }

    /**
     * Returns the phone validation error text (TC_37).
     */
    public String getPhoneErrorText() {
        return getText(ERROR_PHONE);
    }

    /**
     * Returns the "Message is required" error text (TC_35).
     */
    public String getMessageErrorText() {
        return getText(ERROR_MESSAGE_FIELD);
    }

    /**
     * Returns true if any validation error span is visible.
     * Used in TC_33 (empty form submit) to confirm at least one error shows.
     */
    public boolean areValidationErrorsDisplayed() {
        return isElementDisplayed(ANY_ERROR);
    }
}
