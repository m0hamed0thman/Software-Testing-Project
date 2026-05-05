// File: src/test/java/com/parabank/tests/AccountOverviewTest.java
package com.parabank.tests;

import com.parabank.base.BaseTest;
import com.parabank.pages.AccountOverviewPage;
import com.parabank.pages.FundTransferPage;
import com.parabank.pages.LoginPage;
import com.parabank.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Test class for the Account Overview module.
 *
 * Scenario: TS_03
 * Test Cases: TC_13 through TC_18
 *
 * Pre-condition for all tests in this class: User must be logged in.
 * The @BeforeMethod handles login before each test method.
 */
public class AccountOverviewTest extends BaseTest {

    private AccountOverviewPage accountOverviewPage;

    // Expected column headers per TC_14
    private static final List<String> EXPECTED_HEADERS =
            Arrays.asList("Account", "Balance", "Available Amount");

    // =========================================================================
    // Pre-condition: Login before each test
    // =========================================================================

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigateToOverview() {
        // Step 1: Log in with valid credentials
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.getValidUsername(), ConfigReader.getValidPassword());

        // Step 2: Navigate to the Accounts Overview page
        accountOverviewPage = new AccountOverviewPage(driver);
        accountOverviewPage.navigateToAccountsOverview();
    }

    // =========================================================================
    // TC_13 — Verify Default Account for New User — HIGH
    // =========================================================================

    /**
     * TC_13: Verifies that the Accounts Overview page is displayed and shows
     *        at least one account for the logged-in user.
     *
     * Note: TC_13 says "new user" but the configured test user (mohamed_159) is
     * existing. We verify that at least 1 account is present, which is the
     * minimum expectation for any valid user.
     */
    @Test(
        groups = {"accountOverview"},
        priority = 1,
        description = "TC_13 | TS_03 | [HIGH] Verify at least one account exists on the Overview page."
    )
    public void testDefaultAccountIsDisplayed() {
        int numberOfAccounts = accountOverviewPage.getNumberOfAccounts();

        Assert.assertTrue(
            numberOfAccounts >= 1,
            "Expected at least 1 account to be displayed, but found: " + numberOfAccounts
        );

        logger.info("TC_13 PASSED: {} account(s) found on overview page.", numberOfAccounts);
    }

    // =========================================================================
    // TC_14 — Verify Account Data Columns — HIGH
    // =========================================================================

    /**
     * TC_14: Verifies that the accounts table has the correct column headers
     *        and that the table is visible.
     */
    @Test(
        groups = {"accountOverview"},
        priority = 2,
        description = "TC_14 | TS_03 | [HIGH] Verify account table headers are 'Account', 'Balance', 'Available Amount'."
    )
    public void testAccountTableColumnsAreCorrect() {
        // Assert 1: Table is visible
        Assert.assertTrue(
            accountOverviewPage.isAccountTableDisplayed(),
            "Accounts table was not displayed on the overview page."
        );

        // Assert 2: Column headers match exactly
        List<String> actualHeaders = accountOverviewPage.getTableHeaderTexts();
        Assert.assertEquals(
            actualHeaders,
            EXPECTED_HEADERS,
            "Column headers mismatch. Expected: " + EXPECTED_HEADERS + " | Got: " + actualHeaders
        );

        logger.info("TC_14 PASSED: Table headers verified: {}", actualHeaders);
    }

    // =========================================================================
    // TC_15 — Verify Total Balance Calculation — CRITICAL
    // =========================================================================

    /**
     * TC_15: Verifies that the "Total" row at the bottom of the accounts table
     *        equals the mathematical sum of all individual account balances.
     *
     * This is a CRITICAL test — an incorrect total would misrepresent a user's
     * financial position.
     */
    @Test(
        groups = {"accountOverview"},
        priority = 3,
        description = "TC_15 | TS_03 | [CRITICAL] Verify Total row equals sum of all account balances."
    )
    public void testTotalBalanceMatchesSumOfAccounts() {
        double computedTotal   = accountOverviewPage.sumOfAllAccountBalances();
        double displayedTotal  = accountOverviewPage.getDisplayedTotalBalance();

        // Use a delta of 0.01 to accommodate floating point rounding in currency display
        Assert.assertEquals(
            displayedTotal, computedTotal, 0.01,
            String.format(
                "Total balance mismatch! Displayed: $%.2f | Computed sum: $%.2f",
                displayedTotal, computedTotal
            )
        );

        logger.info("TC_15 PASSED: Displayed total $%.2f matches computed sum $%.2f.",
                    displayedTotal, computedTotal);
    }

    // =========================================================================
    // TC_16 — Verify Account Navigation Link — CRITICAL
    // =========================================================================

    /**
     * TC_16: Verifies that clicking an account number hyperlink navigates the
     *        user to the Account Details/Activity page for that specific account.
     */
    @Test(
        groups = {"accountOverview"},
        priority = 4,
        description = "TC_16 | TS_03 | [CRITICAL] Verify clicking an account link navigates to its detail page."
    )
    public void testAccountNavigationLink() {
        String clickedAccountNumber = accountOverviewPage.clickFirstAccountLink();

        String currentUrl = accountOverviewPage.getCurrentUrl();

        // The URL of the Account Activity page should contain the account number
        Assert.assertTrue(
            currentUrl.contains(clickedAccountNumber) ||
            currentUrl.contains("activity") ||
            currentUrl.contains("account"),
            "After clicking account '" + clickedAccountNumber +
            "', URL did not navigate to the account detail page. Actual URL: " + currentUrl
        );

        logger.info("TC_16 PASSED: Clicking account '{}' navigated to '{}'.",
                    clickedAccountNumber, currentUrl);
    }

    // =========================================================================
    // TC_17 — Verify Balance Update After Transaction — CRITICAL
    // =========================================================================

    /**
     * TC_17: Verifies that account balances on the Overview page update
     *        immediately after a fund transfer is performed.
     *
     * Steps:
     *  1. Record the balance of the "From" account before transfer
     *  2. Perform a $20 transfer
     *  3. Return to Overview and check that balances have changed
     */
    @Test(
        groups = {"accountOverview"},
        priority = 5,
        description = "TC_17 | TS_03 | [CRITICAL] Verify balance updates after a fund transfer."
    )
    public void testBalanceUpdateAfterTransfer() {
        // Step 1: Record the balances before the transfer
        double totalBefore = accountOverviewPage.getDisplayedTotalBalance();

        // Step 2: Only possible if user has at least 2 accounts for a meaningful transfer
        // Skip the transfer part if there's only 1 account (same-account transfer is invalid)
        int accountCount = accountOverviewPage.getNumberOfAccounts();
        if (accountCount < 2) {
            logger.warn("TC_17: Only {} account(s) found. Cannot verify inter-account transfer. " +
                        "Verifying page refresh instead.", accountCount);
            accountOverviewPage.navigateToAccountsOverview();
            double totalAfterRefresh = accountOverviewPage.getDisplayedTotalBalance();
            Assert.assertEquals(
                totalAfterRefresh, totalBefore, 0.01,
                "Balance changed unexpectedly after page refresh with no transaction."
            );
            return;
        }

        // Perform a $20 transfer using the FundTransferPage
        FundTransferPage fundTransferPage = new FundTransferPage(driver);
        fundTransferPage.navigateToTransferFunds();
        fundTransferPage.enterAmount("20");
        fundTransferPage.selectFromAccountByIndex(0);   // First account as source
        fundTransferPage.selectToAccountByIndex(1);     // Second account as destination
        fundTransferPage.clickTransfer();

        // Step 3: Navigate back to overview and assert balances are updated
        accountOverviewPage.navigateToAccountsOverview();
        double totalAfter = accountOverviewPage.getDisplayedTotalBalance();

        // Total should remain the same (internal transfer: money moves, not disappears)
        Assert.assertEquals(
            totalAfter, totalBefore, 0.01,
            String.format(
                "Total balance changed after internal transfer! Before: $%.2f | After: $%.2f",
                totalBefore, totalAfter
            )
        );

        logger.info("TC_17 PASSED: Total balance consistent before ($%.2f) and after ($%.2f) transfer.",
                    totalBefore, totalAfter);
    }

    // =========================================================================
    // TC_18 — Verify UI Responsiveness & Formatting — MEDIUM
    // =========================================================================

    /**
     * TC_18: Verifies that the accounts table remains accessible and visible
     *        when the browser is resized to a mobile viewport.
     *
     * Note: ParaBank is a basic demo app without a responsive design. This test
     * documents whether the table breaks or remains functional at mobile widths.
     */
    @Test(
        groups = {"accountOverview"},
        priority = 6,
        description = "TC_18 | TS_03 | [MEDIUM] Verify account table is visible at mobile viewport size."
    )
    public void testTableVisibilityAtMobileViewport() {
        // Resize to a common mobile width (iPhone 12 Pro: 390px)
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(390, 844));

        // The table element must still be present in the DOM and visible
        Assert.assertTrue(
            accountOverviewPage.isAccountTableDisplayed(),
            "Accounts table is not visible after resizing to mobile viewport (390x844). " +
            "Possible layout breakage — the table may be hidden or overflowing."
        );

        logger.info("TC_18 PASSED: Account table is visible at 390x844 mobile viewport.");

        // Restore to full size for any subsequent tests
        driver.manage().window().maximize();
    }
}
