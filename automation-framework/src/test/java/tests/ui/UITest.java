///** This is a test file that I use to test the scripts I write. **/
//
//package tests.ui;
//
//import base.BaseTest;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.JavascriptExecutor;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import java.time.Duration;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ThreadLocalRandom;
//
//public class UITest extends BaseTest {
//    String url = "https://demo.nopcommerce.com/";
//
//    @Test
//    public void verifyProductVariationSelection() throws InterruptedException {
//        logger.info("Navigating directly to the product page.");
//        driver.get("https://demo.nopcommerce.com/build-your-own-computer");
//
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//
//        logger.info("Product page loaded successfully.");
//
//        List<String> expectedOptions = List.of(
//                "Processor: 2.5 GHz Intel Pentium Dual-Core E2200 [+$15.00]",
//                "RAM: 4GB [+$20.00]",
//                "HDD: 400 GB [+$100.00]",
//                "OS: Vista Premium [+$60.00]",
//                "Software: Microsoft Office [+$50.00]"
//        );
//
//        List<String> selectedOptions = new ArrayList<>();
//
//        // Select Processor
//        WebElement processorDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("product_attribute_1")));
//        scrollToElement(processorDropdown);
//        realisticDelay();
//        js.executeScript("arguments[0].value='2'; arguments[0].dispatchEvent(new Event('change'))", processorDropdown);
//        selectedOptions.add("Processor: 2.5 GHz Intel Pentium Dual-Core E2200 [+$15.00]");
//        realisticDelay();
//
//        // Select RAM
//        WebElement ramDropdown = driver.findElement(By.id("product_attribute_2"));
//        scrollToElement(ramDropdown);
//        realisticDelay();
//        js.executeScript("arguments[0].value='4'; arguments[0].dispatchEvent(new Event('change'))", ramDropdown);
//        selectedOptions.add("RAM: 4GB [+$20.00]");
//        realisticDelay();
//
//        // Select HDD (400GB)
//        WebElement hddOption = driver.findElement(By.xpath("//input[@id='product_attribute_3_7']")); // Correct ID for 400GB
//        scrollToElement(hddOption);
//        realisticDelay();
//        if (!hddOption.isSelected()) {
//            js.executeScript("arguments[0].click(); arguments[0].dispatchEvent(new Event('change'))", hddOption);
//        }
//        selectedOptions.add("HDD: 400 GB [+$100.00]");
//        realisticDelay();
//
//        // Select OS (Vista Premium)
//        WebElement osOption = driver.findElement(By.xpath("//input[@id='product_attribute_4_9']")); // Correct ID for Vista Premium
//        scrollToElement(osOption);
//        realisticDelay();
//        if (!osOption.isSelected()) {
//            js.executeScript("arguments[0].click(); arguments[0].dispatchEvent(new Event('change'))", osOption);
//        }
//        selectedOptions.add("OS: Vista Premium [+$60.00]");
//        realisticDelay();
//
//        // Select Software
//        WebElement officeCheckbox = driver.findElement(By.xpath("//input[@id='product_attribute_5_10']"));
//        scrollToElement(officeCheckbox);
//        realisticDelay();
//        if (!officeCheckbox.isSelected()) {
//            js.executeScript("arguments[0].click();", officeCheckbox);
//        }
//        selectedOptions.add("Software: Microsoft Office [+$50.00]");
//        realisticDelay();
//
//        // Add to Cart
//        WebElement addToCartButton = driver.findElement(By.id("add-to-cart-button-1"));
//        scrollToElement(addToCartButton);
//        realisticDelay();
//        js.executeScript("arguments[0].click();", addToCartButton);
//
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-qty")));
//        logger.info("Product added to the cart successfully.");
//
//        // Open the Cart
//        WebElement cartLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Shopping cart")));
//        cartLink.click();
//
//        // Wait for the cart table to be visible
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".table-wrapper")));
//
//        // Find all rows inside the cart table
//        List<WebElement> cartItems = driver.findElements(By.cssSelector(".cart tbody tr"));
//
//        if (cartItems.isEmpty()) {
//            logger.error("No items found in the cart!");
//            Assert.fail("No items were retrieved from the cart.");
//        }
//
//        boolean matchFound = false;
//
//        // Extract details for each item in the cart
//        for (WebElement cartItem : cartItems) {
//            String sku = cartItem.findElement(By.cssSelector(".sku .sku-number")).getText().trim();
//            String productName = cartItem.findElement(By.cssSelector(".product-name")).getText().trim();
//            String unitPrice = cartItem.findElement(By.cssSelector(".unit-price .product-unit-price")).getText().trim();
//            String quantity = cartItem.findElement(By.cssSelector(".quantity input")).getAttribute("value").trim();
//            String total = cartItem.findElement(By.cssSelector(".subtotal .product-subtotal")).getText().trim();
//            String attributes = cartItem.findElement(By.cssSelector(".attributes")).getText().trim();
//
//            logger.info("SKU: " + sku);
//            logger.info("Product Name: " + productName);
//            logger.info("Attributes: " + attributes);
//            logger.info("Unit Price: " + unitPrice);
//            logger.info("Quantity: " + quantity);
//            logger.info("Total: " + total);
//
//            // Verify that the selected attributes match what is in the cart
//            List<String> extractedAttributes = List.of(attributes.split("\n"));
//            if (selectedOptions.equals(extractedAttributes)) {
//                matchFound = true;
//            }
//        }
//
//        // Assertion: Ensure selected options match what is in the cart
//        Assert.assertTrue(matchFound, "The selected options do not match the cart!");
//
//        // Final log message
//        logger.info("Cart validation successful! Product attributes match the selection.");
//    }
//
//    // Random delay function
//    public void realisticDelay() throws InterruptedException {
//        int delay = ThreadLocalRandom.current().nextInt(1000, 3000); // 1s - 3s
//        Thread.sleep(delay);
//    }
//
//    public void scrollToElement(WebElement element) {
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
//    }
//}
