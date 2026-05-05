
package com.parabank.base;

import com.parabank.utils.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected static final Logger logger = LogManager.getLogger(BasePage.class);

    /**
     * @param driver The WebDriver instance passed down from the test class
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        // All explicit waits use the timeout from config.properties
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getExplicitWait()));
        // Initialize @FindBy annotated fields in subclasses
        PageFactory.initElements(driver, this);
    }

    // -------------------------------------------------------------------------
    // Reusable helper methods
    // -------------------------------------------------------------------------

    /**
     * Waits for an element to be visible, clears it, then types text into it.
     * Used for all input fields to ensure the element is ready before interaction.
     */
    protected void typeText(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
        logger.debug("Typed '{}' into element: {}", text, locator);
    }

    /**
     * Waits for an element to be clickable, then clicks it.
     * "Clickable" means visible AND enabled — stronger than just visible.
     */
    protected void click(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
        logger.debug("Clicked element: {}", locator);
    }

    /**
     * Waits for an element's text to be non-empty, then returns it.
     * Useful for reading confirmation messages and error labels.
     */
    protected String getText(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return element.getText().trim();
    }

    /**
     * Waits for an element and returns whether it is displayed.
     * Does NOT throw if element is absent — returns false instead.
     */
    protected boolean isElementDisplayed(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Selects a dropdown option by visible text.
     *
     * @param locator   Locator for the <select> element
     * @param visibleText The text of the option to select
     */
    protected void selectByVisibleText(By locator, String visibleText) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        new Select(element).selectByVisibleText(visibleText);
        logger.debug("Selected '{}' in dropdown: {}", visibleText, locator);
    }

    /**
     * Selects a dropdown option by its index (0-based).
     */
    protected void selectByIndex(By locator, int index) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        new Select(element).selectByIndex(index);
        logger.debug("Selected index {} in dropdown: {}", index, locator);
    }

    /**
     * Waits until the page title contains the expected substring.
     * Useful for verifying navigation after a form submission.
     */
    protected boolean waitForTitleToContain(String titleSubstring) {
        return wait.until(ExpectedConditions.titleContains(titleSubstring));
    }

    /**
     * Waits for a URL fragment to appear in the current URL.
     * Used to confirm redirects (e.g., to the overview page after login).
     */
    protected boolean waitForUrlToContain(String urlFragment) {
        return wait.until(ExpectedConditions.urlContains(urlFragment));
    }

    /**
     * Returns the text value of the first selected option in a dropdown.
     */
    protected String getSelectedDropdownText(By locator) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        return new Select(element).getFirstSelectedOption().getText();
    }
}
