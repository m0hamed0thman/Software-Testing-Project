// File: src/main/java/com/parabank/pages/AccountOverviewPage.java
package com.parabank.pages;

import com.parabank.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object for the ParaBank Accounts Overview Page.
 *
 * Maps to Test Scenario: TS_03 (Account Overview)
 * Covers: TC_13 through TC_18
 */
public class AccountOverviewPage extends BasePage {

    // =========================================================================
    // Locators
    // =========================================================================

    // "Accounts Overview" nav link in the left sidebar
    private static final By ACCOUNTS_OVERVIEW_LINK  = By.linkText("Accounts Overview");

    // The main accounts table
    private static final By ACCOUNTS_TABLE           = By.id("accountTable");

    // All data rows in the account table (excluding header and total rows)
    private static final By ACCOUNT_ROWS             = By.cssSelector("#accountTable tbody tr:not(:last-child)");

    // Column headers within the table
    private static final By TABLE_HEADERS            = By.cssSelector("#accountTable thead th");

    // The "Total" row at the bottom of the table
    private static final By TOTAL_ROW                = By.cssSelector("#accountTable tfoot tr");

    // Total balance value cell in the footer
    private static final By TOTAL_BALANCE_CELL       = By.cssSelector("#accountTable tfoot tr td:nth-child(2)");

    // All individual account balance cells (2nd column in data rows)
    private static final By BALANCE_CELLS            = By.cssSelector("#accountTable tbody tr:not(:last-child) td:nth-child(2)");

    // All account number links (1st column in data rows)
    private static final By ACCOUNT_NUMBER_LINKS     = By.cssSelector("#accountTable tbody tr td:first-child a");

    // Page heading confirming we are on the overview page
    private static final By PAGE_HEADING             = By.cssSelector(".title");

    public AccountOverviewPage(WebDriver driver) {
        super(driver);
    }

    // =========================================================================
    // Navigation
    // =========================================================================

    /**
     * Clicks the "Accounts Overview" link in the navigation panel.
     * Called after a successful login.
     */
    public void navigateToAccountsOverview() {
        click(ACCOUNTS_OVERVIEW_LINK);
        logger.info("Navigated to Accounts Overview page.");
    }

    // =========================================================================
    // Action Methods
    // =========================================================================

    /**
     * Clicks the first account number link in the table.
     * Used in TC_16 to verify navigation to Account Details.
     *
     * @return The account number text (e.g., "12345") for assertion
     */
    public String clickFirstAccountLink() {
        WebElement firstLink = wait.until(
            org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(ACCOUNT_NUMBER_LINKS)
        );
        String accountNumber = firstLink.getText().trim();
        firstLink.click();
        logger.info("Clicked account link: {}", accountNumber);
        return accountNumber;
    }

    // =========================================================================
    // Query / State Methods
    // =========================================================================

    /**
     * Returns the page heading text (TC_13 — confirm we landed on the right page).
     */
    public String getPageHeading() {
        return getText(PAGE_HEADING);
    }

    /**
     * Returns the number of account data rows visible in the table.
     * TC_13: a new user should have exactly 1 account.
     */
    public int getNumberOfAccounts() {
        List<WebElement> rows = driver.findElements(ACCOUNT_ROWS);
        return rows.size();
    }

    /**
     * Checks whether the accounts table is visible on the page (TC_14).
     */
    public boolean isAccountTableDisplayed() {
        return isElementDisplayed(ACCOUNTS_TABLE);
    }

    /**
     * Returns a list of the table column header texts (TC_14).
     * Expected: ["Account", "Balance", "Available Amount"]
     */
    public List<String> getTableHeaderTexts() {
        List<WebElement> headers = driver.findElements(TABLE_HEADERS);
        return headers.stream()
                      .map(h -> h.getText().trim())
                      .filter(t -> !t.isEmpty())
                      .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Parses and sums all individual account balance values from the table body.
     * Used in TC_15 to compute the expected total independently.
     *
     * @return Sum of all account balances as a double
     */
    public double sumOfAllAccountBalances() {
        List<WebElement> cells = driver.findElements(BALANCE_CELLS);
        return cells.stream()
                    .mapToDouble(cell -> parseCurrencyToDouble(cell.getText()))
                    .sum();
    }

    /**
     * Returns the "Total" value displayed in the table footer.
     * Used in TC_15 to compare against the computed sum.
     *
     * @return Total balance as a double
     */
    public double getDisplayedTotalBalance() {
        String totalText = getText(TOTAL_BALANCE_CELL);
        return parseCurrencyToDouble(totalText);
    }

    /**
     * Returns the current URL — used in TC_16 to verify navigation
     * to the Account Activity page after clicking an account link.
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    // =========================================================================
    // Private Helpers
    // =========================================================================

    /**
     * Converts a currency string like "$1,234.56" or "-$50.00" to a double.
     *
     * @param currencyText Raw text from the balance cell
     * @return Parsed numeric value
     */
    private double parseCurrencyToDouble(String currencyText) {
        // Remove currency symbol, commas, and leading/trailing whitespace
        String cleaned = currencyText.replaceAll("[$,\\s]", "").trim();
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            logger.warn("Could not parse currency text '{}' — returning 0.0", currencyText);
            return 0.0;
        }
    }
}
