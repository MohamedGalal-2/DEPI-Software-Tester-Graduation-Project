package tests.ui;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;


public class HomePageTest extends BaseTest {
    String url = "https://demo.nopcommerce.com/";

    @Test
    public void verifyHomePageLoads() {

        logger.info("Navigating to: " + url);

        driver.get(url);

        // Wait for the page title to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.titleContains("nopCommerce"));

        String actualTitle = driver.getTitle();
        logger.info("Actual Page Title: " + actualTitle);

        // Use contains() to avoid exact match issues
        Assert.assertTrue(actualTitle.contains("nopCommerce demo store"), "Homepage title is incorrect!");
    }

}
