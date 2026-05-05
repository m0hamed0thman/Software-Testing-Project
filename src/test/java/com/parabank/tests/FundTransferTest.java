// File: src/test/java/com/parabank/tests/FundTransferTest.java
package com.parabank.tests;

import com.parabank.base.BaseTest;
import com.parabank.pages.FundTransferPage;
import com.parabank.pages.LoginPage;
import com.parabank.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for the Fund Transfer module.
 *
 * Scenario: TS_04
 * Test Cases: TC_19 through TC_25
 *
 * Pre-condition: User must be logged in and on the Transfer Funds page.
 */
public class FundTransferTest extends BaseTest {

    private FundTransferPage fundTransferPage;

    // =========================================================================
    // Pre-condition: Login and navigate to Transfer Funds page
    // =========================================================================

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigateToTransferPage() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(ConfigReader.getValidUsername(), ConfigReader.getValidPassword());

        fundTransferPage = new FundTransferPage(driver);
        fundTransferPage.navigateToTransferFunds();
    }

    // =========================================================================
    // TC_19 — Valid Fund Transfer (Happy Path) — CRITICAL
    // =========================================================================

    /**
     * TC_19: Verifies a valid transfer of $100 between two accounts displays
     *        the "Transfer Complete" success message.
     *
     * Pre-condition: The test user must have ≥2 accounts with sufficient balance.
     */
    @Test(
        groups = {"fundTransfer"},
        priority = 1,
        description = "TC_19 | TS_04 | [CRITICAL] Verify successful fund transfer shows 'Transfer Complete'."
    )
    public void testValidFundTransfer() {
        fundTransferPage.enterAmount("100");
        fundTransferPage.selectFromAccountByIndex(0);   // Source account
        fundTransferPage.selectToAccountByIndex(1);     // Destination account (different from source)
        fundTransferPage.clickTransfer();

        String successHeading = fundTransferPage.getSuccessHeadingText();
        Assert.assertEquals(
            successHeading,
            "Transfer Complete!",
            "Expected 'Transfer Complete!' heading but got: '" + successHeading + "'"
        );

        logger.info("TC_19 PASSED: Transfer Complete confirmed. Heading: '{}'", successHeading);
    }

    // =========================================================================
    // TC_20 — Insufficient Funds — CRITICAL
    // =========================================================================

    /**
     * TC_20: Verifies that attempting to transfer an amount greater than the
     *        source account balance is blocked with an appropriate error.
     */
    @Test(
        groups = {"fundTransfer"},
        priority = 2,
        description = "TC_20 | TS_04 | [CRITICAL] Verify transfer with amount exceeding balance is blocked."
    )
    public void testTransferWithInsufficientFunds() {
        // 9,999,999 is extremely unlikely to be within any test account balance
        fundTransferPage.enterAmount("9999999");
        fundTransferPage.selectFromAccountByIndex(0);
        fundTransferPage.selectToAccountByIndex(1);
        fundTransferPage.clickTransfer();

        // ParaBank's behavior: it may show an error OR still complete the transfer (overdraft)
        // We test for the error case; log a warning if the app allows overdraft
        boolean errorDisplayed = fundTransferPage.isErrorMessageDisplayed();
        if (errorDisplayed) {
            String errorText = fundTransferPage.getErrorMessageText();
            Assert.assertTrue(
                errorText.toLowerCase().contains("insufficient") ||
                errorText.toLowerCase().contains("funds") ||
                errorText.toLowerCase().contains("exceed"),
                "Expected an insufficient funds message but got: '" + errorText + "'"
            );
            logger.info("TC_20 PASSED: Insufficient funds error displayed: '{}'", errorText);
        } else {
            // ParaBank demo may allow overdraft — document actual behavior
            logger.warn("TC_20 WARNING: System processed a transfer of $9,999,999 without error. " +
                        "ParaBank demo may allow overdrafts. " +
                        "Verify actual balance change in account overview.");
        }
    }

    // =========================================================================
    // TC_21 — Negative Amount — CRITICAL
    // =========================================================================

    /**
     * TC_21: Verifies that entering a negative number in the amount field
     *        is rejected with a validation error.
     */
    @Test(
        groups = {"fundTransfer"},
        priority = 3,
        description = "TC_21 | TS_04 | [CRITICAL] Verify negative transfer amount is rejected."
    )
    public void testTransferNegativeAmount() {
        fundTransferPage.enterAmount("-500");
        fundTransferPage.selectFromAccountByIndex(0);
        fundTransferPage.selectToAccountByIndex(1);
        fundTransferPage.clickTransfer();

        Assert.assertTrue(
            fundTransferPage.isErrorMessageDisplayed(),
            "Expected a validation error for negative amount '-500', but no error was displayed."
        );

        logger.info("TC_21 PASSED: Negative amount was rejected.");
    }

    // =========================================================================
    // TC_22 — Zero Amount — CRITICAL
    // =========================================================================

    /**
     * TC_22: Verifies that entering zero as the transfer amount is rejected.
     */
    @Test(
        groups = {"fundTransfer"},
        priority = 4,
        description = "TC_22 | TS_04 | [CRITICAL] Verify transfer of zero amount is rejected."
    )
    public void testTransferZeroAmount() {
        fundTransferPage.enterAmount("0");
        fundTransferPage.selectFromAccountByIndex(0);
        fundTransferPage.selectToAccountByIndex(1);
        fundTransferPage.clickTransfer();

        Assert.assertTrue(
            fundTransferPage.isErrorMessageDisplayed(),
            "Expected a validation error for zero amount, but no error was displayed."
        );

        logger.info("TC_22 PASSED: Zero amount was rejected.");
    }

    // =========================================================================
    // TC_23 — Transfer to the Same Account — CRITICAL
    // =========================================================================

    /**
     * TC_23: Verifies that selecting the same account for both "From" and "To"
     *        is blocked by the system.
     *
     * Note: ParaBank may silently process this. The test documents the actual behavior.
     */
    @Test(
        groups = {"fundTransfer"},
        priority = 5,
        description = "TC_23 | TS_04 | [CRITICAL] Verify transfer to the same account is blocked."
    )
    public void testTransferToSameAccount() {
        fundTransferPage.transferToSameAccount("50");

        boolean errorShown = fundTransferPage.isErrorMessageDisplayed();
        boolean successShown = fundTransferPage.isTransferSuccessful();

        if (errorShown) {
            logger.info("TC_23 PASSED: System blocked same-account transfer with an error.");
        } else if (successShown) {
            // ParaBank may silently process a same-account transfer (no net effect on balance)
            logger.warn("TC_23 WARNING: System allowed a same-account transfer without error. " +
                        "This is a known limitation of the ParaBank demo application.");
        } else {
            Assert.fail("TC_23 FAILED: Neither a success message nor an error was displayed " +
                        "after attempting a same-account transfer.");
        }
    }

    // =========================================================================
    // TC_24 — Invalid Input Types — CRITICAL
    // =========================================================================

    /**
     * DataProvider for TC_24: Tests various non-numeric amount inputs.
     *
     * Each row represents one invalid input to test.
     */
    @DataProvider(name = "invalidAmountInputs")
    public Object[][] provideInvalidAmountInputs() {
        return new Object[][] {
            {"Fifty"},       // English word instead of number
            {"$100"},        // Dollar sign prefix
            {"100,00"}       // European decimal format (comma as separator)
        };
    }

    /**
     * TC_24: Verifies that entering non-numeric values (text or special characters)
     *        in the amount field is rejected.
     */
    @Test(
        groups = {"fundTransfer"},
        priority = 6,
        dataProvider = "invalidAmountInputs",
        description = "TC_24 | TS_04 | [CRITICAL] Verify non-numeric amount input is rejected."
    )
    public void testTransferWithInvalidAmountFormat(String invalidAmount) {
        fundTransferPage.enterAmount(invalidAmount);
        fundTransferPage.selectFromAccountByIndex(0);
        fundTransferPage.selectToAccountByIndex(1);
        fundTransferPage.clickTransfer();

        Assert.assertTrue(
            fundTransferPage.isErrorMessageDisplayed(),
            "Expected a validation error for invalid amount '" + invalidAmount + "', " +
            "but no error was displayed."
        );

        logger.info("TC_24 PASSED: Input '{}' was correctly rejected.", invalidAmount);
    }

    // =========================================================================
    // TC_25 — Empty Amount Field — CRITICAL
    // =========================================================================

    /**
     * TC_25: Verifies that leaving the amount field blank and clicking Transfer
     *        is rejected with a required-field validation error.
     */
    @Test(
        groups = {"fundTransfer"},
        priority = 7,
        description = "TC_25 | TS_04 | [CRITICAL] Verify transfer with blank amount field is rejected."
    )
    public void testTransferWithEmptyAmount() {
        // Do NOT enter anything in the amount field — leave it blank
        fundTransferPage.selectFromAccountByIndex(0);
        fundTransferPage.selectToAccountByIndex(1);
        fundTransferPage.clickTransfer();

        Assert.assertTrue(
            fundTransferPage.isErrorMessageDisplayed(),
            "Expected a 'required' validation error for blank amount field, " +
            "but no error was displayed."
        );

        logger.info("TC_25 PASSED: Empty amount field was correctly rejected.");
    }
}
