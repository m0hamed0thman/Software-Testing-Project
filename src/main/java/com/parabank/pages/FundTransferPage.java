// File: src/main/java/com/parabank/pages/FundTransferPage.java
package com.parabank.pages;

import com.parabank.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for the ParaBank Transfer Funds Page.
 *
 * Maps to Test Scenario: TS_04 (Fund Transfer)
 * Covers: TC_19 through TC_25
 */
public class FundTransferPage extends BasePage {

    // =========================================================================
    // Locators
    // =========================================================================

    // Navigation link in the sidebar
    private static final By TRANSFER_FUNDS_LINK    = By.linkText("Transfer Funds");

    // Amount input field
    private static final By AMOUNT_INPUT           = By.id("amount");

    // "From" account dropdown
    private static final By FROM_ACCOUNT_DROPDOWN  = By.id("fromAccountId");

    // "To" account dropdown
    private static final By TO_ACCOUNT_DROPDOWN    = By.id("toAccountId");

    // Submit button
    private static final By TRANSFER_BUTTON        = By.cssSelector("input[value='Transfer']");

    // Success confirmation heading shown after a valid transfer
    private static final By SUCCESS_HEADING        = By.cssSelector("#showResult h1");

    // Error or validation message container
    private static final By ERROR_MESSAGE          = By.cssSelector(".error");

    // Full result/status panel (used to confirm any outcome)
    private static final By RESULT_PANEL           = By.id("showResult");

    public FundTransferPage(WebDriver driver) {
        super(driver);
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    /**
     * Clicks the "Transfer Funds" link in the left navigation panel.
     */
    public void navigateToTransferFunds() {
        click(TRANSFER_FUNDS_LINK);
        logger.info("Navigated to Transfer Funds page.");
    }

    // =========================================================================
    // Action Methods
    // =========================================================================

    /**
     * Enters a value in the amount field.
     *
     * @param amount Amount as a string (can be numeric, negative, zero, or text)
     */
    public void enterAmount(String amount) {
        typeText(AMOUNT_INPUT, amount);
    }

    /**
     * Selects the "From" account by its index in the dropdown.
     * Index 0 is the first account (the source account).
     *
     * @param index Zero-based dropdown index
     */
    public void selectFromAccountByIndex(int index) {
        selectByIndex(FROM_ACCOUNT_DROPDOWN, index);
    }

    /**
     * Selects the "To" account by its index in the dropdown.
     *
     * @param index Zero-based dropdown index
     */
    public void selectToAccountByIndex(int index) {
        selectByIndex(TO_ACCOUNT_DROPDOWN, index);
    }

    /**
     * Selects the "From" account by its visible text (account number).
     */
    public void selectFromAccountByText(String accountText) {
        selectByVisibleText(FROM_ACCOUNT_DROPDOWN, accountText);
    }

    /**
     * Selects the "To" account by its visible text (account number).
     */
    public void selectToAccountByText(String accountText) {
        selectByVisibleText(TO_ACCOUNT_DROPDOWN, accountText);
    }

    /**
     * Clicks the Transfer button to submit the form.
     */
    public void clickTransfer() {
        click(TRANSFER_BUTTON);
    }

    /**
     * Convenience method: selects same account for both From and To, then transfers.
     * Used in TC_23 (same account transfer).
     *
     * @param amount Amount to attempt to transfer
     */
    public void transferToSameAccount(String amount) {
        enterAmount(amount);
        // Select index 0 for both — guaranteed to be the same account
        selectFromAccountByIndex(0);
        selectToAccountByIndex(0);
        clickTransfer();
    }

    // =========================================================================
    // Query / State Methods
    // =========================================================================

    /**
     * Returns the success heading text (TC_19 — "Transfer Complete").
     */
    public String getSuccessHeadingText() {
        return getText(SUCCESS_HEADING);
    }

    /**
     * Returns the error message text shown after a failed transfer.
     * Covers TC_20, TC_21, TC_22, TC_24, TC_25.
     */
    public String getErrorMessageText() {
        return getText(ERROR_MESSAGE);
    }

    /**
     * Returns true if the success result panel is visible.
     */
    public boolean isTransferSuccessful() {
        return isElementDisplayed(RESULT_PANEL);
    }

    /**
     * Returns true if an error message is displayed.
     */
    public boolean isErrorMessageDisplayed() {
        return isElementDisplayed(ERROR_MESSAGE);
    }

    /**
     * Returns the currently selected "From" account text.
     * Used in TC_23 to confirm both dropdowns show the same account.
     */
    public String getSelectedFromAccount() {
        return getSelectedDropdownText(FROM_ACCOUNT_DROPDOWN);
    }

    /**
     * Returns the currently selected "To" account text.
     */
    public String getSelectedToAccount() {
        return getSelectedDropdownText(TO_ACCOUNT_DROPDOWN);
    }
}
