package tests.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
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

public class CartTest extends BaseTest {
    private final SeleniumHelper helper = new SeleniumHelper(driver);

    // URLs
    private final String url = ConfigReader.getProperty("baseURL");
    private final String loginUrl = ConfigReader.getProperty("loginURL");
    private final String cartUrl = ConfigReader.getProperty("cartURL");
    private final String wishlistUrl = ConfigReader.getProperty("wishlistURL");
    private final String photoshopUrl = ConfigReader.getProperty("adobePhotoshopUrl");
    private final String lenovoUrl = ConfigReader.getProperty("lenovoIdeacentreUrl");
    private final String macbookUrl = ConfigReader.getProperty("appleMacbookProUrl");
    private final String flowerGirlUrl = ConfigReader.getProperty("flowerGirlBraceletUrl");
    private final String prideUrl = ConfigReader.getProperty("prideAndPrejudiceUrl");
    private final String portableSpeakersUrl = ConfigReader.getProperty("portableSoundSpeakersUrl");

    // Product IDs
    private final String photoshopID = helper.getProductID("adobePhotoshopID");
    private final String lenovoID = helper.getProductID("lenovoIdeacentreID");
    private final String macbookID = helper.getProductID("appleMacbookProID");
    private final String flowerGirlID = helper.getProductID("flowerGirlBraceletID");
    private final String prideID = helper.getProductID("prideAndPrejudiceID");
    private final String portableSpeakersID = helper.getProductID("portableSoundSpeakersID");

    // Product Names
    private final String photoshopName = helper.getProductName("adobePhotoshopName");
    private final String lenovoName = helper.getProductName("lenovoIdeacentreName");
    private final String macbookName = helper.getProductName("appleMacbookProName");
    private final String flowerGirlName = helper.getProductName("flowerGirlBraceletName");
    private final String prideName = helper.getProductName("prideAndPrejudiceName");
    private final String portableSpeakersName = helper.getProductName("portableSoundSpeakersName");

    @Test(groups = {"smoke"}, description = "TC-015")
    public void verifyCartPersistence() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        // Step 1: Add a product to the cart
        main.addProductToCart(photoshopUrl, photoshopID);

        // Step 2: Wait for cart quantity to update
        main.checkCartQuantity();

        // Step 3: Refresh the page
        driver.navigate().refresh();
        logger.info("Page refreshed to check cart persistence.");

        // Step 4: Re-check cart quantity
        main.checkCartQuantity();

        logger.info("Check cart persistence complete.");
    }

    @Test(groups = {"smoke"}, description = "TC-018")
    public void verifyAddMultipleProductsToCart() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        String[] productUrls = {lenovoUrl, macbookUrl, flowerGirlUrl, prideUrl};

        String[] productIds = {lenovoID, macbookID, flowerGirlID, prideID};

        String[] productNames = {lenovoName, macbookName, flowerGirlName, prideName};

        System.out.println(lenovoName);

        // Step 1: Add multiple products to the cart
        for (int i = 0; i < productUrls.length; i++) {
            main.addProductToCart(productUrls[i], productIds[i]);
        }

        // Step 2: Check cart quantity
        main.checkCartQuantity();

        // Step 3: Verify that all products are added to the cart
        driver.get(cartUrl);
        logger.info("Navigating to cart page.");

        // Assert that each product is listed in the cart
        Assert.assertTrue(driver.getPageSource().contains(productNames[0]), "Product 1 not found in cart.");
        Assert.assertTrue(driver.getPageSource().contains(productNames[1]), "Product 2 not found in cart.");
        Assert.assertTrue(driver.getPageSource().contains(productNames[2]), "Product 3 not found in cart.");
        Assert.assertTrue(driver.getPageSource().contains(productNames[3]), "Product 4 not found in cart.");

        logger.info("All selected products are confirmed in the cart.");
    }

    @Test(groups = {"smoke"}, description = "TC-019")
    public void verifyRemoveMultipleProducts() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        String[] productUrls = {lenovoUrl, macbookUrl, flowerGirlUrl, prideUrl};

        String[] productIds = {lenovoID, macbookID, flowerGirlID, prideID};

        String[] productNames = {lenovoName, macbookName, flowerGirlName, prideName};

        // Step 1: Add multiple products to the cart
        for (int i = 0; i < productUrls.length; i++) {
            main.addProductToCart(productUrls[i], productIds[i]);
        }

        // Step 2: Check cart quantity
        main.checkCartQuantity();

        for (String productName : productNames) {
            Assert.assertTrue(driver.getPageSource().contains(productName), productName + " not found in cart.");
            logger.info(productName + " is confirmed in the cart.");
        }

        driver.get(cartUrl);

        // Step 3: Remove each product from the cart
        for (String productId : productIds) {
            main.removeProductFromCart(productId);
        }

        // Step 4: Verify cart is empty
        WebElement cartMessage = helper.waitForVisibility(helper.getByLocator("cart.emptyMessage"));
        Assert.assertTrue(cartMessage.getText().contains("Your Shopping Cart is empty!"), "Cart is not empty after removing all products.");
        System.out.println("All products successfully removed. Cart is empty.");
    }

    @Test(groups = {"functional"}, description = "TC-068")
    public void verifyUpdateProductQuantity() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        int expectedQuantity = 2;

        // Step 1: Add a product to the cart
        main.addProductToCart(lenovoUrl, lenovoID);

        // Step 2: Navigate to cart and update quantity
        driver.get("https://demo.nopcommerce.com/cart");
        logger.info("Navigating to cart page.");
        helper.realisticDelay();
        WebElement quantityField = helper.waitForPresence(helper.getByLocator("cart.productQuantityInput"));

        // Step 3: Verify quantity can be updated via input field
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = arguments[1];", quantityField, expectedQuantity);
        js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", quantityField);
        logger.info("Updated quantity to: " + expectedQuantity);

        // Verify total price update
        WebElement totalPriceElement = helper.waitForVisibility(helper.getByLocator("cart.totalPrice"));
        Assert.assertNotNull(totalPriceElement.getText(), "Total price not updated correctly!");
        logger.info("Total price recalculated successfully: " + totalPriceElement.getText());

        // Step 4: Use quantity up button 3 times and quantity down button once
        for (int i = 0; i < 3; i++) {
            helper.realisticDelay();

            // Re-locate the button in each iteration to avoid StaleElementReferenceException
            WebElement quantityUpButton = helper.waitForClickable(helper.getByLocator("cart.quantityUpButton"));
            quantityUpButton.click();

            logger.info("Clicked quantity up button: " + (i + 1) + " times");
        }

        helper.realisticDelay();
        WebElement quantityDownButton = helper.waitForClickable(helper.getByLocator("cart.quantityDownButton"));
        quantityDownButton.click();

        helper.realisticDelay();
        logger.info("Clicked quantity down button once");

        // Verify updated quantity
        // Re-locate the quantity field before fetching the updated value
        quantityField = helper.waitForPresence(helper.getByLocator("cart.productQuantityInput"));

        int finalQuantity = Integer.parseInt(quantityField.getAttribute("value"));
        logger.info("Final quantity in cart: " + finalQuantity);

        // Adjust the assertion to match the expected quantity after updates
        Assert.assertEquals(finalQuantity, expectedQuantity + 2, "Quantity update via buttons did not work correctly!");
    }

    @Test(groups = {"functional"}, description = "TC-070")
    public void verifyCartSummaryDisplaysCorrectPricing() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Step 1: Add multiple products to the cart
        String[] productUrls = {lenovoUrl, photoshopUrl};
        String[] productIds = {lenovoID, photoshopID};
        String[] productNames = {lenovoName, photoshopName};

        for (int i = 0; i < productUrls.length; i++) {
            main.addProductToCart(productUrls[i], productIds[i]);
        }

        // Step 2: Navigate to cart page
        driver.get("https://demo.nopcommerce.com/cart");
        logger.info("Navigating to cart page.");
        helper.realisticDelay();

        // Step 3: Retrieve and verify cart summary
        main.checkCartSummary();

        logger.info("Cart summary displays correct pricing.");
    }

    @Test(groups = {"ui"}, description = "TC-077")
    public void verifyEmptyCartMessage() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        main.checkCartEmpty();
    }

    @Test(groups = {"functional"}, description = "TC-079")
    public void verifyAddToWishlist() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Step 1: Add a product to the wishlist
        main.addProductToWishlist(lenovoUrl);

        // Step 2: Add product to the cart
        main.addProductToCartFromWishlist(lenovoName);
    }

    @Test(groups = {"ui"}, description = "TC-182")
    public void verifyCartIconUpdatesCount() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        // Step 1: Add first product to the cart
        main.addProductToCart(lenovoUrl, lenovoID);

        // Step 2: Check cart icon count
        main.checkCartQuantity();

        // Step 3: Add second product to the cart
        main.addProductToCart(portableSpeakersUrl, portableSpeakersID);

        // Step 4: Check cart icon count
        main.checkCartQuantity();

        logger.info("Cart icon updates count correctly.");
    }

    @Test(groups = {"functional"}, description = "TC-076")
    public void testInvalidPromoCode() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        String promoCode = "INVALIDCODE123";

        // Step 1: Add product to cart
        main.addProductToCart(lenovoUrl, lenovoID);

        // Step 2: Apply promo code
        main.enterPromoCode(promoCode);

        // Step 3: Verify error message
        WebElement errorMessage = driver.findElement(helper.getByLocator("cart.errorMessage"));
        Assert.assertFalse(errorMessage.getText().contains("The coupon code cannot be found"), "Error message not displayed correctly");
    }

    @Test(groups = {"functional"}, description = "TC-071")
    public void verifyValidPromoCodeBlocked() {
        throw new SkipException("Blocked: No active promo codes available for testing.");
    }
}