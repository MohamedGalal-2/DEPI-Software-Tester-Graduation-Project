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


}