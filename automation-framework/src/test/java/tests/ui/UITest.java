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

public class UITest extends BaseTest {
    private final String url = ConfigReader.getProperty("baseURL");
    private final String loginUrl = ConfigReader.getProperty("loginURL");
    private final String cartUrl = ConfigReader.getProperty("cartURL");
    private final String wishlistUrl = ConfigReader.getProperty("wishlistURL");

    @Test(groups = {"smoke", "security"}, description = "TC-021")
    public void verifySessionHandling() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Navigate to the login page
        String loginUrl = "https://demo.nopcommerce.com/login";
        driver.get(loginUrl);
        logger.info("Navigating to login page.");

        // Step 2: Log in with valid credentials
        SeleniumHelper.realisticDelay();
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Email")));
        WebElement passwordField = driver.findElement(By.id("Password"));
        SeleniumHelper.realisticDelay();
        WebElement loginButton = driver.findElement(By.cssSelector("button.login-button"));

        emailField.sendKeys("7a6d6efc3d@emaily.pro");
        passwordField.sendKeys("TestPassword123");
        loginButton.click();
        logger.info("User logged in.");

        // Step 3: Verify user is logged in (check for a user-specific element, like the account name or logout button)
        SeleniumHelper.realisticDelay();
        WebElement accountLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ico-account")));
        Assert.assertTrue(accountLink.isDisplayed(), "User is not logged in!");
        logger.info("User is logged in successfully.");

        // Step 4: Refresh the browser to simulate session persistence
        driver.navigate().refresh();

        // Step 5: Verify that the user is still logged in after refresh or reopening
        SeleniumHelper.realisticDelay();
        WebElement accountLinkAfterRefresh = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ico-account")));
        Assert.assertTrue(accountLinkAfterRefresh.isDisplayed(), "User is not logged in after refresh or browser reopen!");
        logger.info("User remains logged in after refresh or browser reopen.");
    }
}
