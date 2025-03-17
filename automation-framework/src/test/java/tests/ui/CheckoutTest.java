package tests.ui;

import base.BaseTest;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.List;
import java.util.Random;

public class CheckoutTest extends BaseTest {
    String cartUrl = "https://demo.nopcommerce.com/cart";
    String loginUrl = "https://demo.nopcommerce.com/login";

    @Test
    public void verifyCheckoutPageLoads() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        String productUrl = "https://demo.nopcommerce.com/adobe-photoshop";

        // Step 1: Navigate to the product page
        logger.info("Navigating to product page: " + productUrl);
        driver.get(productUrl);

        // Step 2: Add product to cart
        Thread.sleep(1000);
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-10")));
        addToCartButton.click();
        logger.info("Clicked 'Add to Cart' button.");

        // Step 3: Wait for cart success message
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bar-notification.success")));
        Assert.assertTrue(successMessage.isDisplayed(), "Product was not added to the cart!");
        logger.info("Product successfully added to cart.");

        // Step 4: Wait for cart quantity to update
        WebElement cartQty = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-qty")));
        String qtyText = cartQty.getText().replaceAll("[^0-9]", ""); // Extract numeric value
        Assert.assertTrue(Integer.parseInt(qtyText) > 0, "Cart quantity did not update.");
        logger.info("Cart quantity updated to: " + qtyText);

        // Step 5: Go to cart page
        driver.get(cartUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".table-wrapper")));
        logger.info("Cart page loaded successfully.");

        // Step 6: Accept terms of service
        WebElement termsCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("termsofservice")));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }
        logger.info("Accepted terms of service.");

        // Step 7: Click "Checkout" button
        WebElement checkoutButton = driver.findElement(By.id("checkout"));
        checkoutButton.click();
        logger.info("Clicked on Checkout button.");

        // Step 8: Verify checkout page loads
        wait.until(ExpectedConditions.urlContains("/checkout"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/checkout"), "Checkout page did not load.");
        logger.info("Checkout page loaded successfully.");
    }

    @Test
    public void testCheckoutProcessAsGuest() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        String productUrl = "https://demo.nopcommerce.com/adobe-photoshop";

        // Step 1: Navigate to the product page
        logger.info("Navigating to product page: " + productUrl);
        driver.get(productUrl);

        // Step 2: Add product to cart
        Thread.sleep(1000);
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-10")));
        addToCartButton.click();
        logger.info("Clicked 'Add to Cart' button.");

        // Step 3: Wait for cart success message
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bar-notification.success")));
        Assert.assertTrue(successMessage.isDisplayed(), "Product was not added to the cart!");
        logger.info("Product successfully added to cart.");

        // Step 4: Wait for cart quantity to update
        WebElement cartQty = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-qty")));
        String qtyText = cartQty.getText().replaceAll("[^0-9]", ""); // Extract numeric value
        Assert.assertTrue(Integer.parseInt(qtyText) > 0, "Cart quantity did not update.");
        logger.info("Cart quantity updated to: " + qtyText);

        // Step 5: Go to cart page
        driver.get(cartUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".table-wrapper")));
        logger.info("Cart page loaded successfully.");

        // Step 6: Accept terms and proceed to checkout
        WebElement termsCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("termsofservice")));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }
        logger.info("Accepted terms of service.");
        driver.findElement(By.id("checkout")).click();

        // Step 7: Select "Checkout as Guest"
        WebElement guestCheckoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".checkout-as-guest-button")));
        guestCheckoutButton.click();
        logger.info("Selected 'Checkout as Guest' option.");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("BillingNewAddress_FirstName")));
        logger.info("Checkout page loaded successfully.");
        Thread.sleep(1000);

        // Fill billing details
        driver.findElement(By.id("BillingNewAddress_FirstName")).sendKeys("Maegan");
        driver.findElement(By.id("BillingNewAddress_LastName")).sendKeys("Rolfson");
        driver.findElement(By.id("BillingNewAddress_Email")).sendKeys("385d163bdd@emaily.pro");

        // Select country
        Select countryDropdown = new Select(driver.findElement(By.id("BillingNewAddress_CountryId")));
        countryDropdown.selectByVisibleText("United States of America");

        // Select the state
        Thread.sleep(1000);
        Select stateDropdown = new Select(driver.findElement(By.id("BillingNewAddress_StateProvinceId")));
        stateDropdown.selectByVisibleText("Alaska");

        // Fill address details
        driver.findElement(By.id("BillingNewAddress_City")).sendKeys("Anchorage");
        driver.findElement(By.id("BillingNewAddress_Address1")).sendKeys("PO Box 242941");
        driver.findElement(By.id("BillingNewAddress_ZipPostalCode")).sendKeys("99524");
        driver.findElement(By.id("BillingNewAddress_PhoneNumber")).sendKeys("9072430988");

        // Select "Ship to the same address" checkbox
        WebElement shipToSameAddressCheckbox = driver.findElement(By.id("ShipToSameAddress"));
        if (!shipToSameAddressCheckbox.isSelected()) {
            shipToSameAddressCheckbox.click();
        }
        logger.info("Checked 'Ship to the same address' option.");

        // Click continue
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".new-address-next-step-button")));
        continueButton.click();

        // Step 9: Choose shipping method
        wait.until(ExpectedConditions.elementToBeClickable(By.id("shippingoption_1"))).click();
        driver.findElement(By.cssSelector("#shipping-method-buttons-container button")).click();
        logger.info("Shipping method selected.");

        // Step 10: Select payment method
        wait.until(ExpectedConditions.elementToBeClickable(By.id("paymentmethod_1"))).click();
        driver.findElement(By.cssSelector("#payment-method-buttons-container button")).click();
        logger.info("Payment method selected.");

        // Step 11: Enter payment details
        driver.findElement(By.id("CardholderName")).sendKeys("Maegan Rolfson");
        driver.findElement(By.id("CardNumber")).sendKeys("3595972973139459");

        Select expMonth = new Select(driver.findElement(By.id("ExpireMonth")));
        expMonth.selectByVisibleText("10");

        Select expYear = new Select(driver.findElement(By.id("ExpireYear")));
        expYear.selectByVisibleText("2029");

        driver.findElement(By.id("CardCode")).sendKeys("469");

        driver.findElement(By.cssSelector("#payment-info-buttons-container button")).click();
        logger.info("Payment details entered.");

        // Step 12: Confirm order
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#confirm-order-buttons-container button"))).click();
        logger.info("Order confirmed.");

        // Step 13: Validate order success
        WebElement orderSuccessMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".section.order-completed")));
        Assert.assertTrue(orderSuccessMessage.getText().contains("Your order has been successfully processed!"), "Order not successful!");
        logger.info("Order placed successfully!");
    }

    @Test
    public void testCheckoutProcessAsUser() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Navigate to the login page
        driver.get(loginUrl);
        logger.info("Navigating to login page: " + loginUrl);

        // Step 2: Enter login credentials
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Email"))).sendKeys("7a6d6efc3d@emaily.pro");
        driver.findElement(By.id("Password")).sendKeys("TestPassword123");

        // Step 3: Click login button
        driver.findElement(By.cssSelector("button.login-button")).click();
        logger.info("User logged in successfully.");

        // Step 4: Verify successful login
        Thread.sleep(2000);
        boolean isLoggedIn = driver.findElements(By.className("ico-logout")).size() > 0;
        Assert.assertTrue(isLoggedIn, "Login failed!");
        logger.info("Login verified, proceeding with checkout process.");

        // Step 5: Navigate to the product page
        String productUrl = "https://demo.nopcommerce.com/adobe-photoshop";
        driver.get(productUrl);
        logger.info("Navigating to product page: " + productUrl);


        // Step 6: Add product to cart
        Thread.sleep(1000);
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-10")));
        addToCartButton.click();
        logger.info("Clicked 'Add to Cart' button.");

        // Step 7: Wait for cart success message
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bar-notification.success")));
        Assert.assertTrue(successMessage.isDisplayed(), "Product was not added to the cart!");
        logger.info("Product successfully added to cart.");

        // Step 8: Wait for cart quantity to update
        WebElement cartQty = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-qty")));
        String qtyText = cartQty.getText().replaceAll("[^0-9]", ""); // Extract numeric value
        Assert.assertTrue(Integer.parseInt(qtyText) > 0, "Cart quantity did not update.");
        logger.info("Cart quantity updated to: " + qtyText);

        // Step 8: Go to cart page
        driver.get(cartUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".table-wrapper")));
        logger.info("Cart page loaded successfully.");

        // Step 9: Accept terms and proceed to checkout
        WebElement termsCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("termsofservice")));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }
        logger.info("Accepted terms of service.");
        driver.findElement(By.id("checkout")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("BillingNewAddress_FirstName")));
        logger.info("Checkout page loaded successfully.");
        Thread.sleep(1000);

        // Fill billing details
        driver.findElement(By.id("BillingNewAddress_FirstName")).sendKeys("Maegan");
        driver.findElement(By.id("BillingNewAddress_LastName")).sendKeys("Rolfson");

        // Select country
        Select countryDropdown = new Select(driver.findElement(By.id("BillingNewAddress_CountryId")));
        countryDropdown.selectByVisibleText("United States of America");

        // Select the state
        Thread.sleep(1000);
        Select stateDropdown = new Select(driver.findElement(By.id("BillingNewAddress_StateProvinceId")));
        stateDropdown.selectByVisibleText("Alaska");

        // Fill address details
        driver.findElement(By.id("BillingNewAddress_City")).sendKeys("Anchorage");
        driver.findElement(By.id("BillingNewAddress_Address1")).sendKeys("PO Box 242941");
        driver.findElement(By.id("BillingNewAddress_ZipPostalCode")).sendKeys("99524");
        driver.findElement(By.id("BillingNewAddress_PhoneNumber")).sendKeys("9072430988");

        // Select "Ship to the same address" checkbox
        WebElement shipToSameAddressCheckbox = driver.findElement(By.id("ShipToSameAddress"));
        if (!shipToSameAddressCheckbox.isSelected()) {
            shipToSameAddressCheckbox.click();
        }
        logger.info("Checked 'Ship to the same address' option.");

        // Click continue
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".new-address-next-step-button")));
        continueButton.click();

        // Step 11: Choose shipping method
        wait.until(ExpectedConditions.elementToBeClickable(By.id("shippingoption_1"))).click();
        driver.findElement(By.cssSelector("#shipping-method-buttons-container button")).click();
        logger.info("Shipping method selected.");

        // Step 12: Select payment method
        wait.until(ExpectedConditions.elementToBeClickable(By.id("paymentmethod_1"))).click();
        driver.findElement(By.cssSelector("#payment-method-buttons-container button")).click();
        logger.info("Payment method selected.");

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
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#confirm-order-buttons-container button"))).click();
        logger.info("Order confirmed.");

        // Step 15: Validate order success
        WebElement orderSuccessMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".section.order-completed")));
        Assert.assertTrue(orderSuccessMessage.getText().contains("Your order has been successfully processed!"), "Order not successful!");
        logger.info("Order placed successfully!");
    }

    @Test
    public void verifyInvalidAddressPreventsCheckout() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        String productUrl = "https://demo.nopcommerce.com/adobe-photoshop";

        // Step 1: Navigate to the product page
        logger.info("Navigating to product page: " + productUrl);
        driver.get(productUrl);

        // Step 2: Add product to cart
        Thread.sleep(1000);
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-10")));
        addToCartButton.click();
        logger.info("Clicked 'Add to Cart' button.");

        // Step 3: Wait for cart success message
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bar-notification.success")));
        Assert.assertTrue(successMessage.isDisplayed(), "Product was not added to the cart!");
        logger.info("Product successfully added to cart.");

        // Step 4: Wait for cart quantity to update
        WebElement cartQty = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-qty")));
        String qtyText = cartQty.getText().replaceAll("[^0-9]", ""); // Extract numeric value
        Assert.assertTrue(Integer.parseInt(qtyText) > 0, "Cart quantity did not update.");
        logger.info("Cart quantity updated to: " + qtyText);

        // Step 5: Go to cart page
        driver.get(cartUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".table-wrapper")));
        logger.info("Cart page loaded successfully.");

        // Step 6: Accept terms and proceed to checkout
        WebElement termsCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("termsofservice")));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }
        logger.info("Accepted terms of service.");
        driver.findElement(By.id("checkout")).click();

        // Step 7: Select "Checkout as Guest"
        WebElement guestCheckoutButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".checkout-as-guest-button")));
        guestCheckoutButton.click();
        logger.info("Selected 'Checkout as Guest' option.");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("BillingNewAddress_FirstName")));
        logger.info("Checkout page loaded successfully.");
        Thread.sleep(1000);

        // Step 6: Enter incorrect billing details
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("BillingNewAddress_FirstName"))).sendKeys("John");
        driver.findElement(By.id("BillingNewAddress_LastName")).sendKeys("Doe");

        // Select an invalid country (or skip selection)
        Select countryDropdown = new Select(driver.findElement(By.id("BillingNewAddress_CountryId")));
        countryDropdown.selectByVisibleText("Select country");

        // Enter incomplete address
        driver.findElement(By.id("BillingNewAddress_City")).sendKeys("");
        driver.findElement(By.id("BillingNewAddress_Address1")).sendKeys("");
        driver.findElement(By.id("BillingNewAddress_ZipPostalCode")).sendKeys("");
        driver.findElement(By.id("BillingNewAddress_PhoneNumber")).sendKeys("123");

        // Click continue
        WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".new-address-next-step-button")));
        continueButton.click();

        try {
            // Wait for alert to appear
            WebDriverWait waitAlert = new WebDriverWait(driver, Duration.ofSeconds(5));
            waitAlert.until(ExpectedConditions.alertIsPresent());

            // Switch to alert
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            logger.info("Validation alert found: " + alertText);

            // Assert that the expected error message is present in the alert
            Assert.assertTrue(alertText.contains("City is required") &&
                            alertText.contains("Email is required") &&
                            alertText.contains("Street address is required") &&
                            alertText.contains("Zip / postal code is required"),
                    "Expected validation error message was not displayed!");

            // Accept the alert
            alert.accept();

            // If we reach here, the test should pass
            logger.info("Test passed: Validation alert appeared as expected.");

        } catch (Exception e) {
            logger.error("Test failed: Expected validation alert did not appear.");
            Assert.fail("Expected validation alert did not appear.");
        }


    }

    @Test
    public void verifyOrderIsGenerated() throws InterruptedException {
        String productUrl = "https://demo.nopcommerce.com/adobe-photoshop";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Navigate to login page and log in
        logger.info("Navigating to login page: " + loginUrl);
        driver.get(loginUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Email"))).sendKeys("7a6d6efc3d@emaily.pro");
        driver.findElement(By.id("Password")).sendKeys("TestPassword123");
        driver.findElement(By.cssSelector("button.login-button")).click();

        // Verify login
        boolean isLoggedIn = driver.findElements(By.className("ico-logout")).size() > 0;
        Assert.assertTrue(isLoggedIn, "Login failed!");
        logger.info("User logged in successfully.");

        // Step 2: Navigate to product page
        logger.info("Navigating to product page: " + productUrl);
        driver.get(productUrl);

        // Step 3: Add product to cart
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-10")));
        addToCartButton.click();
        logger.info("Clicked 'Add to Cart' button.");

        // Verify product added success message
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bar-notification.success")));
        Assert.assertTrue(successMessage.isDisplayed(), "Product was not added to the cart!");
        logger.info("Product successfully added to cart.");

        // Step 4: Navigate to cart page
        logger.info("Navigating to cart page: " + cartUrl);
        driver.get(cartUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".table-wrapper")));
        logger.info("Cart page loaded successfully.");

        // Step 5: Accept terms and proceed to checkout
        WebElement termsCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("termsofservice")));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }
        logger.info("Accepted terms of service.");
        driver.findElement(By.id("checkout")).click();

        // Step 6: Select shipping method
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".new-address-next-step-button"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("shippingoption_1"))).click();
        driver.findElement(By.cssSelector("#shipping-method-buttons-container button")).click();
        logger.info("Shipping method selected.");

        // Step 7: Select payment method
        wait.until(ExpectedConditions.elementToBeClickable(By.id("paymentmethod_1"))).click();
        driver.findElement(By.cssSelector("#payment-method-buttons-container button")).click();
        logger.info("Payment method selected.");

        // Step 8: Enter payment details
        driver.findElement(By.id("CardholderName")).sendKeys("John Doe");
        driver.findElement(By.id("CardNumber")).sendKeys("3595972973139459");
        new Select(driver.findElement(By.id("ExpireMonth"))).selectByVisibleText("10");
        new Select(driver.findElement(By.id("ExpireYear"))).selectByVisibleText("2029");
        driver.findElement(By.id("CardCode")).sendKeys("469");
        driver.findElement(By.cssSelector("#payment-info-buttons-container button")).click();
        logger.info("Payment details entered successfully.");

        // Step 9: Confirm order
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#confirm-order-buttons-container button"))).click();
        logger.info("Order confirmation button clicked.");

        // Step 10: Verify order success message
        WebElement orderSuccessMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".section.order-completed")));
        Assert.assertTrue(orderSuccessMessage.getText().contains("Your order has been successfully processed!"), "Order not successful!");
        logger.info("Order placed successfully.");

        // Step 11: Verify order number is displayed
        WebElement orderNumberElement = driver.findElement(By.cssSelector(".order-number strong"));
        String orderNumber = orderNumberElement.getText().replaceAll("[^0-9]", ""); // Extract order number
        Assert.assertFalse(orderNumber.isEmpty(), "Order number is missing!");
        logger.info("Order number generated: " + orderNumber);

        System.out.println("Order placed successfully. Order Number: " + orderNumber);
    }

    @Test
    public void verifyOrderSummaryDisplaysCorrectInfo() throws InterruptedException {
        String productUrl = "https://demo.nopcommerce.com/apple-iphone-16-128gb";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Navigate to login page and log in
        logger.info("Navigating to login page: " + loginUrl);
        driver.get(loginUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Email"))).sendKeys("7a6d6efc3d@emaily.pro");
        driver.findElement(By.id("Password")).sendKeys("TestPassword123");
        driver.findElement(By.cssSelector("button.login-button")).click();

        // Verify login
        boolean isLoggedIn = driver.findElements(By.className("ico-logout")).size() > 0;
        Assert.assertTrue(isLoggedIn, "Login failed!");
        logger.info("User logged in successfully.");

        // Step 2: Ensure cart is empty before adding a product
        logger.info("Navigating to cart page to check if it's empty: " + cartUrl);
        driver.get(cartUrl);

        // Wait until cart page loads
        WebElement cartContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".order-summary-content")));

        // Check if the "Your Shopping Cart is empty!" message is displayed
        List<WebElement> emptyCartMessage = driver.findElements(By.cssSelector(".no-data"));
        if (!emptyCartMessage.isEmpty() && emptyCartMessage.get(0).isDisplayed()) {
            logger.info("Cart is already empty. Skipping removal.");
        } else {
            logger.info("Cart is not empty. Removing all items...");

            List<WebElement> removeButtons = driver.findElements(By.cssSelector("button.remove-btn"));
            for (WebElement removeButton : removeButtons) {
                if (removeButton.isDisplayed() && removeButton.isEnabled()) {
                    removeButton.click();
                    wait.until(ExpectedConditions.stalenessOf(removeButton)); // Ensure item is removed
                }
            }

            // Wait for "Your Shopping Cart is empty!" message to appear
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".no-data")));
        }

        // Final verification
        Assert.assertTrue(driver.findElement(By.cssSelector(".no-data")).isDisplayed(), "Cart is not empty after attempting to clear it!");
        logger.info("Cart is now empty.");


        // Step 3: Navigate to product page
        logger.info("Navigating to product page: " + productUrl);
        driver.get(productUrl);

        // Step 4: Add product to cart
        Thread.sleep(1000);
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-21")));
        addToCartButton.click();
        logger.info("Clicked 'Add to Cart' button.");

        // Verify product added success message
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bar-notification.success")));
        Assert.assertTrue(successMessage.isDisplayed(), "Product was not added to the cart!");
        logger.info("Product successfully added to cart.");

        // Step 5: Navigate to cart page
        logger.info("Navigating to cart page: " + cartUrl);
        driver.get(cartUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".table-wrapper")));
        logger.info("Cart page loaded successfully.");

        // Step 6: Accept terms and proceed to checkout
        WebElement termsCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("termsofservice")));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }
        logger.info("Accepted terms of service.");
        driver.findElement(By.id("checkout")).click();

        // Step 7: Select shipping method
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".new-address-next-step-button"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("shippingoption_1"))).click();
        driver.findElement(By.cssSelector("#shipping-method-buttons-container button")).click();
        logger.info("Shipping method selected.");

        // Step 8: Select payment method
        wait.until(ExpectedConditions.elementToBeClickable(By.id("paymentmethod_1"))).click();
        driver.findElement(By.cssSelector("#payment-method-buttons-container button")).click();
        logger.info("Payment method selected.");

        // Step 9: Enter payment details
        driver.findElement(By.id("CardholderName")).sendKeys("John Doe");
        driver.findElement(By.id("CardNumber")).sendKeys("3595972973139459");
        new Select(driver.findElement(By.id("ExpireMonth"))).selectByVisibleText("10");
        new Select(driver.findElement(By.id("ExpireYear"))).selectByVisibleText("2029");
        driver.findElement(By.id("CardCode")).sendKeys("469");
        driver.findElement(By.cssSelector("#payment-info-buttons-container button")).click();
        logger.info("Payment details entered successfully.");

        // Step 10: Verify order summary on checkout page
        WebElement orderSummarySection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".order-summary-content")));
        Assert.assertTrue(orderSummarySection.isDisplayed(), "Order summary section is not visible on checkout page.");
        logger.info("Order summary section is displayed.");

        // Step 11: Verify product details in order summary
        WebElement productName = driver.findElement(By.cssSelector(".product-name"));
        String productNameText = productName.getText();
        Assert.assertEquals(productNameText, "Apple iPhone 16 128GB", "Product name mismatch in order summary.");
        logger.info("Product name matches in order summary: " + productNameText);

        WebElement productPrice = driver.findElement(By.cssSelector(".product-unit-price"));
        String productPriceText = productPrice.getText();
        Assert.assertEquals(productPriceText, "$799.00", "Product price mismatch in order summary.");
        logger.info("Product price matches in order summary: " + productPriceText);

        WebElement productQuantity = driver.findElement(By.cssSelector(".product-quantity"));
        String productQuantityText = productQuantity.getText();
        Assert.assertEquals(productQuantityText, "1", "Product quantity mismatch in order summary.");
        logger.info("Product quantity matches in order summary: " + productQuantityText);

        WebElement orderSubtotal = driver.findElement(By.cssSelector(".order-subtotal .value-summary"));
        String subtotalText = orderSubtotal.getText();
        Assert.assertEquals(subtotalText, "$799.00", "Order subtotal mismatch in order summary.");
        logger.info("Order subtotal matches in order summary: " + subtotalText);

        WebElement shippingCost = driver.findElement(By.cssSelector(".shipping-cost .value-summary"));
        String shippingCostText = shippingCost.getText();
        Assert.assertEquals(shippingCostText, "$0.00", "Shipping cost mismatch in order summary.");
        logger.info("Shipping cost matches in order summary: " + shippingCostText);

        WebElement orderTotal = driver.findElement(By.cssSelector(".order-total .value-summary"));
        String orderTotalText = orderTotal.getText();
        Assert.assertEquals(orderTotalText, "$799.00", "Order total mismatch in order summary.");
        logger.info("Order total matches in order summary: " + orderTotalText);
    }

    @Test
    public void verifyDifferentPaymentOptionsAreAvailable() throws InterruptedException {
        String productUrl = "https://demo.nopcommerce.com/adobe-photoshop";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Navigate to login page and log in
        logger.info("Navigating to login page: " + loginUrl);
        driver.get(loginUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Email"))).sendKeys("7a6d6efc3d@emaily.pro");
        driver.findElement(By.id("Password")).sendKeys("TestPassword123");
        driver.findElement(By.cssSelector("button.login-button")).click();

        // Verify login
        boolean isLoggedIn = driver.findElements(By.className("ico-logout")).size() > 0;
        Assert.assertTrue(isLoggedIn, "Login failed!");
        logger.info("User logged in successfully.");

        // Step 2: Navigate to product page
        logger.info("Navigating to product page: " + productUrl);
        driver.get(productUrl);

        // Step 3: Add product to cart
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-10")));
        addToCartButton.click();
        logger.info("Clicked 'Add to Cart' button.");

        // Verify product added success message
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bar-notification.success")));
        Assert.assertTrue(successMessage.isDisplayed(), "Product was not added to the cart!");
        logger.info("Product successfully added to cart.");

        // Step 4: Navigate to cart page
        logger.info("Navigating to cart page: " + cartUrl);
        driver.get(cartUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".table-wrapper")));
        logger.info("Cart page loaded successfully.");

        // Step 5: Accept terms and proceed to checkout
        WebElement termsCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("termsofservice")));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }
        logger.info("Accepted terms of service.");
        driver.findElement(By.id("checkout")).click();

        // Step 6: Select shipping method
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".new-address-next-step-button"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("shippingoption_1"))).click();
        driver.findElement(By.cssSelector("#shipping-method-buttons-container button")).click();
        logger.info("Shipping method selected.");

        // Ensure the payment section is fully loaded
        WebElement paymentSection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("payment-method-block")));
        logger.info("Payment section loaded successfully.");

        // Give the page some extra time before checking for radio buttons
        Thread.sleep(2000);  // Temporary debugging step, remove once stable

        // Ensure radio buttons are present
        List<WebElement> paymentOptions = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("input[type='radio']")));
        Assert.assertFalse(paymentOptions.isEmpty(), "No payment options found.");
        logger.info("Payment options are listed.");

        // Scroll into view if necessary
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", paymentOptions.get(0));

        // Verify specific payment options exist
        boolean isCheckMoneyOrderVisible = false;
        boolean isCreditCardVisible = false;

        for (WebElement radio : paymentOptions) {
            String labelText = radio.findElement(By.xpath("following-sibling::label")).getText();
            if (labelText.contains("Check / Money Order")) {
                isCheckMoneyOrderVisible = true;
            } else if (labelText.contains("Credit Card")) {
                isCreditCardVisible = true;
            }
        }

        // Assert both options are found
        Assert.assertTrue(isCheckMoneyOrderVisible, "'Check / Money Order' option is not visible.");
        Assert.assertTrue(isCreditCardVisible, "'Credit Card' option is not visible.");

        logger.info("Both 'Check / Money Order' and 'Credit Card' options are visible.");
    }

    // Random delay function
    public void realisticDelay() throws InterruptedException {
        Random random = new Random();
        int delay = random.nextInt(2000) + 1000; // Delay between 1000ms and 3000ms
        Thread.sleep(delay);
    }
}
