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
import java.util.*;
import java.util.stream.Collectors;

public class ProductPageTest extends BaseTest {
    private final SeleniumHelper helper = new SeleniumHelper(driver);

    // URLs
    private final String url = ConfigReader.getProperty("baseURL");
    private final String loginUrl = ConfigReader.getProperty("loginURL");
    private final String cartUrl = ConfigReader.getProperty("cartURL");
    private final String wishlistUrl = ConfigReader.getProperty("wishlistURL");
    private final String macbookUrl = ConfigReader.getProperty("appleMacbookProUrl");
    private final String computerUrl = ConfigReader.getProperty("buildYourComputerUrl");

    // Product IDs
    private final String macbookID = helper.getProductID("appleMacbookProID");
    private final String computerID = helper.getProductID("buildYourComputerID");

    // Product Names
    private final String macbookName = helper.getProductName("appleMacbookProName");
    private final String computerName = helper.getProductName("buildYourComputerName");

    @Test(groups = {"smoke"}, description = "TC-020")
    public void verifyProductVariationSelection() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        logger.info("Navigating directly to the product page.");
        driver.get(computerUrl);
        logger.info("Product page loaded successfully.");

        Map<String, String> options = new HashMap<>();
        options.put("Processor", "2"); // 2.5 GHz Intel Pentium Dual-Core E2200
        options.put("RAM", "4"); // 4GB RAM
        options.put("HDD", "product_attribute_3_7"); // 400 GB
        options.put("OS", "product_attribute_4_9"); // Vista Premium
        options.put("Software", "product_attribute_5_10"); // Microsoft Office

        Map<String, String> expectedOptionsMap = new LinkedHashMap<>();
        expectedOptionsMap.put("Processor", "Processor: 2.5 GHz Intel Pentium Dual-Core E2200 [+$15.00]");
        expectedOptionsMap.put("RAM", "RAM: 4GB [+$20.00]");
        expectedOptionsMap.put("HDD", "HDD: 400 GB [+$100.00]");
        expectedOptionsMap.put("OS", "OS: Vista Premium [+$60.00]");
        expectedOptionsMap.put("Software", "Software: Microsoft Office [+$50.00]");


        main.selectProductOptions(options);

        // Add to Cart
        WebElement addToCartButton = driver.findElement(By.id("add-to-cart-button-1"));
        helper.scrollToElement(addToCartButton);
        helper.realisticDelay();
        js.executeScript("arguments[0].click();", addToCartButton);

        helper.waitForVisibility(helper.getByLocator("cart.quantity"));
        logger.info("Product added to the cart successfully.");

        // Open the Cart and validate
        helper.realisticDelay();
        WebElement cartLink = helper.waitForClickable(helper.getByLocator("cart.name"));
        cartLink.click();
        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("cart.tableWrapper"));

        List<WebElement> cartItems = driver.findElements(helper.getByLocator("cart.body"));
        Assert.assertFalse(cartItems.isEmpty(), "No items found in the cart!");

        boolean matchFound = false;
        for (WebElement cartItem : cartItems) {
            String attributes = cartItem.findElement(helper.getByLocator("cart.attributes")).getText().trim();
            List<String> extractedAttributes = List.of(attributes.split("\n"));

            if (new ArrayList<>(expectedOptionsMap.values()).equals(extractedAttributes)) {
                matchFound = true;
                break;
            }
        }

        Assert.assertTrue(matchFound, "The selected options do not match the cart!");
        logger.info("Cart validation successful! Product attributes match the selection.");
    }

    @Test(groups = {"smoke"}, description = "TC-003")
    public void verifyProductPageLoadsCorrectly() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        logger.info("Navigating to: " + url);
        driver.get(url);

        // Use the helper function to perform the search
        main.searchProduct(macbookName);

        logger.info("Product page loaded successfully.");
    }

    @Test(groups = {"ui"}, description = "TC-063")
    public void verifyRelatedProductsAreDisplayed() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));  // Increased timeout to 20 seconds
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        logger.info("Navigating to: " + url);
        driver.get(url);

        // Use the helper function to perform the search
        main.searchProduct(macbookName);

        logger.info("Product page loaded successfully.");

        // Click on the first product link
        helper.realisticDelay();
        helper.realisticDelay();
        WebElement firstProduct = driver.findElement(helper.getByLocator("search.firstProduct"));
        firstProduct.click();

        // Wait for the product page to load
        helper.realisticDelay();
        helper.waitForVisibility(helper.getByLocator("product.Essential"));

        // Log to check if the page has loaded correctly
        logger.info("Product page loaded successfully.");

        main.relatedProducts();

        logger.info("Related products are displayed successfully.");
    }

    @Test(groups = {"ui"}, description = "TC-179")
    public void verifyProductImageGallery() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        MainFunctionalities main = new MainFunctionalities(driver);

        logger.info("Navigating to: " + url);
        driver.get(url);

        // Step 1: Go to the product page
        driver.get(computerUrl);

        // Step 2: Check image gallery
        main.clickThumbnailAndVerifyImageUpdate("product.firstImage", "product.thumb", 1, "_550.jpeg");
    }

    @Test(groups = {"ui"}, description = "TC-180")
    public void verifyProductReviewsAreDisplayed() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        logger.info("Navigating to: " + url);
        driver.get(url);

        // Step 1: Go to the product page
        driver.get(computerUrl);

        // Step 2: Check if reviews are displayed
        main.checkReviews();
    }

    @Test(groups = {"ui"}, description = "TC-199")
    public void verifyCompareListFunctionality() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        logger.info("Navigating to: " + url);
        driver.get(url);

        // Add products to the compare list
        main.addProductToCompareList("https://demo.nopcommerce.com/apple-iphone-16-128gb");
        main.addProductToCompareList("https://demo.nopcommerce.com/htc-one-mini-blue");

        // Verify products in the compare list
        List<String> expectedProducts = List.of("Apple iPhone 16 128GB", "HTC One Mini Blue");
        main.verifyCompareList(expectedProducts);
    }

    @Test(groups = {"functional"}, description = "TC-282")
    public void verifySocialMediaSharing() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        main.checkSocialMediaSharing("https://demo.nopcommerce.com/fahrenheit-451-by-ray-bradbury");
    }

}
