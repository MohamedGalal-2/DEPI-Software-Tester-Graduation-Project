package tests.ui;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;

import base.BaseTest;
import org.testng.SkipException;
import org.testng.reporters.jq.Main;
import utils.MainFunctionalities;
import utils.SeleniumHelper;
import utils.ConfigReader;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.ArrayList;

public class CheckoutTest extends BaseTest {
    private final SeleniumHelper helper = new SeleniumHelper(driver);

    // URLs
    private final String url = ConfigReader.getProperty("baseURL");
    private final String loginUrl = ConfigReader.getProperty("loginURL");
    private final String cartUrl = ConfigReader.getProperty("cartURL");
    private final String wishlistUrl = ConfigReader.getProperty("wishlistURL");
    private final String photoshopUrl = ConfigReader.getProperty("adobePhotoshopUrl");
    private final String iphoneUrl = ConfigReader.getProperty("appleIphoneUrl");

    // Product IDs
    private final String photoshopID = helper.getProductID("adobePhotoshopID");
    private final String iphoneID = ConfigReader.getProperty("appleIphoneID");

    // Product Names
    private final String photoshopName = helper.getProductName("adobePhotoshopName");
    private final String iphoneName = ConfigReader.getProperty("appleIphoneName");

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

    @Test(groups = {"smoke"}, description = "TC-004")
    public void verifyCheckoutPageLoads() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        // Step 1: Add product to cart
        main.addProductToCart(photoshopUrl, photoshopID);

        // Step 2: Process checkout
        main.goToCheckout();
    }

    @Test(groups = {"smoke"}, description = "TC-005_TC-006_TC-072")
    public void verifyCheckoutProcessAsGuest() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        // Step 1: Add product to cart
        main.addProductToCart(photoshopUrl, photoshopID);

        // Step 2: Process checkout as guest
        main.checkoutAsGuest(details);

        // Step 3: Confirm order
        helper.realisticDelay();
        wait.until(ExpectedConditions.elementToBeClickable(helper.getByLocator("order.confirmButton"))).click();
        logger.info("Order confirmed.");

        // Step 4: Validate order success
        helper.realisticDelay();
        WebElement orderSuccessMessage = helper.waitForVisibility(helper.getByLocator("order.successMessage"));
        Assert.assertTrue(orderSuccessMessage.getText().contains("Your order has been successfully processed!"), "Order not successful!");
        logger.info("Order placed successfully!");
    }

    @Test(groups = {"functional"}, description = "TC-073")
    public void testCheckoutProcessAsUser() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        String firstName = "Test";
        String lastName = "User";
        String emailAddress = "TestUserTest@TestUser.Test";
        String validPassword = "123456";

        // Step 1: Register new user
        main.register(firstName, lastName, emailAddress, validPassword);

        // Step 2: Login with valid credentials
        main.login(emailAddress, validPassword);

        // Step 3: Add product to cart
        main.addProductToCart(photoshopUrl, photoshopID);

        // Step 4: Proceed checkout process
        main.checkoutAsUserWithoutContinue(details);
    }

    @Test(groups = {"functional"}, description = "TC-080")
    public void verifyInvalidAddressPreventsCheckout() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        String[] invalidDetails = {
                "John",
                "Doe",
                "Select country",
                "",
                "",
                "",
                "123",
        };

        // Step 1: Add product to cart
        main.addProductToCart(photoshopUrl, photoshopID);

        // Step 2: Proceed checkout process
        main.checkoutAddressOnly(invalidDetails);

    }

    @Test(groups = {"functional"}, description = "TC-081")
    public void verifyOrderIsGenerated() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        // Step 1: Add product to cart
        main.addProductToCart(photoshopUrl, photoshopID);

        // Step 2: Proceed checkout process
        main.checkoutAsGuest(details);

        // Step 3: Confirm order
        helper.realisticDelay();
        wait.until(ExpectedConditions.elementToBeClickable(helper.getByLocator("order.confirmButton"))).click();
        logger.info("Order confirmed.");

        // Step 4: Validate order success
        helper.realisticDelay();
        WebElement orderSuccessMessage = helper.waitForVisibility(helper.getByLocator("order.successMessage"));
        Assert.assertTrue(orderSuccessMessage.getText().contains("Your order has been successfully processed!"), "Order not successful!");
        logger.info("Order placed successfully!");

        // Step 5: Verify order number is displayed
        WebElement orderNumberElement = driver.findElement(By.cssSelector(".order-number strong"));
        String orderNumber = orderNumberElement.getText().replaceAll("[^0-9]", ""); // Extract order number
        Assert.assertFalse(orderNumber.isEmpty(), "Order number is missing!");
        logger.info("Order number generated: " + orderNumber);

        System.out.println("Order placed successfully. Order Number: " + orderNumber);
    }

    @Test(groups = {"ui"}, description = "TC-187")
    public void verifyOrderSummaryDisplaysCorrectInfo() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        String[] orderDetails = {
                "Apple iPhone 16 128GB",
                "$799.00",
                "1",
                "$799.00",
                "$0.00",
                "$799.00"
        };

        // Step 1: Add product to cart
        main.addProductToCart(iphoneUrl, iphoneID);

        // Step 2: Proceed checkout process
        main.checkoutAsGuest(details);

        // Step 3: Check order summary
        main.verifyProductDetails(orderDetails);
    }

    @Test(groups = {"ui"}, description = "TC-189")
    public void verifyDifferentPaymentOptionsAreAvailable() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        // Step 1: Add product to cart
        main.addProductToCart(photoshopUrl, photoshopID);

        // Step 2: Check payment options
        main.checkPaymentMethods(details);
    }
}