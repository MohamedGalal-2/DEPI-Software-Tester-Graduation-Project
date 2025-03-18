package tests.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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
import java.util.ArrayList;
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
        SeleniumHelper.realisticDelay();
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
        SeleniumHelper.realisticDelay();
        WebElement termsCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("termsofservice")));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }
        System.out.println("Accepted terms of service.");
        SeleniumHelper.realisticDelay();
        driver.findElement(By.id("checkout")).click();

        logger.info("Checkout page loaded successfully.");
        SeleniumHelper.realisticDelay();

        // Step 10: Click continue
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".new-address-next-step-button")));
        continueButton.click();

        // Step 11: Choose shipping method
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("shippingoption_1"))).click();
        driver.findElement(By.cssSelector("#shipping-method-buttons-container button")).click();
        System.out.println("Shipping method selected.");

        // Step 12: Select payment method
        SeleniumHelper.realisticDelay();
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

        SeleniumHelper.realisticDelay();
        driver.findElement(By.cssSelector("#payment-info-buttons-container button")).click();
        System.out.println("Payment details entered.");

        // Step 14: Validate error messages
        WebElement errorMessages = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message-error.validation-summary-errors")));
        Assert.assertTrue(errorMessages.getText().contains("Wrong card number"), "Error message not displayed for invalid card number.");
        Assert.assertTrue(errorMessages.getText().contains("Card is expired"), "Error message not displayed for invalid card code.");
        logger.info("Error message displayed for invalid payment details.");
    }

    @Test(groups = {"functional"}, description = "TC-082")
    public void verifyPaymentViaCreditCard() throws InterruptedException {
        logger.info("Navigating to: " + loginUrl);
        driver.get(loginUrl);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 2: Enter login credentials
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Email"))).sendKeys("7a6d6efc3d@emaily.pro");
        driver.findElement(By.id("Password")).sendKeys("TestPassword123");

        // Step 3: Click login button
        SeleniumHelper.realisticDelay();
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
        SeleniumHelper.realisticDelay();
        WebElement termsCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("termsofservice")));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }
        System.out.println("Accepted terms of service.");
        SeleniumHelper.realisticDelay();
        driver.findElement(By.id("checkout")).click();

        logger.info("Checkout page loaded successfully.");
        SeleniumHelper.realisticDelay();

        // Step 10: Click continue
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".new-address-next-step-button")));
        continueButton.click();

        // Step 11: Choose shipping method
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("shippingoption_1"))).click();
        driver.findElement(By.cssSelector("#shipping-method-buttons-container button")).click();
        System.out.println("Shipping method selected.");

        // Step 12: Select payment method
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("paymentmethod_1"))).click();
        driver.findElement(By.cssSelector("#payment-method-buttons-container button")).click();
        System.out.println("Payment method selected.");

        // Step 13: Enter payment details
        driver.findElement(By.id("CardholderName")).sendKeys("Maegan Rolfson");
        driver.findElement(By.id("CardNumber")).sendKeys("3595972973139459");

        Select expMonth = new Select(driver.findElement(By.id("ExpireMonth")));
        expMonth.selectByVisibleText("10");

        Select expYear = new Select(driver.findElement(By.id("ExpireYear")));
        expYear.selectByVisibleText("2029");

        driver.findElement(By.id("CardCode")).sendKeys("469");

        driver.findElement(By.cssSelector("#payment-info-buttons-container button")).click();
        logger.info("Payment details entered.");

        // Step 14: Confirm order
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#confirm-order-buttons-container button"))).click();
        logger.info("Order confirmed.");

        // Step 15: Validate order success
        WebElement orderSuccessMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".section.order-completed")));
        Assert.assertTrue(orderSuccessMessage.getText().contains("Your order has been successfully processed!"), "Order not successful!");
        logger.info("Order successfully placed. Payment via Credit Card successful!");
    }

    @Test(groups = {"functional"}, description = "TC-083_TC-084")
    public void verifyAvailablePaymentMethods() throws InterruptedException {
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
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("shippingoption_1"))).click();
        driver.findElement(By.cssSelector("#shipping-method-buttons-container button")).click();
        System.out.println("Shipping method selected.");

        // Step 12: Identify available payment methods
        System.out.println("Checking available payment methods...");

        SeleniumHelper.realisticDelay();
        List<WebElement> paymentMethods = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("#payment-method-block input[type='radio']")));

        // Wait until all the elements are visible
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfAllElements(paymentMethods));

        boolean isPayPalAvailable = false;
        boolean isCODAvailable = false;
        boolean isCreditCardAvailable = false;
        List<String> availableMethods = new ArrayList<>();

        // Check available payment methods
        for (WebElement paymentMethod : paymentMethods) {
            String paymentMethodLabel = paymentMethod.getAttribute("value");
            if (paymentMethodLabel.contains("PayPal")) {
                isPayPalAvailable = true;
                availableMethods.add("PayPal");
                System.out.println("PayPal payment method is available.");
            } else if (paymentMethodLabel.contains("Cash On Delivery")) {
                isCODAvailable = true;
                availableMethods.add("Cash On Delivery (COD)");
                System.out.println("Cash On Delivery (COD) payment method is available.");
            } else if (paymentMethodLabel.contains("Credit Card")) {
                isCreditCardAvailable = true;
                availableMethods.add("Credit Card");
                System.out.println("Credit Card payment method is available.");
            }
        }

        // If neither PayPal nor COD is available, fail the test and output available methods
        if (!isPayPalAvailable && !isCODAvailable) {
            String errorMessage = "Neither PayPal nor Cash On Delivery (COD) payment methods are available! Available methods: " + String.join(", ", availableMethods);
            logger.error(errorMessage);
            Assert.fail(errorMessage);
        } else {
            // Validate the presence of PayPal and COD methods
            Assert.assertTrue(isPayPalAvailable, "PayPal payment method is not available!");
            Assert.assertTrue(isCODAvailable, "Cash On Delivery payment method is not available!");
            Assert.assertTrue(isCreditCardAvailable, "Credit Card payment method is not available!");
            logger.info("Payment methods validation completed.");
        }

        // Step 13: Proceed with the payment method if PayPal or COD is available
        if (isPayPalAvailable) {
            WebElement paypalRadioButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[value='PayPal']")));
            paypalRadioButton.click();
            System.out.println("PayPal payment method selected.");
        } else if (isCODAvailable) {
            WebElement codRadioButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[value='Cash On Delivery']")));
            codRadioButton.click();
            System.out.println("Cash On Delivery (COD) payment method selected.");
        } else {
            WebElement creditCardRadioButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[value='Credit Card']")));
            creditCardRadioButton.click();
            System.out.println("Credit Card payment method selected.");
        }

        // Step 14: Confirm order
        driver.findElement(By.cssSelector("#confirm-order-buttons-container button")).click();
        logger.info("Order confirmed.");

        // Step 15: Validate order success
        WebElement orderSuccessMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".section.order-completed")));
        Assert.assertTrue(orderSuccessMessage.getText().contains("Your order has been successfully processed!"), "Order not successful!");
        logger.info("Order successfully placed. Payment method successfully selected.");
    }

    @Test(groups = {"security"}, description = "TC-088")
    public void verifyDuplicatePaymentPrevention() throws InterruptedException {
        logger.info("Navigating to: " + loginUrl);
        driver.get(loginUrl);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 2: Enter login credentials
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Email"))).sendKeys("7a6d6efc3d@emaily.pro");
        driver.findElement(By.id("Password")).sendKeys("TestPassword123");

        // Step 3: Click login button
        SeleniumHelper.realisticDelay();
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
        SeleniumHelper.realisticDelay();
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bar-notification.success")));
        Assert.assertTrue(successMessage.isDisplayed(), "Product was not added to the cart!");
        logger.info("Product successfully added to cart.");

        // Step 8: Go to cart page
        driver.get(cartUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".table-wrapper")));
        System.out.println("Cart page loaded successfully.");

        // Step 9: Accept terms and proceed to checkout
        SeleniumHelper.realisticDelay();
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
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("shippingoption_1"))).click();
        driver.findElement(By.cssSelector("#shipping-method-buttons-container button")).click();
        System.out.println("Shipping method selected.");

        // Step 12: Select payment method
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("paymentmethod_1"))).click();
        driver.findElement(By.cssSelector("#payment-method-buttons-container button")).click();
        System.out.println("Payment method selected.");

        // Step 13: Enter payment details
        driver.findElement(By.id("CardholderName")).sendKeys("Maegan Rolfson");
        driver.findElement(By.id("CardNumber")).sendKeys("3595972973139459");

        Select expMonth = new Select(driver.findElement(By.id("ExpireMonth")));
        expMonth.selectByVisibleText("10");

        Select expYear = new Select(driver.findElement(By.id("ExpireYear")));
        expYear.selectByVisibleText("2029");

        driver.findElement(By.id("CardCode")).sendKeys("469");

        driver.findElement(By.cssSelector("#payment-info-buttons-container button")).click();
        logger.info("Payment details entered.");

        // Step 14: Intercept payment (simulate duplicate payment attempt)
        try {
            // Attempt to submit the payment again (simulate duplicate payment button click)
            WebElement duplicatePaymentButton = driver.findElement(By.cssSelector("#payment-info-buttons-container button"));
            duplicatePaymentButton.click(); // This is a duplicate payment request

            // If the system allows the second payment request, this line will fail the test
            Assert.fail("Duplicate payment request should have been blocked!");
        } catch (Exception e) {
            // Expected behavior: The duplicate payment should not be allowed, so the exception will be caught
            logger.info("Duplicate payment request prevented successfully.");
        }

        // Step 15: Confirm the order and verify that only one payment was processed
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#confirm-order-buttons-container button"))).click();
        WebElement confirmationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".section.order-completed")));
        Assert.assertTrue(confirmationMessage.getText().contains("Your order has been successfully processed!"), "Order not successful!");
    }

    @Test(groups = {"functional"}, description = "TC-095")
    public void verifyPaymentFailsWithInsufficientFunds() throws InterruptedException {
        logger.info("Navigating to: " + loginUrl);
        driver.get(loginUrl);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 2: Enter login credentials
        SeleniumHelper.realisticDelay();
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
        SeleniumHelper.realisticDelay();
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bar-notification.success")));
        Assert.assertTrue(successMessage.isDisplayed(), "Product was not added to the cart!");
        logger.info("Product successfully added to cart.");

        // Step 8: Go to cart page
        driver.get(cartUrl);
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".table-wrapper")));
        System.out.println("Cart page loaded successfully.");

        // Step 9: Accept terms and proceed to checkout
        SeleniumHelper.realisticDelay();
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
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("shippingoption_1"))).click();
        driver.findElement(By.cssSelector("#shipping-method-buttons-container button")).click();
        System.out.println("Shipping method selected.");

        // Step 12: Select payment method
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("paymentmethod_1"))).click();
        driver.findElement(By.cssSelector("#payment-method-buttons-container button")).click();
        System.out.println("Payment method selected.");

        // Step 13: Enter payment details
        driver.findElement(By.id("CardholderName")).sendKeys("Maegan Rolfson");
        driver.findElement(By.id("CardNumber")).sendKeys("3595972973139459");

        Select expMonth = new Select(driver.findElement(By.id("ExpireMonth")));
        expMonth.selectByVisibleText("10");

        Select expYear = new Select(driver.findElement(By.id("ExpireYear")));
        expYear.selectByVisibleText("2029");

        driver.findElement(By.id("CardCode")).sendKeys("469");

        SeleniumHelper.realisticDelay();
        driver.findElement(By.cssSelector("#payment-info-buttons-container button")).click();
        logger.info("Payment details entered.");

        // Step 14: Confirm order
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#confirm-order-buttons-container button"))).click();
        logger.info("Order confirmed.");

        // Step 14: Attempt to confirm order
        try {
            SeleniumHelper.realisticDelay();
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message-error")));
            Assert.assertTrue(errorMessage.getText().contains("insufficient funds"), "Expected 'insufficient funds' error message was not displayed.");
            logger.info("Transaction correctly declined due to insufficient funds.");
        } catch (TimeoutException e) {
            // If no error message appears and order is completed, fail the test
            logger.error("Transaction was completed despite insufficient funds. Test failed!");
            Assert.fail("Transaction should have been declined due to insufficient funds, but it was processed successfully.");
        }
    }


}