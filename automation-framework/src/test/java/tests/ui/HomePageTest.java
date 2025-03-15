package tests.ui;

import base.BaseTest;
import utils.SeleniumHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomePageTest extends BaseTest {
    private final String url = "https://demo.nopcommerce.com/";

    @Test
    public void verifyHomePageLoads() {
        logger.info("Navigating to: " + url);
        driver.get(url);

        // Create an instance of SeleniumHelper
        SeleniumHelper seleniumHelper = new SeleniumHelper(driver);

        // Use SeleniumHelper to wait for title and get the actual title
        seleniumHelper.waitForTitleContains("nopCommerce", 5);
        String actualTitle = seleniumHelper.getPageTitle();

        logger.info("Actual Page Title: " + actualTitle);

        // Assert using helper method
        Assert.assertTrue(actualTitle.contains("nopCommerce demo store"), "Homepage title is incorrect!");
    }
}
