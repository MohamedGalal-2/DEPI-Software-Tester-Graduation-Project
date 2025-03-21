package tests.ui;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BaseTest;
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
    private final String url = ConfigReader.getProperty("baseURL");
    private final String loginUrl = ConfigReader.getProperty("loginURL");
    private final String cartUrl = ConfigReader.getProperty("cartURL");
    private final String wishlistUrl = ConfigReader.getProperty("wishlistURL");
    private final String registrationUrl = ConfigReader.getProperty("registrationURL");


    @Test(groups = {"ui"}, description = "TC-060")
    public void verifyCustomerReviewsDisplayed() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Navigate to the product page
        driver.get("https://demo.nopcommerce.com/25-virtual-gift-card");
        logger.info("Navigated to product page: $25 Virtual Gift Card");

        // Step 2: Scroll to the reviews section
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        SeleniumHelper.realisticDelay();

        // Step 3: Verify reviews section is present
        WebElement reviewSection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-review-list")));
        Assert.assertTrue(reviewSection.isDisplayed(), "Customer reviews section is not visible");
        logger.info("Customer reviews section is visible");

        // Step 4: Verify a review is displayed
        WebElement reviewTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".review-title")));
        Assert.assertFalse(reviewTitle.getText().isEmpty(), "Review title is missing");
        logger.info("Review title: " + reviewTitle.getText());

        // Step 5: Verify reviewer name, date, and content
        WebElement reviewerName = driver.findElement(By.cssSelector(".review-info .user span"));
        WebElement reviewDate = driver.findElement(By.cssSelector(".review-info .date span"));
        WebElement reviewContent = driver.findElement(By.cssSelector(".review-text .text-body"));

        Assert.assertFalse(reviewerName.getText().isEmpty(), "Reviewer name is missing");
        Assert.assertFalse(reviewDate.getText().isEmpty(), "Review date is missing");
        Assert.assertFalse(reviewContent.getText().isEmpty(), "Review content is missing");

        logger.info("Reviewer: " + reviewerName.getText());
        logger.info("Review Date: " + reviewDate.getText());
        logger.info("Review Content: " + reviewContent.getText());

        // Step 6: Verify star rating is displayed
        WebElement rating = driver.findElement(By.cssSelector(".product-review-box .rating div"));
        Assert.assertTrue(rating.getAttribute("style").contains("width"), "Rating is not displayed");
        logger.info("Star rating is displayed");

        // Step 7: Verify review helpfulness section
        WebElement helpfulnessQuestion = driver.findElement(By.cssSelector(".product-review-helpfulness .question"));
        Assert.assertTrue(helpfulnessQuestion.isDisplayed(), "Helpfulness section is missing");
        logger.info("Helpfulness voting section is present");

        // Step 8: Verify unregistered users cannot submit reviews
        WebElement reviewRestriction = driver.findElement(By.cssSelector(".review-already-added"));
        Assert.assertTrue(reviewRestriction.isDisplayed(), "Review restriction message not visible");
        Assert.assertEquals(reviewRestriction.getText().trim(), "Only registered users can write reviews", "Review restriction message is incorrect");
        logger.info("Verified unregistered users cannot submit reviews");
    }

    @Test(groups = {"functional"}, description = "TC-061")
    public void verifyReviewSubmission() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        String emailAddress = "TestUserTest@TestUser.Test";
        String password = "123456";

        // Step 1: Log in to the website
        driver.get("https://demo.nopcommerce.com/login");
        logger.info("Navigating to login page.");

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Email")));
        WebElement passwordField = driver.findElement(By.id("Password"));
        WebElement loginButton = driver.findElement(By.cssSelector("button.login-button"));

        emailField.sendKeys(emailAddress);
        passwordField.sendKeys(password);
        loginButton.click();
        logger.info("User logged in successfully.");

        // Step 2: Navigate to the product page
        driver.get("https://demo.nopcommerce.com/25-virtual-gift-card");
        logger.info("Navigating to product page: $25 Virtual Gift Card");

        // Step 3: Scroll to the reviews section and ensure it is visible
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        SeleniumHelper.realisticDelay();

        // Ensure review form is visible
        WebElement reviewForm = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("review-form")));
        Assert.assertTrue(reviewForm.isDisplayed(), "Review form is not visible.");

        // Click 'Write a Review' (Fix: Using correct selector and JavaScript click if needed)
        WebElement writeReviewButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-review")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", writeReviewButton);
        logger.info("Clicked 'Write a Review' button.");

        // Step 4: Enter review details
        WebElement reviewTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("AddProductReview_Title")));
        WebElement reviewText = driver.findElement(By.id("AddProductReview_ReviewText"));
        WebElement ratingOption = driver.findElement(By.id("addproductrating_4")); // Selecting 4-star rating
        WebElement submitReviewButton = driver.findElement(By.id("add-review"));

        reviewTitle.sendKeys("Great Gift Card!");
        reviewText.sendKeys("I purchased this gift card, and it was very convenient to use.");
        ratingOption.click();
        submitReviewButton.click();
        logger.info("Submitted the review successfully.");

        // Step 5: Verify review submission success notification
        WebElement successNotification = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#bar-notification .content")));
        Assert.assertTrue(successNotification.getText().contains("Product review is successfully added"), "Review submission failed!");
        logger.info("Review submission confirmed.");
    }



}