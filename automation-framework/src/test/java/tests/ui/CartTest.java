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
import java.util.List;
import java.util.ArrayList;

public class CartTest extends BaseTest {
    private final String url = ConfigReader.getProperty("baseURL");
    private final String loginUrl = ConfigReader.getProperty("loginURL");
    private final String cartUrl = ConfigReader.getProperty("cartURL");

    @Test(groups = {"smoke"}, description = "TC-015 - Verify cart persistence after page refresh")
    public void testCartPersistence() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Step 1: Navigate to the product page
        String productUrl = "https://demo.nopcommerce.com/adobe-photoshop";
        driver.get(productUrl);
        logger.info("Navigating to product page: " + productUrl);

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

        // Step 5: Refresh the page
        driver.navigate().refresh();
        logger.info("Page refreshed to check cart persistence.");

        // Step 6: Re-check cart quantity
        cartQty = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-qty")));
        String newQtyText = cartQty.getText().replaceAll("[^0-9]", "");
        Assert.assertEquals(newQtyText, qtyText, "Cart persistence failed after refresh.");
        logger.info("Cart persistence verified successfully after refresh.");
    }

    
}