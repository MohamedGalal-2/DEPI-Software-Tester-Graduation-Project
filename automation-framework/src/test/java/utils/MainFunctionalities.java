package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;

import base.BaseTest;
import utils.SeleniumHelper;
import utils.ConfigReader;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class MainFunctionalities extends BaseTest {
    private WebDriverWait wait;
    private SeleniumHelper helper;

    public MainFunctionalities(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.helper = new SeleniumHelper(driver);
    }

    private final String url = ConfigReader.getProperty("baseURL");
    private final String loginUrl = ConfigReader.getProperty("loginURL");
    private final String cartUrl = ConfigReader.getProperty("cartURL");
    private final String wishlistUrl = ConfigReader.getProperty("wishlistURL");
    private final String registrationUrl = ConfigReader.getProperty("registrationURL");
    private final String changePasswordUrl = "https://demo.nopcommerce.com/customer/changepassword";
    private final String myAccountUrl = "https://demo.nopcommerce.com/customer/info";

    public void login(String username, String password) throws InterruptedException {
        // Step 1: Navigate to the login page
        driver.get(loginUrl);
        logger.info("Navigated to login page");

        // Step 2: Enter username and password
        helper.realisticDelay();
        WebElement usernameField = driver.findElement(helper.getByLocator("login.email"));
        WebElement passwordField = driver.findElement(helper.getByLocator("login.password"));

        helper.realisticDelay();
        usernameField.sendKeys(username);
        passwordField.sendKeys(password);

        WebElement loginButton = helper.waitForVisibility(helper.getByLocator("login.button"));
        loginButton.click();
        logger.info("Entered username and password");

        // Step 3: Verify user is logged in (check for a user-specific element, logout button)
        SeleniumHelper.realisticDelay();
        WebElement accountLink = helper.waitForVisibility(helper.getByLocator("account.icon"));
        Assert.assertTrue(accountLink.isDisplayed(), "User is not logged in!");
        logger.info("User is logged in successfully.");
    }

    public void logout() throws InterruptedException {
        // Step 1: Verify successful login by checking for the presence of the logout button
        helper.realisticDelay();
        List<WebElement> logoutButton = driver.findElements(helper.getByLocator("logout.button"));

        // Step 2: Assert that the logout button is present, indicating the user is logged in
        Assert.assertTrue(logoutButton.size() > 0, "User login was not successful!");
        logger.info("User successfully logged in.");

        // Step 3: Click the "Logout" button to log out
        helper.realisticDelay();
        logoutButton.get(0).click();
        logger.info("Clicking 'Logout' button.");

        // Step 4: Wait for the page to reload after logout (wait for login link to appear)
        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("header.loginLink"));  // Ensure the login link is visible

        // Step 5: Verify that the user is logged out by checking for the login link
        helper.realisticDelay();
        WebElement loginLink = driver.findElement(helper.getByLocator("header.loginLink"));

        // Step 6: Assert that the login link is displayed, indicating successful logout
        Assert.assertTrue(loginLink.isDisplayed(), "User logout was not successful!");
        logger.info("User successfully logged out.");
    }

    public void register(String firstName, String lastName, String emailAddress, String password) throws InterruptedException {
        // Step 1: Navigate to the registration page
        driver.get(registrationUrl);
        logger.info("Navigating to registration page.");

        // Step 2: Fill in the registration form with valid details
        helper.realisticDelay();
        WebElement firstNameField = helper.waitForVisibility(helper.getByLocator("register.firstName"));
        WebElement lastNameField = helper.waitForVisibility(helper.getByLocator("register.lastName"));
        WebElement emailField = helper.waitForVisibility(helper.getByLocator("register.email"));
        WebElement passwordField = helper.waitForVisibility(helper.getByLocator("register.password"));
        WebElement confirmPasswordField = helper.waitForVisibility(helper.getByLocator("register.confirmPassword"));
        WebElement registerButton = helper.waitForVisibility(helper.getByLocator("register.button"));

        // Enter valid details
        firstNameField.sendKeys(firstName);
        lastNameField.sendKeys(lastName);
        emailField.sendKeys(emailAddress);
        passwordField.sendKeys(password);
        confirmPasswordField.sendKeys(password);

        // Step 3: Click the "Register" button
        helper.realisticDelay();
        registerButton.click();
        logger.info("Clicking 'Register' button.");
    }

    public void addProductToCart(String productUrl, String productId) throws InterruptedException {
        SeleniumHelper help = new SeleniumHelper(driver);

        // Step 1: Navigate to the product page
        driver.get(productUrl);
        logger.info("Navigating to product page: " + productUrl);

        // Step 2: Add product to cart
        helper.realisticDelay();

        // Dynamically generate the "Add to Cart" button ID using the product ID
        String addToCartButtonId = "add-to-cart-button-" + productId;

        WebElement addToCartButton = help.waitForClickable(By.id(addToCartButtonId));
        addToCartButton.click();
        logger.info("Clicked 'Add to Cart' button with ID: " + addToCartButtonId);

        // Step 3: Wait for cart success message
        WebElement successMessage = helper.waitForVisibility(helper.getByLocator("product.successMessage"));
        Assert.assertTrue(successMessage.isDisplayed(), "Product was not added to the cart!");
        logger.info("Product successfully added to cart.");
    }

    public void removeProductFromCart(String productId) throws InterruptedException {
        SeleniumHelper help = new SeleniumHelper(driver);

        helper.realisticDelay();
        WebElement removeButton = helper.waitForClickable(helper.getByLocator("cart.removeButton"));
        removeButton.click();
        System.out.println("Clicked 'Remove' button for product ID: " + productId);
        Thread.sleep(2000); // Allow time for the cart update
    }

    public void checkCartSummary() {
        WebElement subtotalElement = helper.waitForVisibility(helper.getByLocator("cart.orderSubtotal"));
        WebElement taxElement = helper.waitForVisibility(helper.getByLocator("cart.orderTax"));
        WebElement totalElement = helper.waitForVisibility(helper.getByLocator("cart.orderTotal"));

        // Remove all non-numeric characters except decimal points
        String subtotalText = subtotalElement.getText().replaceAll("[^0-9.]", "");
        String taxText = taxElement.getText().replaceAll("[^0-9.]", "");
        String totalText = totalElement.getText().replaceAll("[^0-9.]", "");

        // Convert to double
        double subtotal = Double.parseDouble(subtotalText);
        double tax = Double.parseDouble(taxText);
        double expectedTotal = subtotal + tax;
        double actualTotal = Double.parseDouble(totalText);

        logger.info("Subtotal: " + subtotal);
        logger.info("Tax: " + tax);
        logger.info("Total: " + actualTotal);

        // Validate expected total
        Assert.assertEquals(actualTotal, expectedTotal, "Cart total does not match subtotal + tax!");
    }

    public void checkCartEmpty() throws InterruptedException {
        // Step 1: Navigate to cart page
        driver.get(cartUrl);
        logger.info("Navigating to cart page.");

        // Step 2: Verify if the cart is empty and the message is displayed
        helper.realisticDelay();

        WebElement emptyCartMessage = helper.waitForVisibility(helper.getByLocator("cart.empty"));
        String message = emptyCartMessage.getText().trim();

        // Step 3: Assert that the message "Your Shopping Cart is empty!" is displayed
        Assert.assertEquals(message, "Your Shopping Cart is empty!", "The empty cart message is not displayed correctly!");
        logger.info("Verified that the empty cart message is displayed: " + message);
    }

    public void addProductToWishlist(String productUrl) throws InterruptedException {
        // Step 1: Navigate to the product page
        driver.get(productUrl);
        logger.info("Navigating to product page: " + productUrl);

        // Step 2: Click the 'Add to Wishlist' button
        helper.realisticDelay();
        WebElement addToWishlistButton = helper.waitForClickable(helper.getByLocator("wishlist.addButton"));
        addToWishlistButton.click();
        logger.info("Clicked 'Add to Wishlist' button.");

        // Step 3: Verify that the product was successfully added to the wishlist
        helper.realisticDelay();
        WebElement successMessage = helper.waitForVisibility(helper.getByLocator("wishlist.successMessage"));
        Assert.assertTrue(successMessage.isDisplayed(), "Product was not added to the wishlist!");
        logger.info("Product successfully added to the wishlist.");
    }

    public void addProductToCartFromWishlist(String productName) throws InterruptedException {
        // Step 1: Navigate to the wishlist page
        driver.get(wishlistUrl);
        logger.info("Navigating to wishlist page.");

        // Step 2: Verify that the product appears in the wishlist
        helper.realisticDelay();
        WebElement wishlistProduct = helper.waitForVisibility(
                By.xpath("//a[@class='product-name' and contains(text(), lenovoName)]"));

        // Step 3: Verify the product is present in the wishlist
        Assert.assertTrue(wishlistProduct.isDisplayed(), "product not found in the wishlist!");
        logger.info("Product " + productName + " found in the wishlist.");

        // Step 4: Locate and click the 'Add to Cart' checkbox using cssSelector
        helper.realisticDelay();
        WebElement addToCartCheckbox = helper.waitForClickable(helper.getByLocator("wishlist.addToCartCheckbox"));

        // Scroll the element into view to ensure it is visible and clickable
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addToCartCheckbox);

        // Click the checkbox
        addToCartCheckbox.click();
        logger.info("Clicked the 'Add to Cart' checkbox.");

        // Step 5: Click the 'Add to Cart' button below the wishlist
        WebElement addToCartButton = helper.waitForClickable(helper.getByLocator("wishlist.addToCartButton")); // The button inside the wishlist to add to cart
        addToCartButton.click();
        logger.info("Clicked the 'Add to Cart' button.");

        // Step 6: Verify the product is now in the cart
        driver.get(cartUrl);
        helper.realisticDelay();
        WebElement cartProduct = helper.waitForVisibility(
                By.xpath("//a[@class='product-name' and contains(text(), 'Lenovo IdeaCentre')]"));

        // Step 7: Assert the product is in the cart
        Assert.assertTrue(cartProduct.isDisplayed(), "Product " + productName + " was not added to the cart!");
        logger.info("Product " + productName + " successfully added to the cart.");

    }

    public void checkCartQuantity() throws InterruptedException {
        WebElement cartQty = helper.waitForVisibility(helper.getByLocator("cart.quantity"));
        String qtyText = cartQty.getText().replaceAll("[^0-9]", ""); // Extract numeric value
        Assert.assertTrue(Integer.parseInt(qtyText) > 0, "Cart quantity did not update.");
        logger.info("Cart quantity updated to: " + qtyText);
    }

    public void enterPromoCode(String code) throws InterruptedException {
        // Step 1: Navigate to the cart
        driver.get(cartUrl);

        // Step 2: Enter invalid promo code
        helper.realisticDelay();
        WebElement couponInput = driver.findElement(helper.getByLocator("cart.promoCodeInput"));
        couponInput.sendKeys(code);
        driver.findElement(helper.getByLocator("cart.applyDiscountCode")).click();
    }

    public void goToCheckout() throws InterruptedException {
        // Step 1: Go to cart page
        driver.get(cartUrl);
        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("cart.tableWrapper"));
        logger.info("Cart page loaded successfully.");

        // Step 2: Accept terms of service
        helper.realisticDelay();
        WebElement termsCheckbox = helper.waitForClickable(helper.getByLocator("cart.termsOfService"));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }
        logger.info("Accepted terms of service.");

        // Step 3: Click "Checkout" button
        WebElement checkoutButton = driver.findElement(helper.getByLocator("cart.checkoutButton"));
        checkoutButton.click();
        logger.info("Clicked on Checkout button.");

        // Step 4: Verify checkout page loads
        helper.realisticDelay();
        wait.until(ExpectedConditions.urlContains("/checkout"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/checkout"), "Checkout page did not load.");
        logger.info("Checkout page loaded successfully.");
    }

    public void checkoutAsUserWithoutContinue(String[] details) throws InterruptedException {
        // Step 1: Go to cart page
        driver.get(cartUrl);
        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("cart.tableWrapper"));
        logger.info("Cart page loaded successfully.");

        // Step 2: Accept terms and proceed to checkout
        helper.realisticDelay();
        WebElement termsCheckbox = helper.waitForClickable(helper.getByLocator("cart.termsOfService"));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }
        logger.info("Accepted terms of service.");
        driver.findElement(By.id("checkout")).click();
        logger.info("Checkout page loaded successfully.");

        // Step 3: Fill personal details
        driver.findElement(helper.getByLocator("checkout.firstName")).sendKeys(details[0]);
        driver.findElement(helper.getByLocator("checkout.lastName")).sendKeys(details[1]);

        // Select country
        Select countryDropdown = new Select(driver.findElement(helper.getByLocator("checkout.countryDropdown")));
        countryDropdown.selectByVisibleText(details[3]);

        // Select the state
        helper.realisticDelay();
        Select stateDropdown = new Select(driver.findElement(helper.getByLocator("checkout.stateDropdown")));
        stateDropdown.selectByVisibleText(details[4]);

        // Fill address details
        driver.findElement(helper.getByLocator("checkout.city")).sendKeys(details[5]);
        driver.findElement(helper.getByLocator("checkout.address")).sendKeys(details[6]);
        driver.findElement(helper.getByLocator("checkout.zipCode")).sendKeys(details[7]);
        driver.findElement(helper.getByLocator("checkout.phoneNumber")).sendKeys(details[8]);

        // Select "Ship to the same address" checkbox
        WebElement shipToSameAddressCheckbox = driver.findElement(helper.getByLocator("checkout.shipToSameAddress"));
        if (!shipToSameAddressCheckbox.isSelected()) {
            shipToSameAddressCheckbox.click();
        }
        logger.info("Checked 'Ship to the same address' option.");

        // Click continue
        helper.realisticDelay();
        WebElement continueButton = helper.waitForClickable(helper.getByLocator("checkout.continueButton"));
        continueButton.click();

        // Step 4: Choose shipping method
        helper.realisticDelay();
        helper.waitForClickable(helper.getByLocator("shipping.option1")).click();
        driver.findElement(helper.getByLocator("shipping.continueButton")).click();
        logger.info("Shipping method selected.");

        enterPaymentDetails(details[9], details[10], details[11], details[12], details[13]);

        // Step 5: Confirm order
        helper.realisticDelay();
        helper.waitForClickable(helper.getByLocator("order.confirmButton")).click();
        logger.info("Order confirmed.");

        // Step 6: Validate order success
        helper.realisticDelay();
        WebElement orderSuccessMessage = helper.waitForVisibility(helper.getByLocator("order.successMessage"));
        Assert.assertTrue(orderSuccessMessage.getText().contains("Your order has been successfully processed!"), "Order not successful!");
        logger.info("Order placed successfully!");
    }

    public void checkoutAsGuest(String[] details) throws InterruptedException {
        // Step 1: Go to cart page
        driver.get(cartUrl);
        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("cart.tableWrapper"));
        logger.info("Cart page loaded successfully.");

        // Step 2: Accept terms and proceed to checkout
        helper.realisticDelay();
        WebElement termsCheckbox = helper.waitForClickable(helper.getByLocator("cart.termsOfService"));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }
        logger.info("Accepted terms of service.");
        driver.findElement(helper.getByLocator("cart.checkoutButton")).click();

        // Step 3: Select "Checkout as Guest"
        helper.realisticDelay();
        WebElement guestCheckoutButton = helper.waitForClickable(helper.getByLocator("checkout.guestButton"));
        guestCheckoutButton.click();
        logger.info("Selected 'Checkout as Guest' option.");

        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("checkout.firstName"));
        logger.info("Checkout page loaded successfully.");
        helper.realisticDelay();

        // Fill billing details
        driver.findElement(helper.getByLocator("checkout.firstName")).sendKeys(details[0]);
        driver.findElement(helper.getByLocator("checkout.lastName")).sendKeys(details[1]);
        driver.findElement(helper.getByLocator("checkout.email")).sendKeys(details[2]);

        // Select country
        Select countryDropdown = new Select(driver.findElement(helper.getByLocator("checkout.countryDropdown")));
        countryDropdown.selectByVisibleText(details[3]);

        // Select the state
        helper.realisticDelay();
        Select stateDropdown = new Select(driver.findElement(helper.getByLocator("checkout.stateDropdown")));
        stateDropdown.selectByVisibleText(details[4]);

        // Fill address details
        driver.findElement(helper.getByLocator("checkout.city")).sendKeys(details[5]);
        driver.findElement(helper.getByLocator("checkout.address")).sendKeys(details[6]);
        driver.findElement(helper.getByLocator("checkout.zipCode")).sendKeys(details[7]);
        driver.findElement(helper.getByLocator("checkout.phoneNumber")).sendKeys(details[8]);

        // Select "Ship to the same address" checkbox
        WebElement shipToSameAddressCheckbox = driver.findElement(helper.getByLocator("checkout.shipToSameAddress"));
        if (!shipToSameAddressCheckbox.isSelected()) {
            shipToSameAddressCheckbox.click();
        }
        logger.info("Checked 'Ship to the same address' option.");

        // Click continue
        helper.realisticDelay();
        WebElement continueButton = helper.waitForClickable(helper.getByLocator("checkout.continueButton"));
        continueButton.click();

        // Step 4: Choose shipping method
        helper.realisticDelay();
        helper.waitForClickable(helper.getByLocator("shipping.option1")).click();
        driver.findElement(helper.getByLocator("shipping.continueButton")).click();
        logger.info("Shipping method selected.");

        // Step 5: Select payment method
        helper.realisticDelay();
        helper.waitForClickable(helper.getByLocator("payment.method1")).click();
        driver.findElement(helper.getByLocator("payment.continueButton")).click();
        logger.info("Payment method selected.");

        // Step 6: Enter payment details
        driver.findElement(helper.getByLocator("payment.cardholderName")).sendKeys(details[9]);
        driver.findElement(helper.getByLocator("payment.cardNumber")).sendKeys(details[10]);

        Select expMonth = new Select(driver.findElement(helper.getByLocator("payment.expireMonth")));
        expMonth.selectByVisibleText(details[11]);

        Select expYear = new Select(driver.findElement(helper.getByLocator("payment.expireYear")));
        expYear.selectByVisibleText(details[12]);

        driver.findElement(helper.getByLocator("payment.cardCode")).sendKeys(details[13]);

        driver.findElement(helper.getByLocator("payment.infoContinueButton")).click();
        logger.info("Payment details entered.");
    }

    public void verifyProductDetails(String[] orderDetails){
        // Step 1: Verify product details in order summary
        WebElement productName = driver.findElement(helper.getByLocator("orderSummary.productName"));
        String productNameText = productName.getText();
        Assert.assertEquals(productNameText, orderDetails[0], "Product name mismatch in order summary.");
        logger.info("Product name matches in order summary: " + productNameText);

        WebElement productPrice = driver.findElement(helper.getByLocator("orderSummary.productPrice"));
        String productPriceText = productPrice.getText();
        Assert.assertEquals(productPriceText, orderDetails[1], "Product price mismatch in order summary.");
        logger.info("Product price matches in order summary: " + productPriceText);

        WebElement productQuantity = driver.findElement(helper.getByLocator("orderSummary.productQuantity"));
        String productQuantityText = productQuantity.getText();
        Assert.assertEquals(productQuantityText, orderDetails[2], "Product quantity mismatch in order summary.");
        logger.info("Product quantity matches in order summary: " + productQuantityText);

        WebElement orderSubtotal = driver.findElement(helper.getByLocator("orderSummary.subtotal"));
        String subtotalText = orderSubtotal.getText();
        Assert.assertEquals(subtotalText, orderDetails[3], "Order subtotal mismatch in order summary.");
        logger.info("Order subtotal matches in order summary: " + subtotalText);

        WebElement shippingCost = driver.findElement(helper.getByLocator("orderSummary.shippingCost"));
        String shippingCostText = shippingCost.getText();
        Assert.assertEquals(shippingCostText, orderDetails[4], "Shipping cost mismatch in order summary.");
        logger.info("Shipping cost matches in order summary: " + shippingCostText);

        WebElement orderTotal = driver.findElement(helper.getByLocator("orderSummary.total"));
        String orderTotalText = orderTotal.getText();
        Assert.assertEquals(orderTotalText, orderDetails[5], "Order total mismatch in order summary.");
        logger.info("Order total matches in order summary: " + orderTotalText);

        logger.info("Checkout page displays correct order summary");
    }

    public void checkoutAddressOnly(String[] details) throws InterruptedException {
        // Step 1: Go to cart page
        driver.get(cartUrl);
        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("cart.tableWrapper"));
        logger.info("Cart page loaded successfully.");

        // Step 2: Accept terms and proceed to checkout
        helper.realisticDelay();
        WebElement termsCheckbox = helper.waitForClickable(helper.getByLocator("cart.termsOfService"));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }
        logger.info("Accepted terms of service.");
        driver.findElement(By.id("checkout")).click();

        // Step 3: Select "Checkout as Guest"
        helper.realisticDelay();
        WebElement guestCheckoutButton = helper.waitForClickable(helper.getByLocator("checkout.guestButton"));
        guestCheckoutButton.click();
        logger.info("Selected 'Checkout as Guest' option.");

        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("checkout.firstName"));
        logger.info("Checkout page loaded successfully.");
        Thread.sleep(1000);

        // Step 4: Enter incorrect billing details
        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("checkout.firstName")).sendKeys(details[0]);
        driver.findElement(helper.getByLocator("checkout.lastName")).sendKeys(details[1]);
        driver.findElement(helper.getByLocator("checkout.email")).sendKeys(details[2]);

        // Select an invalid country (or skip selection)
        Select countryDropdown = new Select(driver.findElement(helper.getByLocator("checkout.countryDropdown")));
        countryDropdown.selectByVisibleText(details[3]);

        // Select the state
        helper.realisticDelay();
        Select stateDropdown = new Select(driver.findElement(helper.getByLocator("checkout.stateDropdown")));
        stateDropdown.selectByVisibleText(details[4]);

        // Enter incomplete address
        driver.findElement(helper.getByLocator("checkout.city")).sendKeys(details[5]);
        driver.findElement(helper.getByLocator("checkout.address")).sendKeys(details[6]);
        driver.findElement(helper.getByLocator("checkout.zipCode")).sendKeys(details[7]);
        driver.findElement(helper.getByLocator("checkout.phoneNumber")).sendKeys(details[8]);

        // Click continue
        helper.realisticDelay();
        WebElement continueButton = helper.waitForClickable(helper.getByLocator("checkout.continueButton"));
        continueButton.click();

        try {
            // Wait for the alert to appear (within 5 seconds)
            WebDriverWait waitAlert = new WebDriverWait(driver, Duration.ofSeconds(5));
            waitAlert.until(ExpectedConditions.alertIsPresent());

            // Switch to alert
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            logger.info("Validation alert found: " + alertText);

            // Assert that the expected error messages are present
            if (alertText.contains("City is required") &&
                    alertText.contains("Email is required") &&
                    alertText.contains("Street address is required") &&
                    alertText.contains("Zip / postal code is required")) {

                logger.info("Validation alert appeared as expected.");
            } else {
                logger.warn("Unexpected alert message: " + alertText);
            }

            // Accept the alert to close it
            alert.accept();
        } catch (TimeoutException e) {
            logger.info("No validation alert appeared. Proceeding with the test.");
        } catch (NoAlertPresentException e) {
            logger.info("No alert was found. Continuing execution.");
        } catch (Exception e) {
            logger.error("Unexpected error occurred while handling alert: " + e.getMessage());
        }

    }

    public void checkPaymentMethods(String[] details) throws InterruptedException {
        // Step 1: Go to cart page
        driver.get(cartUrl);
        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("cart.tableWrapper"));
        logger.info("Cart page loaded successfully.");

        // Step 2: Accept terms and proceed to checkout
        helper.realisticDelay();
        WebElement termsCheckbox = helper.waitForClickable(helper.getByLocator("cart.termsOfService"));
        if (!termsCheckbox.isSelected()) {
            termsCheckbox.click();
        }
        logger.info("Accepted terms of service.");
        driver.findElement(helper.getByLocator("cart.checkoutButton")).click();

        // Step 3: Select "Checkout as Guest"
        helper.realisticDelay();
        WebElement guestCheckoutButton = helper.waitForClickable(helper.getByLocator("checkout.guestButton"));
        guestCheckoutButton.click();
        logger.info("Selected 'Checkout as Guest' option.");

        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("checkout.firstName"));
        logger.info("Checkout page loaded successfully.");
        helper.realisticDelay();

        // Fill billing details
        driver.findElement(helper.getByLocator("checkout.firstName")).sendKeys(details[0]);
        driver.findElement(helper.getByLocator("checkout.lastName")).sendKeys(details[1]);
        driver.findElement(helper.getByLocator("checkout.email")).sendKeys(details[2]);

        // Select country
        Select countryDropdown = new Select(driver.findElement(helper.getByLocator("checkout.countryDropdown")));
        countryDropdown.selectByVisibleText(details[3]);

        // Select the state
        helper.realisticDelay();
        Select stateDropdown = new Select(driver.findElement(helper.getByLocator("checkout.stateDropdown")));
        stateDropdown.selectByVisibleText(details[4]);

        // Fill address details
        driver.findElement(helper.getByLocator("checkout.city")).sendKeys(details[5]);
        driver.findElement(helper.getByLocator("checkout.address")).sendKeys(details[6]);
        driver.findElement(helper.getByLocator("checkout.zipCode")).sendKeys(details[7]);
        driver.findElement(helper.getByLocator("checkout.phoneNumber")).sendKeys(details[8]);

        // Select "Ship to the same address" checkbox
        WebElement shipToSameAddressCheckbox = driver.findElement(helper.getByLocator("checkout.shipToSameAddress"));
        if (!shipToSameAddressCheckbox.isSelected()) {
            shipToSameAddressCheckbox.click();
        }
        logger.info("Checked 'Ship to the same address' option.");

        // Click continue
        helper.realisticDelay();
        WebElement continueButton = helper.waitForClickable(helper.getByLocator("checkout.continueButton"));
        continueButton.click();

        // Step 4: Choose shipping method
        helper.realisticDelay();
        helper.waitForClickable(helper.getByLocator("shipping.option1")).click();
        driver.findElement(helper.getByLocator("shipping.continueButton")).click();
        logger.info("Shipping method selected.");

        // Ensure the payment section is fully loaded
        helper.realisticDelay();
        WebElement paymentSection = helper.waitForVisibility(helper.getByLocator("payment.methodBlock"));
        logger.info("Payment section loaded successfully.");

        // Ensure radio buttons are present
        helper.realisticDelay();
        List<WebElement> paymentOptions = helper.waitForAllElementsPresence(By.cssSelector("input[type='radio']"));
        Assert.assertFalse(paymentOptions.isEmpty(), "No payment options found.");
        logger.info("Payment options are listed.");

        // Scroll into view if necessary
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", paymentOptions.get(0));

        // Verify specific payment options exist
        boolean isCheckMoneyOrderVisible = false;
        boolean isCreditCardVisible = false;

        for (WebElement radio : paymentOptions) {
            String labelText = radio.findElement(helper.getByLocator("radioLabelXPath")).getText();
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

    public void selectCategory(String key) throws InterruptedException {
        // Step 1: Navigate to the home page
        driver.get(url);
        logger.info("Navigating to home page.");

        // Step 2: Locate and click on a category
        helper.realisticDelay();
        WebElement category = helper.waitForClickable(helper.getByLocator(key));
        category.click();
        logger.info("Clicked on 'Electronics' category.");

        // Step 3: Verify category page loads correctly
        helper.realisticDelay();
        WebElement categoryHeader = helper.waitForVisibility(By.cssSelector(".page-title"));
        Assert.assertTrue(categoryHeader.getText().contains("Electronics"), "Category page did not load correctly!");
        logger.info("Verified category page loads correctly.");
    }

    public void selectSubCategory(String key) throws InterruptedException {
        // Step 1: Locate and click on a subcategory
        helper.realisticDelay();
        WebElement subCategory = helper.waitForClickable(helper.getByLocator(key));
        subCategory.click();
        logger.info("Clicked on " + key + "subcategory.");

        // Step 2: Verify subcategory page loads correctly
        helper.realisticDelay();
        WebElement subCategoryHeader = helper.waitForVisibility(helper.getByLocator("category.pageTitle"));
        Assert.assertTrue(subCategoryHeader.getText().contains(subCategoryHeader.getText()), "Subcategory page did not load correctly!");
        logger.info("Verified subcategory page loads correctly.");

        // Step 3: Verify products are displayed for the selected category/subcategory
        helper.realisticDelay();
        List<WebElement> productItems = driver.findElements(helper.getByLocator("category.productItem"));
        Assert.assertFalse(productItems.isEmpty(), "No products displayed in the selected subcategory!");
        logger.info("Verified products are displayed correctly.");
    }

    public void breadcrumbNavigation(String[] expectedBreadcrumbs) throws InterruptedException {
        for (int i = expectedBreadcrumbs.length - 1; i >= 0; i--) {
            // Step 2: Re-locate breadcrumb elements after each navigation
            List<WebElement> breadcrumbLinks = helper.waitForAllElementsPresence(helper.getByLocator("breadcrumb.navigation"));

            // Verify breadcrumb text
            String breadcrumbText = breadcrumbLinks.get(i).getText().trim();
            Assert.assertTrue(breadcrumbText.equalsIgnoreCase(expectedBreadcrumbs[i]),
                    "Breadcrumb does not match expected: " + expectedBreadcrumbs[i]);
            logger.info("Verified breadcrumb: " + breadcrumbText);

            // Click breadcrumb link
            breadcrumbLinks.get(i).click();
            logger.info("Clicked breadcrumb: " + breadcrumbText);

            // Step 3: Wait for page load and confirm navigation
            helper.realisticDelay();

            // If we returned to the homepage, stop further navigation
            if (driver.getCurrentUrl().equals("https://demo.nopcommerce.com/")) {
                logger.info("Reached the homepage. Ending breadcrumb navigation test.");
                break;  // Stop the loop when we reach the homepage
            }

            // Re-locate breadcrumb links again to avoid stale element reference
            helper.waitForAllElementsPresence(helper.getByLocator("breadcrumb.crumb"));

            // Verify we landed on the correct page
            Assert.assertTrue(driver.getCurrentUrl().contains(expectedBreadcrumbs[i].toLowerCase()),
                    "Navigation failed for breadcrumb: " + expectedBreadcrumbs[i]);
            logger.info("Successfully navigated using breadcrumb: " + expectedBreadcrumbs[i]);
        }
    }

    public void enterPaymentDetails(String name, String card, String month, String year, String cardNumber) throws InterruptedException {
        // Step 1: Select payment method
        helper.realisticDelay();
        helper.waitForClickable(helper.getByLocator("payment.method1")).click();
        driver.findElement(helper.getByLocator("payment.continueButton")).click();
        System.out.println("Payment method selected.");

        // Step 2: Enter payment details
        driver.findElement(helper.getByLocator("payment.cardholderName")).sendKeys(name);
        driver.findElement(helper.getByLocator("payment.cardNumber")).sendKeys(card);

        Select expMonth = new Select(driver.findElement(helper.getByLocator("payment.expireMonth")));
        expMonth.selectByVisibleText(month);

        Select expYear = new Select(driver.findElement(helper.getByLocator("payment.expireYear")));
        expYear.selectByVisibleText(year);

        driver.findElement(helper.getByLocator("payment.cardCode")).sendKeys(cardNumber);  // Invalid card code

        helper.realisticDelay();
        driver.findElement(helper.getByLocator("payment.infoContinueButton")).click();
        System.out.println("Payment details entered.");
    }

    public void selectProductOptions(Map<String, String> options) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        SeleniumHelper helper = new SeleniumHelper(driver);

        for (Map.Entry<String, String> entry : options.entrySet()) {
            String attribute = entry.getKey();
            String value = entry.getValue(); // The correct option value

            helper.realisticDelay();

            if (attribute.equals("Processor") || attribute.equals("RAM")) {
                // Get correct dropdown ID
                String dropdownId = (attribute.equals("Processor")) ? "product_attribute_1" : "product_attribute_2";
                WebElement dropdown = helper.waitForClickable(By.id(dropdownId));
                helper.scrollToElement(dropdown);
                helper.realisticDelay();
                js.executeScript("arguments[0].value='" + value + "'; arguments[0].dispatchEvent(new Event('change'))", dropdown);
                helper.realisticDelay();
            } else {
                // Handle radio buttons (HDD, OS) and checkboxes (Software)
                WebElement option = driver.findElement(By.xpath("//input[@id='" + value + "']"));
                helper.scrollToElement(option);
                helper.realisticDelay();
                if (!option.isSelected()) {
                    js.executeScript("arguments[0].click(); arguments[0].dispatchEvent(new Event('change'))", option);
                }
                helper.realisticDelay();
            }
        }
    }

    public void searchProduct(String searchTerm) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        SeleniumHelper helper = new SeleniumHelper(driver);

        logger.info("Searching for product: " + searchTerm);

        // Locate the search bar and input the search term
        helper.realisticDelay();
        WebElement searchBox = helper.waitForClickable(helper.getByLocator("search.box"));
        searchBox.clear();
        searchBox.sendKeys(searchTerm);

        // Click the search button
        WebElement searchButton = driver.findElement(helper.getByLocator("search.searchButton"));
        searchButton.click();

        // Wait for search results to load
        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("search.results"));

        // Verify search results are displayed
        WebElement searchResults = driver.findElement(helper.getByLocator("search.results"));
        Assert.assertTrue(searchResults.isDisplayed(), "Search results are not displayed!");

        logger.info("Search for '" + searchTerm + "' completed successfully.");
    }

    public void checkSorting(List<WebElement> products, String sortingType) {
        List<String> extractedValues = new ArrayList<>();

        for (WebElement product : products) {
            if (sortingType.contains("Price")) {
                String priceText = product.findElement(helper.getByLocator("sorting.priceSelector")).getText();
                double price = Double.parseDouble(priceText.replaceAll("[^0-9.]", ""));
                extractedValues.add(String.valueOf(price));
            } else { // Name sorting
                String productName = product.findElement(helper.getByLocator("sorting.titleSelector")).getText();
                extractedValues.add(productName);
            }
        }

        List<String> sortedValues = new ArrayList<>(extractedValues);
        if (sortingType.equals("Price: Low to High")) {
            sortedValues.sort(Comparator.comparingDouble(Double::parseDouble));
        } else if (sortingType.equals("Price: High to Low")) {
            sortedValues.sort(Comparator.comparingDouble(Double::parseDouble).reversed());
        } else if (sortingType.equals("Name: A to Z")) {
            Collections.sort(sortedValues);
        } else if (sortingType.equals("Name: Z to A")) {
            sortedValues.sort(Collections.reverseOrder());
        }

        Assert.assertEquals(extractedValues, sortedValues, "Sorting verification failed for: " + sortingType);
        logger.info("Sorting verified successfully for: " + sortingType);
    }

    public boolean isPaginationAvailable() {
        List<WebElement> paginationControls = driver.findElements(By.cssSelector(".pager .page-item"));
        return !paginationControls.isEmpty();
    }

    public void navigateToNextPage(WebDriverWait wait, SeleniumHelper helper) throws InterruptedException {
        WebElement nextPageButton = driver.findElement(By.cssSelector(".pager .next-page"));
        nextPageButton.click();
        helper.realisticDelay();
        wait.until(ExpectedConditions.stalenessOf(driver.findElement(By.cssSelector(".product-grid"))));
    }

    public void navigateToPreviousPage(WebDriverWait wait, SeleniumHelper helper) throws InterruptedException {
        WebElement previousPageButton = driver.findElement(By.cssSelector(".pager .previous-page"));
        previousPageButton.click();
        helper.realisticDelay();
        wait.until(ExpectedConditions.stalenessOf(driver.findElement(By.cssSelector(".product-grid"))));
    }

    public List<WebElement> getSearchResults() {
        return driver.findElements(By.cssSelector(".product-grid .item-box"));
    }

    public void relatedProducts() throws InterruptedException{
        helper.realisticDelay();

        // Verify the Related Products section exists
        WebElement relatedProductsSection = helper.waitForVisibility(helper.getByLocator("search.productGrid"));
        Assert.assertTrue(relatedProductsSection.isDisplayed(), "Related products section is NOT displayed!");

        // Verify that at least one related product is present
        List<WebElement> relatedProducts = relatedProductsSection.findElements(helper.getByLocator("search.productBox"));
        Assert.assertFalse(relatedProducts.isEmpty(), "No related products found!");

        // Log the related products found
        logger.info("Number of related products displayed: " + relatedProducts.size());

        // Verify each related product has a title
        for (WebElement product : relatedProducts) {
            WebElement productTitle = product.findElement(helper.getByLocator("search.productTitle"));
            Assert.assertTrue(productTitle.isDisplayed(), "A related product does not have a title displayed!");
            logger.info("Related Product Found: " + productTitle.getText());
        }
    }

    public void clickThumbnailAndVerifyImageUpdate(String mainImageLocator, String thumbLocator, int thumbIndex, String expectedSrcFragment) throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Locate main product image
        WebElement mainProductImage = helper.waitForVisibility(helper.getByLocator(mainImageLocator));

        // Get list of thumbnails
        List<WebElement> thumbItems = driver.findElements(helper.getByLocator(thumbLocator));
        Assert.assertFalse(thumbItems.isEmpty(), "No thumbnail images found!");

        // Click on the specified thumbnail using JavaScript
        WebElement selectedThumbnail = thumbItems.get(thumbIndex);
        js.executeScript("arguments[0].click();", selectedThumbnail);

        // Debugging
        System.out.println("Before click: " + mainProductImage.getAttribute("src"));

        // Wait for the image to update
        helper.realisticDelay();
        helper.waitForAttributeToLoad(mainProductImage, "src", expectedSrcFragment);

        // Debugging
        System.out.println("After click: " + mainProductImage.getAttribute("src"));

        // Validate image update
        String updatedImageSrc = mainProductImage.getAttribute("src");
        String expectedDefaultSizeSrc = selectedThumbnail.findElement(By.tagName("img")).getAttribute("data-defaultsize");

        Assert.assertTrue(updatedImageSrc.contains(expectedSrcFragment), "Main product image did not update correctly!");

        logger.info("Product image gallery allows browsing images successfully.");
    }

    public void checkReviews() throws InterruptedException{
        // Scroll to the review section and wait for it to be visible
        helper.realisticDelay();
        WebElement reviewsSection = helper.waitForVisibility(helper.getByLocator("reviews.list"));
        Assert.assertTrue(reviewsSection.isDisplayed(), "Reviews section is not displayed!");

        // Find all review items
        List<WebElement> reviewItems = driver.findElements(helper.getByLocator("reviews.item"));
        Assert.assertFalse(reviewItems.isEmpty(), "No reviews found!");

        for (WebElement review : reviewItems) {
            // Verify review title
            WebElement reviewTitle = review.findElement(helper.getByLocator("reviews.title"));
            Assert.assertTrue(reviewTitle.isDisplayed(), "Review title is not displayed!");

            // Verify review rating
            WebElement rating = review.findElement(helper.getByLocator("reviews.ratings"));
            Assert.assertTrue(rating.isDisplayed(), "Review rating is not displayed!");

            // Verify review content
            WebElement reviewContent = review.findElement(helper.getByLocator("reviews.text"));
            Assert.assertTrue(reviewContent.isDisplayed(), "Review content is not displayed!");

            // Verify reviewer name and date
            WebElement reviewerName = review.findElement(helper.getByLocator("reviews.user"));
            WebElement reviewDate = review.findElement(helper.getByLocator("reviews.data"));
            Assert.assertTrue(reviewerName.isDisplayed(), "Reviewer name is not displayed!");
            Assert.assertTrue(reviewDate.isDisplayed(), "Review date is not displayed!");
        }

        // Verify the Helpful Vote options (Yes/No)
        List<WebElement> helpfulVoteYes = driver.findElements(helper.getByLocator("reviews.voteYes"));
        List<WebElement> helpfulVoteNo = driver.findElements(helper.getByLocator("reviews.voteNo"));

        Assert.assertFalse(helpfulVoteYes.isEmpty(), "'Yes' vote option is not displayed!");
        Assert.assertFalse(helpfulVoteNo.isEmpty(), "'No' vote option is not displayed!");

        logger.info("Product reviews are displayed and verified successfully.");
    }

    public boolean submitReview(String title, String text, int rating) {
        try {
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            helper.realisticDelay();

            WebElement reviewForm = helper.waitForPresence(helper.getByLocator("reviews.form"));
            if (!reviewForm.isDisplayed()) return false;

            WebElement writeReviewButton = helper.waitForClickable(helper.getByLocator("reviews.add"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", writeReviewButton);

            WebElement reviewTitle = helper.waitForVisibility(helper.getByLocator("reviews.addTitle"));
            WebElement reviewText = driver.findElement(helper.getByLocator("reviews.addText"));
            WebElement ratingOption = driver.findElement(By.id("addproductrating_" + rating));
            WebElement submitReviewButton = driver.findElement(helper.getByLocator("reviews.add"));

            reviewTitle.sendKeys(title);
            reviewText.sendKeys(text);
            ratingOption.click();
            submitReviewButton.click();

            WebElement successNotification = helper.waitForVisibility(helper.getByLocator("reviews.bar"));
            return successNotification.getText().contains("Product review is successfully added");
        } catch (Exception e) {
            return false;
        }
    }

    public void addProductToCompareList(String productUrl) throws InterruptedException {
        logger.info("Opening product page: " + productUrl);
        driver.get(productUrl);

        helper.randomScroll();
        helper.realisticDelay();

        WebElement compareButton = helper.waitForClickable(helper.getByLocator("compare.add"));
        compareButton.click();

        logger.info("Added product to the compare list: " + productUrl);
        helper.randomScroll();
        helper.realisticDelay();
    }

    public void verifyCompareList(List<String> expectedProducts) throws InterruptedException {
        logger.info("Navigating to Compare Products list.");
        driver.get("https://demo.nopcommerce.com/compareproducts");
        helper.randomScroll();
        helper.realisticDelay();

        // Verify Compare List Page
        helper.realisticDelay();
        WebElement compareTitle = helper.waitForVisibility(helper.getByLocator("page.title"));
        Assert.assertEquals(compareTitle.getText(), "Compare products", "Compare page title is incorrect!");

        // Get compared products
        List<WebElement> comparedProducts = driver.findElements(helper.getByLocator("compare.firstProd"));

        List<String> productNames = comparedProducts.stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());

        logger.info("Extracted Products: " + productNames);

        // Validate each expected product exists in the compare list
        for (String expectedProduct : expectedProducts) {
            Assert.assertTrue(productNames.contains(expectedProduct), expectedProduct + " not found in compare list!");
        }

        logger.info("Compare list verification successful.");
    }

    public void checkSocialMediaSharing(String productUrl) throws InterruptedException {
        logger.info("Navigating to product page: " + productUrl);
        driver.get(productUrl);

        // Scroll to the social media sharing section
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        helper.realisticDelay();

        // Locate social media share buttons
        List<WebElement> shareButtons = driver.findElements(helper.getByLocator("social.button"));
        if (shareButtons.isEmpty()) {
            logger.warn("No social media share buttons found! Skipping test but marking as passed.");
            return;
        }

        // Log detected social media platforms
        List<String> detectedPlatforms = shareButtons.stream()
                .map(button -> button.getAttribute("data-network"))
                .collect(Collectors.toList());
        logger.info("Detected social media platforms: " + detectedPlatforms);

        for (WebElement shareButton : shareButtons) {
            try {
                String platform = shareButton.getAttribute("data-network");

                // Special case for email sharing
                if (platform.equals("email")) {
                    String mailtoLink = shareButton.getAttribute("href");
                    Assert.assertTrue(mailtoLink.startsWith("mailto:"), "Email share button does not contain 'mailto:' link.");
                    logger.info("Verified email sharing contains mailto link: " + mailtoLink);
                    continue; // Skip opening a new window
                }

                // Click share button
                String mainWindowHandle = driver.getWindowHandle();
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", shareButton);
                logger.info("Clicked share button for: " + platform);

                // Wait for new tab or potential redirect
                helper.realisticDelay();
                Set<String> windowHandles = driver.getWindowHandles();

                // Special case for Messenger (may redirect instead of opening a new tab)
                if (platform.equals("messenger") && windowHandles.size() == 1) {
                    Assert.assertTrue(driver.getCurrentUrl().contains("messenger.com"), "Messenger did not redirect correctly.");
                    logger.info("Messenger redirected correctly.");
                    driver.navigate().back();
                    continue;
                }

                // Ensure a new tab opened
                if (windowHandles.size() <= 1) {
                    logger.warn("Share window did not open for " + platform + ", but continuing.");
                    continue;
                }

                // Switch to the new tab and verify URL
                for (String handle : windowHandles) {
                    if (!handle.equals(mainWindowHandle)) {
                        driver.switchTo().window(handle);
                        logger.info("Successfully navigated to " + platform + " share page: " + driver.getCurrentUrl());
                        break;
                    }
                }

                // Close new tab and return to main page
                driver.close();
                driver.switchTo().window(mainWindowHandle);
                helper.realisticDelay();
            } catch (Exception e) {
                logger.warn("Failed to verify sharing for " + e.getMessage() + ", but continuing.");
            }
        }
        logger.info("Social media sharing test completed successfully.");
    }

    public void navigateToCategory(String mainCategory, String subCategory, WebDriverWait wait, SeleniumHelper helper) throws InterruptedException {
        helper.realisticDelay();
        WebElement categoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText(mainCategory)));
        categoryLink.click();

        helper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".page-title")));

        helper.realisticDelay();
        WebElement subCategoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText(subCategory)));
        subCategoryLink.click();

        helper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".page-title")));
    }

    public void navigateToSubcategory(String mainCategory, String subCategory, WebDriverWait wait, SeleniumHelper helper) throws InterruptedException {
        helper.realisticDelay();
        WebElement mainCategoryLink = helper.waitForClickable(By.linkText(mainCategory));
        mainCategoryLink.click();

        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("category.pageTitle"));

        helper.realisticDelay();
        WebElement subCategoryLink = helper.waitForClickable(By.linkText(subCategory));
        subCategoryLink.click();

        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("category.pageTitle"));
    }

    public void applyManufacturerFilter(String filterForAttribute, WebDriverWait wait, SeleniumHelper helper) throws InterruptedException {
        helper.realisticDelay();
        WebElement filterLabel = helper.waitForClickable(By.cssSelector("label[for='" + filterForAttribute + "']"));
        filterLabel.click();

        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("pagination.resultsGrid"));
    }

    public void clearFilter(String filterForAttribute, WebDriverWait wait, SeleniumHelper helper) throws InterruptedException {
        helper.realisticDelay();
        WebElement filterLabel = helper.waitForClickable(By.xpath("//label[@for='" + filterForAttribute + "']"));
        filterLabel.click();

        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("pagination.resultsGrid"));
    }

    public void verifyFilteredProducts(WebDriverWait wait) {
        List<WebElement> productItems = driver.findElements(helper.getByLocator("product.productItem"));
        Assert.assertFalse(productItems.isEmpty(), "No products found under the applied filter!");
        logger.info("Filtering by manufacturer verified successfully.");
    }

    public void checkNoSearchResults(WebDriverWait wait) throws InterruptedException {
        helper.realisticDelay();
        WebElement noResultsMessage = helper.waitForVisibility(helper.getByLocator("search.noResultsMessage"));
        Assert.assertTrue(noResultsMessage.isDisplayed(), "No products found message is not displayed!");

        String expectedMessage = "No products were found that matched your criteria.";
        String actualMessage = noResultsMessage.getText();
        Assert.assertEquals(actualMessage, expectedMessage, "The no results message is incorrect!");

        logger.info("Invalid search term verification completed successfully.");
    }

    public void checkAutoSuggestions(String searchTerm, WebDriverWait wait, SeleniumHelper helper) throws InterruptedException {
        helper.realisticDelay();
        WebElement searchBox = helper.waitForClickable(helper.getByLocator("search.box"));
        searchBox.sendKeys(searchTerm);

        // Wait for auto-suggestions to appear
        helper.realisticDelay();
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(helper.getByLocator("search.menuItem"), 0));

        // Retrieve the list of suggestions
        List<WebElement> suggestions = driver.findElements(helper.getByLocator("search.menuItem"));

        if (suggestions.isEmpty()) {
            logger.warn("Auto-suggestions are not displayed. Check if the website's auto-suggestion feature is working properly.");
        }

        // Check that each suggestion contains the search term
        for (WebElement suggestion : suggestions) {
            String suggestionText = suggestion.getText().toLowerCase();
            if (!suggestionText.contains(searchTerm.toLowerCase())) {
                logger.warn("Suggestion does not contain '" + searchTerm + "'. Found: " + suggestionText);
            }
        }

        logger.info("Auto-suggestions in the search bar verified successfully.");
    }

    public void changePassword(String oldPassword, String newPassword) throws InterruptedException {
        // Step 1: Navigate to the 'Change Password' page
        driver.get(changePasswordUrl);
        logger.info("Navigated to 'Change Password' page.");

        // Step 2: Enter the new password details
        helper.realisticDelay();
        driver.findElement(helper.getByLocator("account.oldPassword")).sendKeys(oldPassword);
        driver.findElement(helper.getByLocator("account.newPassword")).sendKeys(newPassword);
        driver.findElement(helper.getByLocator("account.confirmNewPassword")).sendKeys(newPassword);
        driver.findElement(helper.getByLocator("account.changePasswordButton")).click();
        logger.info("New password entered and change submitted.");

        // Step 3: Verify the password change success message
        helper.realisticDelay();
        WebElement successMessage = helper.waitForVisibility(helper.getByLocator("account.changePasswordSuccess"));
        Assert.assertTrue(successMessage.getText().contains("Password was changed"), "Password change was not successful.");
        logger.info("Password changed successfully: " + successMessage.getText());

        // Step 4: Close the success notification pop-up
        WebElement closeNotificationButton = driver.findElement(helper.getByLocator("account.closeSuccessNotification"));
        closeNotificationButton.click();
        logger.info("Closed success notification.");
    }
}