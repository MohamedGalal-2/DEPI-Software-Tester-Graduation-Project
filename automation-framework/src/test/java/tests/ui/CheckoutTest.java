package tests.ui;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.Random;

public class CheckoutTest extends BaseTest {
    String productUrl = "https://demo.nopcommerce.com/adobe-photoshop";
    String cartUrl = "https://demo.nopcommerce.com/cart";

    @Test
    public void verifyCheckoutPageLoads() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Navigate to the product page
        logger.info("Navigating to product page: " + productUrl);
        driver.get(productUrl);

        // Step 2: Add product to cart
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

    // Random delay function
    public void realisticDelay() throws InterruptedException {
        Random random = new Random();
        int delay = random.nextInt(2000) + 1000; // Delay between 1000ms and 3000ms
        Thread.sleep(delay);
    }
}
