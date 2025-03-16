package tests.ui;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.SeleniumHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class HomePageTest extends BaseTest {
    private final String url = "https://demo.nopcommerce.com/";

    @Test
    public void verifyHomePageLoads() {
        logger.info("Navigating to: " + url);
        driver.get(url);

        // Create an instance of SeleniumHelper
        SeleniumHelper seleniumHelper = new SeleniumHelper(driver);

        // Use SeleniumHelper to wait for title and get the actual title
        seleniumHelper.waitForTitleContains("nopCommerce", 5);
        String actualTitle = seleniumHelper.getPageTitle();

        logger.info("Actual Page Title: " + actualTitle);

        // Assert using helper method
        Assert.assertTrue(actualTitle.contains("nopCommerce demo store"), "Homepage title is incorrect!");
    }

    @Test
    public void verifyHeaderLinks() {
        logger.info("Navigating to: https://demo.nopcommerce.com");
        driver.get("https://demo.nopcommerce.com");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        SeleniumHelper seleniumHelper = new SeleniumHelper(driver);

        // Define all header links
        By[] headerLinks = {
                By.cssSelector(".ico-register"),
                By.cssSelector(".ico-login"),
                By.cssSelector(".ico-wishlist"),
                By.cssSelector(".ico-cart")
        };

        String[] linkNames = {"Register", "Login", "Wishlist", "Shopping Cart"};

        // Verify each link is present, visible, and clickable
        for (int i = 0; i < headerLinks.length; i++) {
            By locator = headerLinks[i];
            String linkName = linkNames[i];

            logger.info("Verifying " + linkName + " link...");

            WebElement link = wait.until(ExpectedConditions.elementToBeClickable(locator));
            Assert.assertTrue(link.isDisplayed(), linkName + " link is not displayed!");

            link.click();

            // Wait for the new page to load
            wait.until(ExpectedConditions.stalenessOf(link));

            // Ensure that at least one element is present on the new page
            List<WebElement> pageElements = driver.findElements(By.cssSelector("body *"));
            Assert.assertFalse(pageElements.isEmpty(), linkName + " page did not load properly!");

            logger.info(linkName + " link verification successful!");
        }

        logger.info("All header links are verified and clickable!");
    }

}
