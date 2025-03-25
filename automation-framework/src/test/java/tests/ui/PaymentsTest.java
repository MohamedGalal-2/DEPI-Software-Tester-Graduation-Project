package tests.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;

import base.BaseTest;
import utils.MainFunctionalities;
import utils.SeleniumHelper;
import utils.ConfigReader;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PaymentsTest extends BaseTest {
    SeleniumHelper helper;

    private final String url = ConfigReader.getProperty("baseURL");
    private final String loginUrl = ConfigReader.getProperty("loginURL");
    private final String cartUrl = ConfigReader.getProperty("cartURL");
    private final String photoshopUrl = ConfigReader.getProperty("adobePhotoshopUrl");
    private final String photoshopID = helper.getProductID("adobePhotoshopID");
    private final String photoshopName = helper.getProductName("adobePhotoshopName");

    String[] details = {
            "Maegan",
            "Rolfson",
            "48dd6282b6@emaily.pro",
            "United States of America",
            "Alaska",
            "Anchorage",
            "PO Box 242941",
            "99524",
            "9072430988",
            "Maegan Rolfson",
            "3595972973139459",
            "10",
            "2029",
            "469"
    };

    @Test(groups = {"smoke"}, description = "TC-012")
    public void verifyExpiredPaymentMethod() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        // Step 1: Add product to cart
        main.addProductToCart(photoshopUrl, photoshopID);

        // Step 2: Enter Address Details
        main.checkoutAddressOnly(details);

        // Step 3: Choose shipping method
        helper.realisticDelay();
        helper.waitForClickable(helper.getByLocator("shipping.option1")).click();
        driver.findElement(helper.getByLocator("shipping.continueButton")).click();
        System.out.println("Shipping method selected.");

        // Step 4: Enter payment details
        main.enterPaymentDetails(details[9], "111111111111111111111", "01", "2025", "123");

        // Step 5: Validate error messages
        helper.realisticDelay();
        WebElement errorMessages = helper.waitForVisibility(helper.getByLocator("checkout.paymentErrorMessage"));
        Assert.assertTrue(errorMessages.getText().contains("Wrong card number"), "Error message not displayed for invalid card number.");
        Assert.assertTrue(errorMessages.getText().contains("Card is expired"), "Error message not displayed for invalid card code.");
        logger.info("Error message displayed for invalid payment details.");

    }

    @Test(groups = {"functional"}, description = "TC-082")
    public void verifyPaymentViaCreditCard() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        String firstName = "Test";
        String lastName = "User";
        String emailAddress = "TestUserTest16@TestUser.Test";
        String validPassword = "123456";

        // Step 1: Register an account
        main.register(firstName, lastName, emailAddress, validPassword);

        //Step 2: Login with valid credentials
        main.login(emailAddress, validPassword);

        // Step 3: Add product to the card
        main.addProductToCart(photoshopUrl, photoshopID);

        // Step 4: Checkout
        main.checkoutAsUserWithoutContinue(details);
    }

    @Test(groups = {"functional"}, description = "TC-083_TC-084")
    public void verifyAvailablePaymentMethods() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        // Step 1: Add product to the cart
        main.addProductToCart(photoshopUrl, photoshopID);

        // Step 2: Proceed to checkout and check payment methods
        String[] userDetails = {"John", "Doe", "johndoe@example.com", "United States of America", "California", "Los Angeles", "123 Main St", "90001", "555-1234"};
        main.checkPaymentMethods(userDetails);

        // Ensure the payment section is fully loaded
        helper.realisticDelay();
        WebElement paymentSection = helper.waitForVisibility(helper.getByLocator("payment.methodBlock"));
        logger.info("Payment section loaded successfully.");

        // Retrieve available payment options
        helper.realisticDelay();
        List<WebElement> paymentOptions = helper.waitForAllElementsPresence(By.cssSelector("input[type='radio']"));
        Assert.assertFalse(paymentOptions.isEmpty(), "No payment options found.");
        logger.info("Payment options are listed.");

        // Verify specific payment options exist
        boolean isPayPalAvailable = false;
        boolean isCODAvailable = false;
        boolean isCreditCardAvailable = false;

        for (WebElement option : paymentOptions) {
            String labelText = option.findElement(helper.getByLocator("radioLabelXPath")).getText();
            if (labelText.contains("PayPal")) {
                isPayPalAvailable = true;
                logger.info("PayPal payment method is available.");
            } else if (labelText.contains("Cash On Delivery")) {
                isCODAvailable = true;
                logger.info("Cash On Delivery (COD) payment method is available.");
            } else if (labelText.contains("Credit Card")) {
                isCreditCardAvailable = true;
                logger.info("Credit Card payment method is available.");
            }
        }

        // Validate the presence of required payment methods
        Assert.assertTrue(isPayPalAvailable || isCODAvailable, "Neither PayPal nor Cash On Delivery is available!");
        Assert.assertTrue(isCreditCardAvailable, "Credit Card payment method is not available!");
        logger.info("Payment methods validation completed.");
    }

    @Test(groups = {"security"}, description = "TC-088")
    public void verifyDuplicatePaymentPrevention() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        // Step 1: Add product to cart
        main.addProductToCart(photoshopUrl, photoshopID);

        // Step 2: Proceed to checkout
        main.checkoutAddressOnly(details);

        // Step 3: Choose shipping method
        helper.realisticDelay();
        helper.waitForClickable(helper.getByLocator("shipping.option1")).click();
        driver.findElement(helper.getByLocator("shipping.continueButton")).click();
        System.out.println("Shipping method selected.");

        // Step 4: Enter payment details
        main.enterPaymentDetails(details[9], details[10], details[11], details[12], details[13]);

        driver.findElement(helper.getByLocator("payment.infoContinueButton")).click();
        logger.info("Payment details entered.");

        // Step 14: Intercept payment (simulate duplicate payment attempt)
        try {
            // Attempt to submit the payment again (simulate duplicate payment button click)
            WebElement duplicatePaymentButton = driver.findElement(helper.getByLocator("payment.infoContinueButton"));
            duplicatePaymentButton.click(); // This is a duplicate payment request

            // If the system allows the second payment request, this line will fail the test
            Assert.fail("Duplicate payment request should have been blocked!");
        } catch (Exception e) {
            // Expected behavior: The duplicate payment should not be allowed, so the exception will be caught
            logger.info("Duplicate payment request prevented successfully.");
        }

        // Step 15: Confirm the order and verify that only one payment was processed
        helper.realisticDelay();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#confirm-order-buttons-container button"))).click();
        WebElement confirmationMessage = helper.waitForVisibility(By.cssSelector(".section.order-completed"));
        Assert.assertTrue(confirmationMessage.getText().contains("Your order has been successfully processed!"), "Order not successful!");
    }

    @Test(groups = {"functional"}, description = "TC-095")
    public void verifyPaymentFailsWithInsufficientFunds() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        // Step 1: Add product to the cart
        main.addProductToCart(photoshopUrl, photoshopID);

        // Step 2: Proceed to checkout
        main.checkoutAsGuest(details);

        // Step 3: Confirm order
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#confirm-order-buttons-container button"))).click();
        logger.info("Order confirmed.");

        // Step 4: Attempt to confirm order
        try {
            SeleniumHelper.realisticDelay();
            WebElement errorMessage = helper.waitForVisibility(By.cssSelector(".message-error"));
            Assert.assertTrue(errorMessage.getText().contains("insufficient funds"), "Expected 'insufficient funds' error message was not displayed.");
            logger.info("Transaction correctly declined due to insufficient funds.");
        } catch (TimeoutException e) {
            // If no error message appears and order is completed, fail the test
            logger.error("Transaction was completed despite insufficient funds. Test failed!");
            Assert.fail("Transaction should have been declined due to insufficient funds, but it was processed successfully.");
        }
    }

}