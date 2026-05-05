// File: src/main/java/com/parabank/pages/BillPaymentPage.java
package com.parabank.pages;

import com.parabank.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the ParaBank Bill Payment Page.
 *
 * Maps to Test Scenario: TS_05 (Bill Payment)
 * Covers: TC_26 through TC_31
 */
public class BillPaymentPage extends BasePage {

    // =========================================================================
    // Locators
    // =========================================================================

    // Navigation link
    private static final By BILL_PAY_LINK          = By.linkText("Bill Pay");

    // Payee information fields
    private static final By PAYEE_NAME_INPUT        = By.name("payee.name");
    private static final By ADDRESS_INPUT           = By.name("payee.address.street");
    private static final By CITY_INPUT              = By.name("payee.address.city");
    private static final By STATE_INPUT             = By.name("payee.address.state");
    private static final By ZIP_CODE_INPUT          = By.name("payee.address.zipCode");
    private static final By PHONE_INPUT             = By.name("payee.phoneNumber");

    // Account fields
    private static final By ACCOUNT_INPUT           = By.name("payee.accountNumber");
    private static final By VERIFY_ACCOUNT_INPUT    = By.name("verifyAccount");

    // Amount and source
    private static final By AMOUNT_INPUT            = By.name("amount");
    private static final By FROM_ACCOUNT_DROPDOWN   = By.name("fromAccountId");

    // Submit button
    private static final By SEND_PAYMENT_BUTTON     = By.cssSelector("input[value='Send Payment']");

    // Result messages
    private static final By SUCCESS_HEADING         = By.cssSelector("#billpayResult h1");
    private static final By SUCCESS_RESULT_PANEL    = By.id("billpayResult");

    // Validation error messages (field-level)
    private static final By ERROR_PAYEE_NAME        = By.id("validationModel-name");
    private static final By ERROR_ACCOUNT_MISMATCH  = By.id("validationModel-verifyAccount");
    private static final By ERROR_AMOUNT            = By.id("validationModel-amount");
    private static final By ANY_ERROR               = By.cssSelector(".error");

    public BillPaymentPage(WebDriver driver) {
        super(driver);
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    /**
     * Navigates to the Bill Pay page by clicking its sidebar link.
     */
    public void navigateToBillPay() {
        click(BILL_PAY_LINK);
        logger.info("Navigated to Bill Pay page.");
    }

    // =========================================================================
    // Action Methods
    // =========================================================================

    public void enterPayeeName(String name)       { typeText(PAYEE_NAME_INPUT, name); }
    public void enterAddress(String address)      { typeText(ADDRESS_INPUT, address); }
    public void enterCity(String city)            { typeText(CITY_INPUT, city); }
    public void enterState(String state)          { typeText(STATE_INPUT, state); }
    public void enterZipCode(String zip)          { typeText(ZIP_CODE_INPUT, zip); }
    public void enterPhone(String phone)          { typeText(PHONE_INPUT, phone); }
    public void enterAccount(String account)      { typeText(ACCOUNT_INPUT, account); }
    public void enterVerifyAccount(String acct)   { typeText(VERIFY_ACCOUNT_INPUT, acct); }
    public void enterAmount(String amount)        { typeText(AMOUNT_INPUT, amount); }

    /**
     * Selects the "From" account by its visible text.
     */
    public void selectFromAccount(String accountText) {
        selectByVisibleText(FROM_ACCOUNT_DROPDOWN, accountText);
    }

    /**
     * Selects the "From" account by index (0-based). Used when no specific
     * account text is known but any valid account will suffice.
     */
    public void selectFromAccountByIndex(int index) {
        selectByIndex(FROM_ACCOUNT_DROPDOWN, index);
    }

    /**
     * Clicks "Send Payment" to submit the bill payment form.
     */
    public void clickSendPayment() {
        click(SEND_PAYMENT_BUTTON);
    }

    /**
     * Fills in all required fields for a valid bill payment and submits.
     * Used as the Happy Path (TC_26) helper.
     *
     * @param payeeName   Name of the payee
     * @param address     Street address of the payee
     * @param city        City
     * @param state       State abbreviation
     * @param zip         Zip code
     * @param phone       Phone number
     * @param account     Account number
     * @param verifyAcct  Confirm account number (should match account)
     * @param amount      Payment amount as string
     */
    public void fillAndSubmitValidPayment(String payeeName, String address, String city,
                                          String state, String zip, String phone,
                                          String account, String verifyAcct, String amount) {
        enterPayeeName(payeeName);
        enterAddress(address);
        enterCity(city);
        enterState(state);
        enterZipCode(zip);
        enterPhone(phone);
        enterAccount(account);
        enterVerifyAccount(verifyAcct);
        enterAmount(amount);
        selectFromAccountByIndex(0);
        clickSendPayment();
    }

    // =========================================================================
    // Query / State Methods
    // =========================================================================

    /**
     * Returns the success heading text after a valid bill payment (TC_26).
     * Expected: "Bill Payment Complete"
     */
    public String getSuccessHeadingText() {
        return getText(SUCCESS_HEADING);
    }

    /**
     * Returns true if the payment success result panel is displayed.
     */
    public boolean isPaymentSuccessful() {
        return isElementDisplayed(SUCCESS_RESULT_PANEL);
    }

    /**
     * Returns the payee name validation error text (TC_27).
     */
    public String getPayeeNameErrorText() {
        return getText(ERROR_PAYEE_NAME);
    }

    /**
     * Returns the account mismatch error text (TC_28).
     */
    public String getAccountMismatchErrorText() {
        return getText(ERROR_ACCOUNT_MISMATCH);
    }

    /**
     * Returns the amount field validation error text (TC_30).
     */
    public String getAmountErrorText() {
        return getText(ERROR_AMOUNT);
    }

    /**
     * Returns true if any error span is visible on the page.
     * Covers TC_27 and other negative cases.
     */
    public boolean isAnyErrorDisplayed() {
        return isElementDisplayed(ANY_ERROR);
    }
}
