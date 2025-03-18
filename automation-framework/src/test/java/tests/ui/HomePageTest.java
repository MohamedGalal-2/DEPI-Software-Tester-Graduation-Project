package tests.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BaseTest;
import utils.SeleniumHelper;
import utils.ConfigReader;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class HomePageTest extends BaseTest {
    private final String url = ConfigReader.getProperty("baseURL");

    @Test(groups = {"smoke"}, description = "TC-001")
    public void verifyHomePageLoads() {
        logger.info("Navigating to: " + url);
        driver.get(url);

        SeleniumHelper seleniumHelper = new SeleniumHelper(driver);

        // Get timeout from properties file
        int timeout = ConfigReader.getIntProperty("explicitWait");

        // Wait for title and get the actual title
        seleniumHelper.waitForTitleContains("nopCommerce", timeout);
        String actualTitle = seleniumHelper.getPageTitle();

        logger.info("Actual Page Title: " + actualTitle);

        // Verify page title from properties
        Assert.assertTrue(actualTitle.contains(ConfigReader.getProperty("homePageTitle")),
                "Homepage title is incorrect!");
    }

    @Test
    public void verifyHeaderLinks() {
        logger.info("Navigating to: " + url);
        driver.get(url);

        int timeout = ConfigReader.getIntProperty("explicitWait");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        SeleniumHelper seleniumHelper = new SeleniumHelper(driver);

        // Fetch header links from properties
        By[] headerLinks = {
                By.cssSelector(ConfigReader.getProperty("registerLink")),
                By.cssSelector(ConfigReader.getProperty("loginLink")),
                By.cssSelector(ConfigReader.getProperty("wishlistLink")),
                By.cssSelector(ConfigReader.getProperty("cartLink"))
        };

        String[] linkNames = {"Register", "Login", "Wishlist", "Shopping Cart"};

        for (int i = 0; i < headerLinks.length; i++) {
            By locator = headerLinks[i];
            String linkName = linkNames[i];

            logger.info("Verifying " + linkName + " link...");

            WebElement link = wait.until(ExpectedConditions.elementToBeClickable(locator));
            Assert.assertTrue(link.isDisplayed(), linkName + " link is not displayed!");

            link.click();
            wait.until(ExpectedConditions.stalenessOf(link));

            List<WebElement> pageElements = driver.findElements(By.cssSelector("body *"));
            Assert.assertFalse(pageElements.isEmpty(), linkName + " page did not load properly!");

            logger.info(linkName + " link verification successful!");
        }

        logger.info("All header links are verified and clickable!");
    }
}
