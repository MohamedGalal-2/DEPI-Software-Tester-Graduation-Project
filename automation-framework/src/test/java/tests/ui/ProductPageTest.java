package tests.ui;

import base.BaseTest;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class ProductPageTest extends BaseTest {
    String url = "https://demo.nopcommerce.com/";

    @Test
    public void verifyProductPageLoadsCorrectly() {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));  // Increased timeout to 20 seconds

        // Search for a product
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));
        searchBox.sendKeys("Laptop");

        // Click the search button
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        // Wait for search results to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-results")));

        // Click on the first product link
        WebElement firstProduct = driver.findElement(By.cssSelector(".product-grid .product-item .product-title"));
        firstProduct.click();

        // Wait for the product page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-essential")));

        // Log to check if the page has loaded correctly
        logger.info("Product page loaded successfully.");
    }
}
