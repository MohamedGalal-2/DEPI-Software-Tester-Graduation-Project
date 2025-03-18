package tests.ui;

import utils.SeleniumHelper;
import base.BaseTest;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;

public class ProductPageTest extends BaseTest {
    String url = "https://demo.nopcommerce.com/";

    @Test(groups = {"smoke"}, description = "TC-020")
    public void verifyProductVariationSelection() throws InterruptedException {
        logger.info("Navigating directly to the product page.");
        driver.get("https://demo.nopcommerce.com/build-your-own-computer");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        SeleniumHelper helper = new SeleniumHelper(driver);

        logger.info("Product page loaded successfully.");

        List<String> expectedOptions = List.of(
                "Processor: 2.5 GHz Intel Pentium Dual-Core E2200 [+$15.00]",
                "RAM: 4GB [+$20.00]",
                "HDD: 400 GB [+$100.00]",
                "OS: Vista Premium [+$60.00]",
                "Software: Microsoft Office [+$50.00]"
        );

        List<String> selectedOptions = new ArrayList<>();

        // Select Processor
        SeleniumHelper.realisticDelay();
        WebElement processorDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("product_attribute_1")));
        helper.scrollToElement(processorDropdown);
        helper.realisticDelay();
        js.executeScript("arguments[0].value='2'; arguments[0].dispatchEvent(new Event('change'))", processorDropdown);
        selectedOptions.add("Processor: 2.5 GHz Intel Pentium Dual-Core E2200 [+$15.00]");
        helper.realisticDelay();

        // Select RAM
        WebElement ramDropdown = driver.findElement(By.id("product_attribute_2"));
        helper.scrollToElement(ramDropdown);
        helper.realisticDelay();
        js.executeScript("arguments[0].value='4'; arguments[0].dispatchEvent(new Event('change'))", ramDropdown);
        selectedOptions.add("RAM: 4GB [+$20.00]");
        helper.realisticDelay();

        // Select HDD (400GB)
        WebElement hddOption = driver.findElement(By.xpath("//input[@id='product_attribute_3_7']")); // Correct ID for 400GB
        helper.scrollToElement(hddOption);
        helper.realisticDelay();
        if (!hddOption.isSelected()) {
            js.executeScript("arguments[0].click(); arguments[0].dispatchEvent(new Event('change'))", hddOption);
        }
        selectedOptions.add("HDD: 400 GB [+$100.00]");
        helper.realisticDelay();

        // Select OS (Vista Premium)
        WebElement osOption = driver.findElement(By.xpath("//input[@id='product_attribute_4_9']")); // Correct ID for Vista Premium
        helper.scrollToElement(osOption);
        helper.realisticDelay();
        if (!osOption.isSelected()) {
            js.executeScript("arguments[0].click(); arguments[0].dispatchEvent(new Event('change'))", osOption);
        }
        selectedOptions.add("OS: Vista Premium [+$60.00]");
        helper.realisticDelay();

        // Select Software
        WebElement officeCheckbox = driver.findElement(By.xpath("//input[@id='product_attribute_5_10']"));
        helper.scrollToElement(officeCheckbox);
        helper.realisticDelay();
        if (!officeCheckbox.isSelected()) {
            js.executeScript("arguments[0].click();", officeCheckbox);
        }
        selectedOptions.add("Software: Microsoft Office [+$50.00]");
        helper.realisticDelay();

        // Add to Cart
        WebElement addToCartButton = driver.findElement(By.id("add-to-cart-button-1"));
        helper.scrollToElement(addToCartButton);
        helper.realisticDelay();
        js.executeScript("arguments[0].click();", addToCartButton);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-qty")));
        logger.info("Product added to the cart successfully.");

        // Open the Cart
        SeleniumHelper.realisticDelay();
        WebElement cartLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Shopping cart")));
        cartLink.click();

        // Wait for the cart table to be visible
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".table-wrapper")));

        // Find all rows inside the cart table
        List<WebElement> cartItems = driver.findElements(By.cssSelector(".cart tbody tr"));

        if (cartItems.isEmpty()) {
            logger.error("No items found in the cart!");
            Assert.fail("No items were retrieved from the cart.");
        }

        boolean matchFound = false;

        // Extract details for each item in the cart
        for (WebElement cartItem : cartItems) {
            String sku = cartItem.findElement(By.cssSelector(".sku .sku-number")).getText().trim();
            String productName = cartItem.findElement(By.cssSelector(".product-name")).getText().trim();
            String unitPrice = cartItem.findElement(By.cssSelector(".unit-price .product-unit-price")).getText().trim();
            String quantity = cartItem.findElement(By.cssSelector(".quantity input")).getAttribute("value").trim();
            String total = cartItem.findElement(By.cssSelector(".subtotal .product-subtotal")).getText().trim();
            String attributes = cartItem.findElement(By.cssSelector(".attributes")).getText().trim();

            logger.info("SKU: " + sku);
            logger.info("Product Name: " + productName);
            logger.info("Attributes: " + attributes);
            logger.info("Unit Price: " + unitPrice);
            logger.info("Quantity: " + quantity);
            logger.info("Total: " + total);

            // Verify that the selected attributes match what is in the cart
            List<String> extractedAttributes = List.of(attributes.split("\n"));
            if (selectedOptions.equals(extractedAttributes)) {
                matchFound = true;
            }
        }

        // Assertion: Ensure selected options match what is in the cart
        Assert.assertTrue(matchFound, "The selected options do not match the cart!");

        // Final log message
        logger.info("Cart validation successful! Product attributes match the selection.");
    }

    @Test(groups = {"smoke"}, description = "TC-003")
    public void verifyProductPageLoadsCorrectly() throws InterruptedException {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Locate the search bar and input a search term
        SeleniumHelper.realisticDelay();
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));
        searchBox.sendKeys("Laptop");

        // Click the search button
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        // Wait for search results to load
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-results")));

        // Verify search results are displayed
        WebElement searchResults = driver.findElement(By.cssSelector(".search-results"));
        Assert.assertTrue(searchResults.isDisplayed(), "Search results are not displayed!");

        logger.info("Product page loaded successfully.");
    }

    @Test(groups = {"ui"}, description = "TC-063")
    public void verifyRelatedProductsAreDisplayed() throws InterruptedException {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));  // Increased timeout to 20 seconds

        // Search for a product
        SeleniumHelper.realisticDelay();
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));
        searchBox.sendKeys("Build your own computer");

        // Click the search button
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        // Wait for search results to load
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-results")));

        // Click on the first product link
        SeleniumHelper.realisticDelay();
        SeleniumHelper.realisticDelay();
        WebElement firstProduct = driver.findElement(By.cssSelector(".item-box .product-title a"));
        firstProduct.click();

        // Wait for the product page to load
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-essential")));

        // Log to check if the page has loaded correctly
        logger.info("Product page loaded successfully.");

        // Verify the Related Products section exists
        SeleniumHelper.realisticDelay();
        WebElement relatedProductsSection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".item-grid")));
        Assert.assertTrue(relatedProductsSection.isDisplayed(), "Related products section is NOT displayed!");

        // Verify that at least one related product is present
        List<WebElement> relatedProducts = relatedProductsSection.findElements(By.cssSelector(".item-box"));
        Assert.assertFalse(relatedProducts.isEmpty(), "No related products found!");

        // Log the related products found
        logger.info("Number of related products displayed: " + relatedProducts.size());

        // Verify each related product has a title
        for (WebElement product : relatedProducts) {
            WebElement productTitle = product.findElement(By.cssSelector(".product-title a"));
            Assert.assertTrue(productTitle.isDisplayed(), "A related product does not have a title displayed!");
            logger.info("Related Product Found: " + productTitle.getText());
        }

        logger.info("Related products are displayed successfully.");
    }

    @Test(groups = {"ui"}, description = "TC-179")
    public void verifyProductImageGallery() throws InterruptedException {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Search for a product
        SeleniumHelper.realisticDelay();
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));
        searchBox.sendKeys("Build your own computer");

        // Click the search button
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        // Wait for search results to load
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-results")));

        // Click on the first product link
        SeleniumHelper.realisticDelay();
        WebElement firstProduct = driver.findElement(By.cssSelector(".item-box .product-title a"));
        firstProduct.click();

        // Wait for the product page to load
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-essential")));

        // Locate main product image and thumbnails
        WebElement mainProductImage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("main-product-img-1")));
        List<WebElement> thumbItems = driver.findElements(By.cssSelector(".picture-thumbs .thumb-item"));

        Assert.assertFalse(thumbItems.isEmpty(), "No thumbnail images found!");

        // Click on the second thumbnail image using JavaScript
        WebElement secondThumbnail = thumbItems.get(1);
        js.executeScript("arguments[0].click();", secondThumbnail);

        // Debugging
        System.out.println("Before click: " + mainProductImage.getAttribute("src"));

        // Wait for the image to update
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.attributeContains(mainProductImage, "src", "_550.jpeg"));

        // Debugging
        System.out.println("After click: " + mainProductImage.getAttribute("src"));

        // Validate image update
        String updatedImageSrc = mainProductImage.getAttribute("src");
        String expectedDefaultSizeSrc = secondThumbnail.findElement(By.tagName("img")).getAttribute("data-defaultsize");

        Assert.assertTrue(updatedImageSrc.contains("_550.jpeg"), "Main product image did not update correctly!");

        logger.info("Product image gallery allows browsing images successfully.");
    }

    @Test(groups = {"ui"}, description = "TC-180")
    public void verifyProductReviewsAreDisplayed() throws InterruptedException {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Search for a product
        SeleniumHelper.realisticDelay();
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));
        searchBox.sendKeys("Build your own computer");

        // Click the search button
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        // Wait for search results to load
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-results")));

        // Click on the first product link
        SeleniumHelper.realisticDelay();
        WebElement firstProduct = driver.findElement(By.cssSelector(".item-box .product-title a"));
        firstProduct.click();

        // Wait for the product page to load
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-essential")));

        // Log to check if the page has loaded correctly
        logger.info("Product page loaded successfully.");

        // Scroll to the review section
        SeleniumHelper.realisticDelay();
        WebElement reviewsSection = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-review-list")));

        // Verify if the review section is displayed
        Assert.assertTrue(reviewsSection.isDisplayed(), "Reviews section is not displayed!");

        // Verify the review details
        List<WebElement> reviewItems = driver.findElements(By.cssSelector(".product-review-item"));
        Assert.assertFalse(reviewItems.isEmpty(), "No reviews found!");

        for (WebElement review : reviewItems) {
            // Verify review title
            WebElement reviewTitle = review.findElement(By.cssSelector(".review-title"));
            Assert.assertTrue(reviewTitle.isDisplayed(), "Review title is not displayed!");

            // Verify review rating
            WebElement rating = review.findElement(By.cssSelector(".rating"));
            Assert.assertTrue(rating.isDisplayed(), "Review rating is not displayed!");

            // Verify review content
            WebElement reviewContent = review.findElement(By.cssSelector(".review-text"));
            Assert.assertTrue(reviewContent.isDisplayed(), "Review content is not displayed!");

            // Verify reviewer name and date
            WebElement reviewerName = review.findElement(By.cssSelector(".user span"));
            WebElement reviewDate = review.findElement(By.cssSelector(".date span"));
            Assert.assertTrue(reviewerName.isDisplayed(), "Reviewer name is not displayed!");
            Assert.assertTrue(reviewDate.isDisplayed(), "Review date is not displayed!");
        }

        // Verify the Helpful Vote options (Yes/No)
        WebElement helpfulVoteYes = driver.findElement(By.id("vote-yes-38"));
        WebElement helpfulVoteNo = driver.findElement(By.id("vote-no-38"));
        Assert.assertTrue(helpfulVoteYes.isDisplayed(), "'Yes' vote option is not displayed!");
        Assert.assertTrue(helpfulVoteNo.isDisplayed(), "'No' vote option is not displayed!");

        logger.info("Product reviews are displayed successfully.");
    }

    @Test(groups = {"ui"}, description = "TC-199")
    public void verifyCompareListFunctionality() throws InterruptedException {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        SeleniumHelper helper = new SeleniumHelper(driver);

        // Step 1: Add first product to compare list
        logger.info("Opening Apple iPhone 16 128GB product page.");
        driver.get("https://demo.nopcommerce.com/apple-iphone-16-128gb");
        helper.randomScroll();
        SeleniumHelper.randomDelay();

        js.executeScript("document.querySelector('.compare-products .add-to-compare-list-button').click();");
        logger.info("Added Apple iPhone 16 128GB to the compare list.");
        helper.randomScroll();
        helper.randomDelay();

        // Step 2: Add second product to compare list
        Thread.sleep(1000);
        logger.info("Opening HTC One Mini Blue product page.");
        driver.get("https://demo.nopcommerce.com/htc-one-mini-blue");
        helper.randomScroll();
        helper.randomDelay();

        js.executeScript("document.querySelector('.compare-products .add-to-compare-list-button').click();");
        logger.info("Added HTC One Mini Blue to the compare list.");
        helper.randomScroll();
        helper.randomDelay();

        // Step 3: Navigate to Compare Products page
        logger.info("Navigating to Compare Products list.");
        driver.get("https://demo.nopcommerce.com/compareproducts");
        helper.randomScroll();
        helper.randomDelay();

        // Step 4: Verify Compare List Page
        SeleniumHelper.realisticDelay();
        WebElement compareTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".page-title h1")));
        Assert.assertEquals(compareTitle.getText(), "Compare products", "Compare page title is incorrect!");

        // Step 5: Verify Products in the Compare List
        List<WebElement> comparedProducts = driver.findElements(By.cssSelector(".compare-products-table .product-name a"));

        List<String> productNames = comparedProducts.stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());

        System.out.println("Extracted Products: " + productNames);

        // Check if both expected products exist in the compare list, regardless of order
        Assert.assertTrue(productNames.contains("Apple iPhone 16 128GB"), "Apple iPhone not found in compare list!");
        Assert.assertTrue(productNames.contains("HTC One Mini Blue"), "HTC One Mini Blue not found in compare list!");

        logger.info("Compare list verification successful.");
    }

}
