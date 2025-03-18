package tests.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;

import base.BaseTest;
import utils.SeleniumHelper;
import utils.ConfigReader;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class PaymentsTest extends BaseTest {
    private final String url = ConfigReader.getProperty("baseURL");
    private final String loginUrl = ConfigReader.getProperty("loginURL");
    private final String cartUrl = ConfigReader.getProperty("cartURL");

    @Test(groups = {"smoke"}, description = "TC-012")
    public void verifyExpiredPaymentMethod() throws InterruptedException {
        logger.info("Navigating to: " + loginUrl);
        driver.get(loginUrl);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 2: Enter login credentials
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Email"))).sendKeys("7a6d6efc3d@emaily.pro");
        driver.findElement(By.id("Password")).sendKeys("TestPassword123");

        // Step 3: Click login button
        driver.findElement(By.cssSelector("button.login-button")).click();
        System.out.println("User logged in successfully.");

        // Step 4: Verify successful login
        SeleniumHelper.realisticDelay();
        boolean isLoggedIn = driver.findElements(By.className("ico-logout")).size() > 0;
        Assert.assertTrue(isLoggedIn, "Login failed!");
        logger.info("Login verified, proceeding with checkout process.");

        // Step 5: Navigate to the product page
        String productUrl = "https://demo.nopcommerce.com/adobe-photoshop";
        driver.get(productUrl);
        System.out.println("Navigating to product page: " + productUrl);

        // Step 6: Add product to cart
        SeleniumHelper.realisticDelay();
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-10")));
        addToCartButton.click();
        System.out.println("Clicked 'Add to Cart' button.");

        // Step 7: Wait for cart success message
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bar-notification.success")));
        Assert.assertTrue(successMessage.isDisplayed(), "Product was not added to the cart!");
        logger.info("Product successfully added to cart.");

        // Step 8: Go to cart page
        driver.get(cartUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".table-wrapper")));
        System.out.println("Cart page loaded successfully.");

        // Step 9: Accept terms and proceed to checkout
        WebElement termsCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("termsofservice")));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }
        System.out.println("Accepted terms of service.");
        driver.findElement(By.id("checkout")).click();

        logger.info("Checkout page loaded successfully.");
        SeleniumHelper.realisticDelay();

        // Step 10: Click continue
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".new-address-next-step-button")));
        continueButton.click();

        // Step 11: Choose shipping method
        wait.until(ExpectedConditions.elementToBeClickable(By.id("shippingoption_1"))).click();
        driver.findElement(By.cssSelector("#shipping-method-buttons-container button")).click();
        System.out.println("Shipping method selected.");

        // Step 12: Select payment method
        wait.until(ExpectedConditions.elementToBeClickable(By.id("paymentmethod_1"))).click();
        driver.findElement(By.cssSelector("#payment-method-buttons-container button")).click();
        System.out.println("Payment method selected.");

        // Step 13: Enter invalid payment details
        driver.findElement(By.id("CardholderName")).sendKeys("John Doe");
        driver.findElement(By.id("CardNumber")).sendKeys("1234567812345678");  // Invalid card number

        Select expMonth = new Select(driver.findElement(By.id("ExpireMonth")));
        expMonth.selectByVisibleText("01");

        Select expYear = new Select(driver.findElement(By.id("ExpireYear")));
        expYear.selectByVisibleText("2025");

        driver.findElement(By.id("CardCode")).sendKeys("123");  // Invalid card code

        driver.findElement(By.cssSelector("#payment-info-buttons-container button")).click();
        System.out.println("Payment details entered.");

        // Step 14: Validate error messages
        WebElement errorMessages = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message-error.validation-summary-errors")));
        Assert.assertTrue(errorMessages.getText().contains("Wrong card number"), "Error message not displayed for invalid card number.");
        Assert.assertTrue(errorMessages.getText().contains("Card is expired"), "Error message not displayed for invalid card code.");
        logger.info("Error message displayed for invalid payment details.");
    }
}