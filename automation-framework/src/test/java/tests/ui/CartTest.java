package tests.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
import java.util.List;
import java.util.ArrayList;

public class CartTest extends BaseTest {
    private final String url = ConfigReader.getProperty("baseURL");
    private final String loginUrl = ConfigReader.getProperty("loginURL");
    private final String cartUrl = ConfigReader.getProperty("cartURL");
    private final String wishlistUrl = ConfigReader.getProperty("wishlistURL");

    @Test(groups = {"smoke"}, description = "TC-015")
    public void testCartPersistence() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Navigate to the product page
        String productUrl = "https://demo.nopcommerce.com/adobe-photoshop";
        driver.get(productUrl);
        logger.info("Navigating to product page: " + productUrl);

        // Step 2: Add product to cart
        SeleniumHelper.realisticDelay();
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

        // Step 5: Refresh the page
        driver.navigate().refresh();
        logger.info("Page refreshed to check cart persistence.");

        // Step 6: Re-check cart quantity
        cartQty = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-qty")));
        String newQtyText = cartQty.getText().replaceAll("[^0-9]", "");
        Assert.assertEquals(newQtyText, qtyText, "Cart persistence failed after refresh.");
        logger.info("Cart persistence verified successfully after refresh.");
    }

    @Test(groups = {"smoke"}, description = "TC-018")
    public void testAddMultipleProductsToCart() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        String[] productUrls = {
                "https://demo.nopcommerce.com/lenovo-ideacentre",
                "https://demo.nopcommerce.com/apple-macbook-pro",
                "https://demo.nopcommerce.com/flower-girl-bracelet",
                "https://demo.nopcommerce.com/pride-and-prejudice"
        };

        String[] productIds = {"3", "4", "43", "41"};
        String[] productNames = {"Lenovo IdeaCentre", "Apple MacBook Pro", "Flower Girl Bracelet", "Pride and Prejudice"};

        // Step 1: Add multiple products to the cart
        for (int i = 0; i < productUrls.length; i++) {
            driver.get(productUrls[i]);
            logger.info("Navigating to product page: " + productUrls[i]);

            SeleniumHelper.realisticDelay();
            WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-" + productIds[i])));
            addToCartButton.click();
            logger.info("Clicked 'Add to Cart' button for product: " + productNames[i]);

            SeleniumHelper.realisticDelay();
            WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bar-notification.success")));
            Assert.assertTrue(successMessage.isDisplayed(), productNames[i] + " was not added to the cart!");
            logger.info(productNames[i] + " successfully added to cart.");
        }

        // Step 2: Check cart quantity
        WebElement cartQty = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-qty")));
        String qtyText = cartQty.getText().replaceAll("[^0-9]", ""); // Extract numeric value
        Assert.assertTrue(Integer.parseInt(qtyText) >= 4, "Cart quantity is less than expected after adding multiple products.");
        logger.info("Cart quantity updated to: " + qtyText);

        // Step 3: Verify that all products are added to the cart
        driver.get("https://demo.nopcommerce.com/cart");
        logger.info("Navigating to cart page.");

        // Assert that each product is listed in the cart
        Assert.assertTrue(driver.getPageSource().contains("Lenovo IdeaCentre"), "Product 1 not found in cart.");
        Assert.assertTrue(driver.getPageSource().contains("Apple MacBook Pro"), "Product 2 not found in cart.");
        Assert.assertTrue(driver.getPageSource().contains("Flower Girl Bracelet"), "Product 3 not found in cart.");
        Assert.assertTrue(driver.getPageSource().contains("Pride and Prejudice"), "Product 4 not found in cart.");

        logger.info("All selected products are confirmed in the cart.");
    }

    @Test(groups = {"smoke"}, description = "TC-019")
    public void verifyRemoveMultipleProducts() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        String[] productUrls = {
                "https://demo.nopcommerce.com/lenovo-ideacentre",
                "https://demo.nopcommerce.com/apple-macbook-pro",
                "https://demo.nopcommerce.com/flower-girl-bracelet",
                "https://demo.nopcommerce.com/pride-and-prejudice"
        };

        String[] productIds = {"3", "4", "43", "41"};
        String[] productNames = {"Lenovo IdeaCentre", "Apple MacBook Pro", "Flower Girl Bracelet", "Pride and Prejudice"};

        // Step 1: Add multiple products to the cart
        for (int i = 0; i < productUrls.length; i++) {
            driver.get(productUrls[i]);
            logger.info("Navigating to product page: " + productUrls[i]);

            SeleniumHelper.realisticDelay();
            WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-" + productIds[i])));
            addToCartButton.click();
            logger.info("Clicked 'Add to Cart' button for product: " + productNames[i]);

            SeleniumHelper.realisticDelay();
            WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bar-notification.success")));
            Assert.assertTrue(successMessage.isDisplayed(), productNames[i] + " was not added to the cart!");
            logger.info(productNames[i] + " successfully added to cart.");
        }

        // Step 2: Verify all products are in the cart
        driver.get("https://demo.nopcommerce.com/cart");
        logger.info("Navigating to cart page.");

        for (String productName : productNames) {
            Assert.assertTrue(driver.getPageSource().contains(productName), productName + " not found in cart.");
            logger.info(productName + " is confirmed in the cart.");
        }

        // Step 3: Remove each product from the cart
        for (String productId : productIds) {
            SeleniumHelper.realisticDelay();
            WebElement removeButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".remove-btn")));
            removeButton.click();
            System.out.println("Clicked 'Remove' button for product ID: " + productId);
            Thread.sleep(2000); // Allow time for the cart update
        }

        // Step 4: Verify cart is empty
        WebElement cartMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".order-summary-content")));
        Assert.assertTrue(cartMessage.getText().contains("Your Shopping Cart is empty!"), "Cart is not empty after removing all products.");
        System.out.println("All products successfully removed. Cart is empty.");
    }

    @Test(groups = {"functional"}, description = "TC-068")
    public void verifyUpdateProductQuantity() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        String productUrl = "https://demo.nopcommerce.com/lenovo-ideacentre";
        String productId = "3";
        String productName = "Lenovo IdeaCentre";
        int expectedQuantity = 2;

        // Step 1: Add a product to the cart
        driver.get(productUrl);
        logger.info("Navigating to product page: " + productUrl);

        SeleniumHelper.realisticDelay();
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-" + productId)));
        addToCartButton.click();
        logger.info("Clicked 'Add to Cart' button for product: " + productName);

        SeleniumHelper.realisticDelay();
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bar-notification.success")));
        Assert.assertTrue(successMessage.isDisplayed(), productName + " was not added to the cart!");
        logger.info(productName + " successfully added to cart.");

        // Step 2: Navigate to cart and update quantity
        driver.get("https://demo.nopcommerce.com/cart");
        logger.info("Navigating to cart page.");

        SeleniumHelper.realisticDelay();
        WebElement quantityField = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".qty-input")));

        // Step 3: Verify quantity can be updated via input field
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = arguments[1];", quantityField, expectedQuantity);
        js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", quantityField);
        logger.info("Updated quantity to: " + expectedQuantity);

        // Verify total price update
        WebElement totalPriceElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-total .order-total")));
        Assert.assertNotNull(totalPriceElement.getText(), "Total price not updated correctly!");
        logger.info("Total price recalculated successfully: " + totalPriceElement.getText());

        // Step 4: Use quantity up button 3 times and quantity down button once

        for (int i = 0; i < 3; i++) {
            SeleniumHelper.realisticDelay();

            // Re-locate the button in each iteration to avoid StaleElementReferenceException
            WebElement quantityUpButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".quantity.up")));
            quantityUpButton.click();

            logger.info("Clicked quantity up button: " + (i + 1) + " times");
        }

        SeleniumHelper.realisticDelay();
        WebElement quantityDownButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".quantity.down")));
        quantityDownButton.click();

        SeleniumHelper.realisticDelay();
        logger.info("Clicked quantity down button once");

        // Verify updated quantity
        // Re-locate the quantity field before fetching the updated value
        quantityField = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".qty-input")));

        int finalQuantity = Integer.parseInt(quantityField.getAttribute("value"));
        logger.info("Final quantity in cart: " + finalQuantity);

        // Adjust the assertion to match the expected quantity after updates
        Assert.assertEquals(finalQuantity, expectedQuantity + 2, "Quantity update via buttons did not work correctly!");
    }

    @Test(groups = {"functional"}, description = "TC-070")
    public void verifyCartSummaryDisplaysCorrectPricing() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Step 1: Add multiple products to the cart
        String[] productUrls = {
                "https://demo.nopcommerce.com/lenovo-ideacentre",
                "https://demo.nopcommerce.com/adobe-photoshop"
        };
        String[] productIds = {"3", "10"};
        String[] productNames = {"Lenovo IdeaCentre", "Adobe Photoshop"};

        for (int i = 0; i < productUrls.length; i++) {
            driver.get(productUrls[i]);
            logger.info("Navigating to product page: " + productUrls[i]);

            SeleniumHelper.realisticDelay();
            WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-" + productIds[i])));
            addToCartButton.click();
            logger.info("Clicked 'Add to Cart' button for product: " + productNames[i]);

            SeleniumHelper.realisticDelay();
            WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bar-notification.success")));
            Assert.assertTrue(successMessage.isDisplayed(), productNames[i] + " was not added to the cart!");
            logger.info(productNames[i] + " successfully added to cart.");
        }

        // Step 2: Navigate to cart page
        driver.get("https://demo.nopcommerce.com/cart");
        logger.info("Navigating to cart page.");

        SeleniumHelper.realisticDelay();

        // Step 3: Retrieve and verify cart summary
        WebElement subtotalElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-total .order-subtotal")));
        WebElement taxElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-total .tax-value")));
        WebElement totalElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-total .order-total")));

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
        logger.info("Cart summary displays correct pricing.");

    }

    @Test(groups = {"ui"}, description = "TC-077")
    public void verifyEmptyCartMessage() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Step 1: Navigate to cart page
        driver.get(cartUrl);
        logger.info("Navigating to cart page.");

        // Step 2: Verify if the cart is empty and the message is displayed
        SeleniumHelper.realisticDelay();

        WebElement emptyCartMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".no-data")));
        String message = emptyCartMessage.getText().trim();

        // Step 3: Assert that the message "Your Shopping Cart is empty!" is displayed
        Assert.assertEquals(message, "Your Shopping Cart is empty!", "The empty cart message is not displayed correctly!");
        logger.info("Verified that the empty cart message is displayed: " + message);
    }

    @Test(groups = {"functional"}, description = "TC-079")
    public void verifyAddToWishlist() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Step 1: Navigate to the product page
        String productUrl = "https://demo.nopcommerce.com/lenovo-ideacentre";
        driver.get(productUrl);
        logger.info("Navigating to product page: " + productUrl);

        // Step 2: Click the 'Add to Wishlist' button
        SeleniumHelper.realisticDelay();
        WebElement addToWishlistButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("add-to-wishlist-button")));
        addToWishlistButton.click();
        logger.info("Clicked 'Add to Wishlist' button.");

        // Step 3: Verify that the product was successfully added to the wishlist
        SeleniumHelper.realisticDelay();
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bar-notification.success")));
        Assert.assertTrue(successMessage.isDisplayed(), "Product was not added to the wishlist!");
        logger.info("Product successfully added to the wishlist.");

        // Step 4: Navigate to the wishlist page
        driver.get(wishlistUrl);
        logger.info("Navigating to wishlist page.");

        // Step 5: Verify that the product appears in the wishlist
        SeleniumHelper.realisticDelay();
        WebElement wishlistProduct = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[@class='product-name' and contains(text(), 'Lenovo IdeaCentre')]")));

        // Step 6: Verify the product is present in the wishlist
        Assert.assertTrue(wishlistProduct.isDisplayed(), "Lenovo IdeaCentre product not found in the wishlist!");
        logger.info("Product 'Lenovo IdeaCentre' found in the wishlist.");

        // Step 7: Locate and click the 'Add to Cart' checkbox using cssSelector
        SeleniumHelper.realisticDelay();
        WebElement addToCartCheckbox = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".add-to-cart input[type='checkbox']"))); // Targeting checkbox type within the 'add-to-cart' td

        // Scroll the element into view to ensure it is visible and clickable
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", addToCartCheckbox);

        // Click the checkbox
        addToCartCheckbox.click();
        logger.info("Clicked the 'Add to Cart' checkbox.");

        // Step 8: Click the 'Add to Cart' button below the wishlist
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".wishlist-add-to-cart-button"))); // The button inside the wishlist to add to cart
        addToCartButton.click();
        logger.info("Clicked the 'Add to Cart' button.");

        // Step 9: Verify the product is now in the cart
        driver.get(cartUrl);
        SeleniumHelper.realisticDelay();
        WebElement cartProduct = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[@class='product-name' and contains(text(), 'Lenovo IdeaCentre')]")));

        // Step 10: Assert the product is in the cart
        Assert.assertTrue(cartProduct.isDisplayed(), "Product 'Lenovo IdeaCentre' was not added to the cart!");
        logger.info("Product 'Lenovo IdeaCentre' successfully added to the cart.");
    }

    @Test(groups = {"ui"}, description = "TC-182")
    public void verifyCartIconUpdatesCount() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        final String productUrl = "https://demo.nopcommerce.com/lenovo-ideacentre";
        final String productUrl2 = "https://demo.nopcommerce.com/portable-sound-speakers";

        // Step 1: Navigate to the first product page
        driver.get(productUrl);
        logger.info("Navigating to product page: " + productUrl);

        // Step 2: Add first product to the cart
        SeleniumHelper.realisticDelay();
        WebElement addToCartButton1 = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".add-to-cart-button")));
        addToCartButton1.click();
        logger.info("First product added to cart.");

        // Step 3: Wait for the cart icon to update
        SeleniumHelper.realisticDelay();
        WebElement cartIcon = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-qty")));
        String cartCountBefore = cartIcon.getText();  // Get cart count before
        cartCountBefore = cartCountBefore.replaceAll("[^0-9]", "");  // Remove non-numeric characters (like parentheses)

        // Step 4: Verify the cart icon count (it should be greater than 0 after adding first product)
        Assert.assertTrue(Integer.parseInt(cartCountBefore) > 0, "Cart icon count did not update correctly after adding first product.");
        logger.info("Cart icon count after adding first product: " + cartCountBefore);

        // Step 5: Navigate to the second product page
        driver.get(productUrl2);
        logger.info("Navigating to second product page: " + productUrl2);

        // Step 6: Add second product to the cart
        SeleniumHelper.realisticDelay();
        WebElement addToCartButton2 = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".add-to-cart-button")));
        addToCartButton2.click();
        logger.info("Second product added to cart.");

        // Step 7: Wait and check the cart icon multiple times until it updates
        int retries = 5;  // Retry 5 times
        String cartCountAfterSecondProduct = cartCountBefore;
        for (int i = 0; i < retries; i++) {
            cartIcon = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-qty"))); // Fresh reference
            cartCountAfterSecondProduct = cartIcon.getText();  // Get updated cart count
            cartCountAfterSecondProduct = cartCountAfterSecondProduct.replaceAll("[^0-9]", "");  // Remove non-numeric characters (like parentheses)

            // Check if the cart count has updated
            if (Integer.parseInt(cartCountAfterSecondProduct) > Integer.parseInt(cartCountBefore)) {
                break;  // Exit loop once the count has incremented
            }
            Thread.sleep(1000);  // Wait 1 second before retrying
        }

        // Step 8: Verify the cart icon count (it should be greater than the previous count after adding second product)
        Assert.assertTrue(Integer.parseInt(cartCountAfterSecondProduct) > Integer.parseInt(cartCountBefore),
                "Cart icon count did not increment correctly after adding second product.");
        logger.info("Cart icon count after adding second product: " + cartCountAfterSecondProduct);

        // Optional Step 9: Navigate to the cart page and verify that both products are in the cart
        driver.get(cartUrl);
        SeleniumHelper.realisticDelay();
        WebElement cartProduct1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(), 'Lenovo IdeaCentre')]")));
        SeleniumHelper.realisticDelay();
        WebElement cartProduct2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(), 'Portable Sound Speakers')]")));

        // Verify both products are in the cart
        Assert.assertTrue(cartProduct1.isDisplayed(), "First product not found in the cart!");
        Assert.assertTrue(cartProduct2.isDisplayed(), "Second product not found in the cart!");

        logger.info("Both products found in the cart.");
    }



}