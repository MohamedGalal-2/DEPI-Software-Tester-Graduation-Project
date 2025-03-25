package tests.ui;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BaseTest;
import utils.MainFunctionalities;
import utils.SeleniumHelper;
import utils.ConfigReader;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewsTest extends BaseTest {
    private final SeleniumHelper helper = new SeleniumHelper(driver);

    // URLs
    private final String url = ConfigReader.getProperty("baseURL");
    private final String loginUrl = ConfigReader.getProperty("loginURL");
    private final String cartUrl = ConfigReader.getProperty("cartURL");
    private final String wishlistUrl = ConfigReader.getProperty("wishlistURL");
    private final String macbookUrl = ConfigReader.getProperty("appleMacbookProUrl");
    private final String computerUrl = ConfigReader.getProperty("buildYourComputerUrl");

    // Product IDs
    private final String macbookID = helper.getProductID("appleMacbookProID");
    private final String computerID = helper.getProductID("buildYourComputerID");

    // Product Names
    private final String macbookName = helper.getProductName("appleMacbookProName");
    private final String computerName = helper.getProductName("buildYourComputerName");


    @Test(groups = {"ui"}, description = "TC-060")
    public void verifyCustomerReviewsDisplayed() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        logger.info("Navigating to: " + url);
        driver.get(url);

        // Step 1: Go to the product page
        driver.get(computerUrl);

        // Step 2: Check if reviews are displayed
        main.checkReviews();
    }

    @Test(groups = {"functional"}, description = "TC-061")
    public void verifyReviewSubmission() throws InterruptedException {
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        String firstName = "Test";
        String lastName = "User";
        String email = "TestUserTest@TestUser.Test";
        String password = "123456";

        // Step 1: Register a new user
        main.register(firstName, lastName, email, password);

        // Step 2: Login with valid credentials
        main.login(email, password);

        // Step 3: Go to the product page
        driver.get("https://demo.nopcommerce.com/25-virtual-gift-card");

        // Step 4: Navigate to the product page and submit a review
        Assert.assertTrue(main.submitReview(
                "Great Gift Card!",
                "I purchased this gift card, and it was very convenient to use.",
                4
        ), "Review submission failed!");

        logger.info("Review submission confirmed.");
    }
}