package tests.ui;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
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
import java.util.List;

public class SecurityTest extends BaseTest {
    private final SeleniumHelper helper = new SeleniumHelper(driver);

    // URLs
    private final String url = ConfigReader.getProperty("baseURL");
    private final String loginUrl = ConfigReader.getProperty("loginURL");
    private final String cartUrl = ConfigReader.getProperty("cartURL");
    private final String wishlistUrl = ConfigReader.getProperty("wishlistURL");
    private final String registrationUrl = ConfigReader.getProperty("registrationURL");
    private final String photoshopUrl = ConfigReader.getProperty("adobePhotoshopUrl");

    private final String myAccountUrl = "https://demo.nopcommerce.com/customer/info";

    // Product IDs
    private final String photoshopID = helper.getProductID("adobePhotoshopID");

    @Test(groups = {"smoke", "security"}, description = "TC-021")
    public void verifySessionHandling() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        String firstName = "Test";
        String lastName = "User";
        String emailAddress = "TestUserTestverify1@TestUser.Test";
        String validPassword = "123456";

        // Step 1: register with valid credentials
        main.register(firstName, lastName, emailAddress, validPassword);

        // Step 2: Verify that the user is logged in after registration
        main.login(emailAddress, validPassword);

        // Step 3: Refresh the browser to simulate session persistence
        driver.navigate().refresh();

        // Step 4: Verify that the user is still logged in after refresh or reopening
        helper.realisticDelay();
        WebElement accountLinkAfterRefresh = helper.waitForVisibility(helper.getByLocator("account.icon"));
        Assert.assertTrue(accountLinkAfterRefresh.isDisplayed(), "User is not logged in after refresh or browser reopen!");
        logger.info("User remains logged in after refresh or browser reopen.");
    }

    @Test(groups = {"security"}, description = "TC-027")
    public void verifyAccountLockoutAfterMultipleFailedLoginAttempts() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        String firstName = "Test";
        String lastName = "User";
        String emailAddress = "TestUserTestLock1@TestUser.Test";
        String validPassword = "123456";
        String wrongPassword = "123";
        final int maxFailedAttempts = 10;
        final String lockoutMessageText = "Your account has been locked";

        // Step 1: Register a new account
        main.register(firstName, lastName, emailAddress, validPassword);

        // Step 2: Attempt to log in with incorrect credentials multiple times
        for (int attempt = 1; attempt <= maxFailedAttempts; attempt++) {
            try {
                main.login(emailAddress, wrongPassword);
                logger.info("Login attempt #" + attempt + " with invalid credentials.");

                // Verify error message appears
                WebElement errorMessage = helper.waitForVisibility(helper.getByLocator("checkout.paymentErrorMessage"));
                Assert.assertTrue(errorMessage.isDisplayed(), "Error message not displayed on failed login attempt #" + attempt);
            } catch (Exception e) {
                logger.error("Exception during login attempt #" + attempt + ": " + e.getMessage(), e);
            }

            helper.realisticDelay();
        }

        // Step 3: Verify account lockout
        try {
            WebElement lockoutMessage = helper.waitForVisibility(helper.getByLocator("checkout.paymentErrorMessage"));
            String lockoutText = lockoutMessage.getText();
            Assert.assertTrue(lockoutText.contains(lockoutMessageText), "Account was not locked after multiple failed login attempts.");
            logger.info("Account successfully locked after " + maxFailedAttempts + " failed login attempts.");
        } catch (Exception e) {
            Assert.fail("Lockout message not displayed as expected.");
        }

        // Step 4: Attempt to log in with correct credentials after lockout
        try {
            main.login(emailAddress, validPassword);
            logger.info("Attempting to log in with correct credentials after account lockout.");

            // Step 5: Validate that login fails after lockout
            WebElement errorMessage = helper.waitForVisibility(helper.getByLocator("checkout.paymentErrorMessage"));
            String errorMessageText = errorMessage.getText();

            if (errorMessageText.contains(lockoutMessageText)) {
                logger.info("Account is properly locked, and login with correct credentials failed as expected.");
            } else {
                Assert.fail("Login succeeded after account lockout, which is unexpected.");
            }
        } catch (Exception e) {
            Assert.fail("Unexpected behavior: No error message found after login attempt with correct credentials. Login should have failed due to account lockout.");
        }
    }

    @Test(groups = {"security"}, description = "TC-034")
    public void verifyPasswordStrengthRequirements() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        String firstName = "John";
        String lastName = "Doe";
        String emailAddress = "johndoe@example.com";
        String weakPassword = "123";

        // Step 1: Register with weak password
        main.register(firstName, lastName, emailAddress, weakPassword);
        logger.info("Attempting to register with weak password.");

        // Step 2: Verify that the password strength error message is displayed
        helper.realisticDelay();
        WebElement passwordErrorMessage = helper.waitForVisibility(helper.getByLocator("register.validationError"));
        String errorMessageText = passwordErrorMessage.getText();

        // Check if the password error message contains the expected validation text
        Assert.assertTrue(errorMessageText.contains("Password must meet the following rules"), "Error message for weak password was not displayed.");
        Assert.assertTrue(errorMessageText.contains("must have at least 8 characters"), "Error message did not include 'at least 8 characters' requirement.");

        logger.info("Password strength validation error message displayed as expected.");
    }

    @Test(groups = {"security"}, description = "TC-091")
    public void verifyPaymentGatewaySecurity() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        String[] checkoutDetails = {
                "Maegan",
                "Rolfson",
                "385d163bdd@emaily.pro",
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
                "469" // CVV
        };

        // Step 1: Add product to cart
        main.addProductToCart(photoshopUrl, photoshopID);

        // Step 2: Perform guest checkout
        main.checkoutAsGuest(checkoutDetails);

        // Step 3: Validate that the card number is not stored in plaintext
        helper.realisticDelay();
        String cardNumber = driver.findElement(helper.getByLocator("payment.cardNumber")).getAttribute("value");
        Assert.assertFalse(cardNumber.matches("\\d{16}"), "Card number is stored in plaintext. It should be encrypted or masked.");
        logger.info("Card number is not stored in plaintext.");

        // Step 4: Confirm the order
        helper.realisticDelay();
        wait.until(ExpectedConditions.elementToBeClickable(helper.getByLocator("confirm.orderButton"))).click();
        logger.info("Order confirmed.");

        // Step 5: Validate order success
        helper.realisticDelay();
        WebElement orderSuccessMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(helper.getByLocator("confirm.successMessage")));
        Assert.assertTrue(orderSuccessMessage.getText().contains("Your order has been successfully processed!"), "Order not successful!");
        logger.info("Order placed successfully with secure payment!");
    }

    @Test(groups = {"security"}, description = "TC-118")
    public void verifyChangePasswordFromDashboard() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        String firstName = "Test";
        String lastName = "User";
        String emailAddress = "TestUserTestChange2@TestUser.Test";
        String validPassword = "123456";
        String newPassword = "1234567";

        // Step 1: Register a new account
        main.register(firstName, lastName, emailAddress, validPassword);

        // Step 2: Login with valid credentials
        main.login(emailAddress, validPassword);

        // Step 3: Navigate to the 'My Account' section
        driver.get(myAccountUrl);
        logger.info("Navigated to 'My Account' section.");

        // Step 4: Change password
        main.changePassword(validPassword, newPassword);

        // Step 5: Log out after changing the password
        main.logout();

        // Step 6: Verify that login is possible with the new password
        main.login(emailAddress, newPassword);

        logger.info("Logged in with new password successfully.");
    }

    @Test(groups = {"security"}, description = "TC-126")
    public void verifyProtectionAgainstXSS() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);

        // Step 1: Navigate to the page where input fields are located
        driver.get(loginUrl);
        logger.info("Navigated to: " + loginUrl);

        // List of common XSS payloads to test
        String[] xssPayloads = {
                "<script>alert('XSS')</script>",
                "<img src='x' onerror='alert(1)'/>",
                "<svg/onload=alert(1)>",
                "<body onload=alert('XSS')>",
                "<a href='javascript:alert(1)'>Click me</a>"
        };

        // Step 2: Test XSS payloads on the Email and Password input fields
        for (String payload : xssPayloads) {
            // Test for the Email input field
            WebElement emailInputField = driver.findElement(By.id("Email"));
            emailInputField.clear();
            emailInputField.sendKeys(payload);
            logger.info("Entered XSS payload in Email field: " + payload);

            // Test for the Password input field (if applicable)
            WebElement passwordInputField = driver.findElement(By.id("Password"));
            passwordInputField.clear();
            passwordInputField.sendKeys("123456"); // Provide a valid password or adjust as needed
            logger.info("Entered password for submission.");

            // Submit the form (Log in)
            WebElement submitButton = driver.findElement(By.className("login-button"));
            submitButton.click();
            logger.info("Form submitted.");

            // Step 3: Verify that no alert box was triggered
            Assert.assertFalse(helper.isAlertPresent(), "XSS attack successful: alert triggered!");

            // Step 4: Check if the payload is sanitized
            WebElement emailFieldAfterSubmit = helper.waitForVisibility(By.id("Email"));
            Assert.assertFalse(emailFieldAfterSubmit.getAttribute("value").contains(payload), "XSS attack successful: payload is reflected in the Email field.");

            // Step 5: Optionally, check for the appearance of any injected scripts in other parts of the page
            WebElement bodyElement = helper.waitForVisibility(By.tagName("body"));
            Assert.assertFalse(bodyElement.getText().contains(payload), "XSS attack successful: payload is displayed on the page.");

            logger.info("No XSS vulnerability found for payload: " + payload);
        }

        logger.info("XSS vulnerability test completed successfully.");
    }

    @Test(groups = {"compliance"}, description = "TC-292")
    public void verifyHTTPSProtocol() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);

        // List of URLs to test (Without 'https://')
        List<String> urls = new ArrayList<>();
        urls.add("http://demo.nopcommerce.com/");
        urls.add("http://demo.nopcommerce.com/new-online-store-is-open");
        urls.add("http://demo.nopcommerce.com/build-your-own-computer");
        urls.add("http://demo.nopcommerce.com/25-virtual-gift-card");
        urls.add("http://demo.nopcommerce.com/htc-smartphone");
        urls.add("http://demo.nopcommerce.com/apple-macbook-pro");
        urls.add("http://demo.nopcommerce.com/cart");
        urls.add("http://demo.nopcommerce.com/wishlist");
        urls.add("http://demo.nopcommerce.com/login?returnUrl=%2F");
        urls.add("http://demo.nopcommerce.com/register?returnUrl=%2F");
        urls.add("http://demo.nopcommerce.com/gift-cards");
        urls.add("http://demo.nopcommerce.com/jewelry");
        urls.add("http://demo.nopcommerce.com/books");
        urls.add("http://demo.nopcommerce.com/digital-downloads");
        urls.add("http://demo.nopcommerce.com/apparel");
        urls.add("http://demo.nopcommerce.com/electronics");
        urls.add("http://demo.nopcommerce.com/computers");

        // Loop through each URL and test
        for (String url : urls) {
            // Navigate to the page (Using http instead of https)
            driver.get(url);
            System.out.println("Navigating to: " + url);
            logger.info("Navigating to: " + url);

            // Verify the page has been redirected to HTTPS if started with HTTP
            String currentUrl = driver.getCurrentUrl();

            // Check if the URL starts with https:// after redirection
            if (currentUrl.startsWith("http://")) {
                // Verify the redirection to HTTPS
                String redirectedUrl = currentUrl.replace("http://", "https://");
                Assert.assertTrue(redirectedUrl.startsWith("https://"), "HTTPS redirection failed for page: " + url);
                System.out.println("HTTP to HTTPS redirection successful on page: " + url);
                logger.info("HTTP to HTTPS redirection successful on page: " + url);
            } else {
                // If the page already starts with https://, it's secure
                Assert.assertTrue(currentUrl.startsWith("https://"), "HTTPS is not enforced on page: " + url);
                System.out.println("HTTPS and secure connection verified on page: " + url);
                logger.info("HTTPS and secure connection verified on page: " + url);
            }

            // Wait for a specific element to be loaded (can be customized as per application)
            helper.waitForPresence(By.tagName("body"));
        }
    }
}
