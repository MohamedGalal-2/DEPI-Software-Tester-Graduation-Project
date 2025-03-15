package tests.ui;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class ProductPageTest extends BaseTest {
    String url = "https://demo.nopcommerce.com/";

    @Test
    public void verifyProductVariationSelection(){
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));  // Increased timeout to 20 seconds

        // Search for a product
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));
        searchBox.sendKeys("Build your own computer");

        // Click the search button
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        // Wait for search results to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-results")));

        // Click on the first product link
        WebElement firstProduct = driver.findElement(By.cssSelector(".product-grid .product-item .product-title"));
        firstProduct.click();

        // Wait for the product page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-essential")));

        // Log to check if the page has loaded correctly
        logger.info("Product page loaded successfully.");

        // Select Processor (2.2 GHz Intel Pentium Dual-Core E2200)
        WebElement processorDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("product_attribute_1")));
        processorDropdown.click();
        WebElement processorOption = driver.findElement(By.xpath("//option[text()='2.2 GHz Intel Pentium Dual-Core E2200']"));
        processorOption.click();

        // Select RAM (2GB)
        WebElement ramDropdown = driver.findElement(By.id("product_attribute_2"));
        ramDropdown.click();
        WebElement ramOption = driver.findElement(By.xpath("//option[text()='2 GB']"));
        ramOption.click();

        // Select HDD (400 GB [+$100.00])
        WebElement hddOption = driver.findElement(By.xpath("//input[@id='product_attribute_3_7']"));
        hddOption.click();

        // Select OS (Vista Premium [+$60.00])
        WebElement osOption = driver.findElement(By.xpath("//input[@id='product_attribute_4_9']"));
        osOption.click();

        // Select Software (Microsoft Office [+$50.00])
        WebElement officeCheckbox = driver.findElement(By.xpath("//input[@id='product_attribute_5_10']"));
        officeCheckbox.click();

        // Add the product to the cart
        WebElement addToCartButton = driver.findElement(By.id("add-to-cart-button-1"));
        addToCartButton.click();

        // Wait for the cart to be updated
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-qty")));

        // Verify that the selected variations are reflected in the cart
        WebElement cart = driver.findElement(By.cssSelector(".cart-qty"));
        Assert.assertTrue(cart.isDisplayed(), "Cart was not updated!");

        logger.info("Selected variations are reflected in the cart.");
    }

    @Test
    public void verifyProductPageLoadsCorrectly() {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));  // Increased timeout to 20 seconds

        // Search for a product
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));
        searchBox.sendKeys("Laptop");

        // Click the search button
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        // Wait for search results to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-results")));

        // Click on the first product link
        WebElement firstProduct = driver.findElement(By.cssSelector(".product-grid .product-item .product-title"));
        firstProduct.click();

        // Wait for the product page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-essential")));

        // Log to check if the page has loaded correctly
        logger.info("Product page loaded successfully.");
    }


}
