package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class SeleniumHelper {
    private WebDriver driver;
    private WebDriverWait wait;

    public SeleniumHelper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Wait for the page title to contain a specific text
    public void waitForTitleContains(String title, int timeoutInSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        customWait.until(ExpectedConditions.titleContains(title));
    }

    // Get the current page title
    public String getPageTitle() {
        return driver.getTitle();
    }

    // Wait for an element to be visible
    public WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // Wait for an element to be clickable
    public WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    // Click an element
    public void click(By locator) {
        waitForClickable(locator).click();
    }

    // Send keys to an input field
    public void sendKeys(By locator, String text) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    // Select from dropdown by visible text
    public void selectByVisibleText(By locator, String text) {
        Select dropdown = new Select(waitForVisibility(locator));
        dropdown.selectByVisibleText(text);
    }

    // Select from dropdown by index
    public void selectByIndex(By locator, int index) {
        Select dropdown = new Select(waitForVisibility(locator));
        dropdown.selectByIndex(index);
    }

    // Select from dropdown by value
    public void selectByValue(By locator, String value) {
        Select dropdown = new Select(waitForVisibility(locator));
        dropdown.selectByValue(value);
    }

    // Get text from an element
    public String getText(By locator) {
        return waitForVisibility(locator).getText();
    }

    // Execute JavaScript
    public void executeJavaScript(String script, Object... args) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(script, args);
    }

    // Scroll to an element
    public void scrollToElement(By locator) {
        WebElement element = waitForVisibility(locator);
        executeJavaScript("arguments[0].scrollIntoView(true);", element);
    }

    // Get list of elements
    public List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }

    // Check if an element exists
    public boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
