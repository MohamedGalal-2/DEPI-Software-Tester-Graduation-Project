package tests.ui;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class SearchTest extends BaseTest {
    String url = "https://demo.nopcommerce.com/";

    @Test
    public void verifySearchFunctionality() {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Locate the search bar and input a search term
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));
        searchBox.sendKeys("Laptop");

        // Click the search button
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        // Wait for search results to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-results")));

        // Verify search results are displayed
        WebElement searchResults = driver.findElement(By.cssSelector(".search-results"));
        Assert.assertTrue(searchResults.isDisplayed(), "Search results are not displayed!");

        logger.info("Search functionality verified successfully.");
    }
}