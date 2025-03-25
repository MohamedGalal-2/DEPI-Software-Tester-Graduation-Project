package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SeleniumHelper {
    private WebDriver driver;
    private WebDriverWait wait;

    public SeleniumHelper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    /**
     * Converts a string locator from config.properties to a Selenium By object.
     *
     * @param key The property key from config.properties
     * @return By object corresponding to the locator
     */
    public static By getByLocator(String key) {
        String locator = ConfigReader.getProperty(key);

        if (locator == null) {
            throw new RuntimeException("Locator for key '" + key + "' not found in config.properties.");
        }

        if (locator.startsWith("id=")) {
            return By.id(locator.substring(3));
        } else if (locator.startsWith("css=")) {
            return By.cssSelector(locator.substring(4));
        } else if (locator.startsWith("xpath=")) {
            return By.xpath(locator.substring(6));
        } else if (locator.startsWith("linkText=")) {
            return By.linkText(locator.substring(9));
        } else if (locator.startsWith("name=")) {
            return By.name(locator.substring(5));
        } else if (locator.startsWith("className=")) {
            return By.className(locator.substring(10));
        } else {
            throw new RuntimeException("Invalid locator type: " + locator);
        }
    }

    public static String getProductName(String key) {
        String value = ConfigReader.getProperty(key); // Read the property value
        if (value != null && value.startsWith("productName=")) {
            return value.substring(12); // Remove "productName=" and return the rest
        }
        return value; // Return as is if no prefix exists
    }

    public static String getProductID(String key) {
        String value = ConfigReader.getProperty(key);
        if (value != null && value.startsWith("productId=")) {
            return value.substring(10); // Remove "productId=" and return the rest
        }
        return value; // Return as is if no prefix exists
    }

    // Wait for the page title to contain a specific text
    public void waitForTitleContains(String title, int timeoutInSeconds) {
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        customWait.until(ExpectedConditions.titleContains(title));
    }

    public void waitForAttributeToLoad(WebElement element, String attribute, String expectedValue) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.attributeContains(element, attribute, expectedValue));
    }


    // Get the current page title
    public String getPageTitle() {
        return driver.getTitle();
    }

    // Wait for an element to be visible
    public WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    // Wait for an element to be present in the DOM
    public WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    // Wait for all elements to be present
    public List<WebElement> waitForAllElementsPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
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

    // Random delay function
    public static void realisticDelay() throws InterruptedException {
        int delay = ThreadLocalRandom.current().nextInt(1000, 3000); // 1s - 3s
        Thread.sleep(delay);
    }

    public void scrollToElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    // Function to generate a random delay between 7 to 12 seconds
    public static void randomDelay() throws InterruptedException {
        int randomTime = ThreadLocalRandom.current().nextInt(7000, 12000); // 7s - 12s
        Thread.sleep(randomTime);
    }

    // Function to simulate human-like scrolling
    public void randomScroll() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        int scrollAmount = ThreadLocalRandom.current().nextInt(300, 700); // Random scroll distance
        js.executeScript("window.scrollBy(0," + scrollAmount + ");");
    }

    // Helper method to check if an alert is present
    public boolean isAlertPresent() {
        try {
            Alert alert = driver.switchTo().alert();
            alert.dismiss(); // Close the alert if present
            return true;
        } catch (Exception e) {
            return false; // No alert present
        }
    }
}
