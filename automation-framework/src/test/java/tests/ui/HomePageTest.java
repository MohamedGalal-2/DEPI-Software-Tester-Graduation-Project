package tests.ui;

import org.openqa.selenium.*;
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

public class HomePageTest extends BaseTest {

    // URLs
    private final String url = ConfigReader.getProperty("baseURL");
    private final String loginUrl = ConfigReader.getProperty("loginURL");
    private final String cartUrl = ConfigReader.getProperty("cartURL");
    private final String wishlistUrl = ConfigReader.getProperty("wishlistURL");
    private final String photoshopUrl = ConfigReader.getProperty("adobePhotoshopUrl");
    private final String iphoneUrl = ConfigReader.getProperty("appleIphoneUrl");
    private final String soundForgeUrl = "https://demo.nopcommerce.com/sound-forge-pro-recurring";

    @Test(groups = {"smoke"}, description = "TC-001")
    public void verifyHomePageLoads() {
        SeleniumHelper helper = new SeleniumHelper(driver);

        logger.info("Navigating to: " + url);
        driver.get(url);

        // Get timeout from properties file
        int timeout = ConfigReader.getIntProperty("explicitWait");

        // Wait for title and get the actual title
        helper.waitForTitleContains("nopCommerce", timeout);
        String actualTitle = helper.getPageTitle();

        logger.info("Actual Page Title: " + actualTitle);

        // Verify page title from properties
        Assert.assertTrue(actualTitle.contains(ConfigReader.getProperty("homePageTitle")),
                "Homepage title is incorrect!");
    }

    @Test(groups = {"ui"}, description = "TC-342")
    public void verifyHeaderLinks() throws InterruptedException {
        int timeout = ConfigReader.getIntProperty("explicitWait");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        SeleniumHelper helper = new SeleniumHelper(driver);

        logger.info("Navigating to: " + url);
        driver.get(url);

        // Fetch header links from properties
        By[] headerLinks = {
                helper.getByLocator("registerLink"),
                helper.getByLocator("loginLink"),
                helper.getByLocator("wishlistLink"),
                helper.getByLocator("cartLink")
        };

        String[] linkNames = {"Register", "Login", "Wishlist", "Shopping Cart"};

        for (int i = 0; i < headerLinks.length; i++) {
            By locator = headerLinks[i];
            String linkName = linkNames[i];

            logger.info("Verifying " + linkName + " link...");

            SeleniumHelper.realisticDelay();
            WebElement link = helper.waitForClickable(locator);
            Assert.assertTrue(link.isDisplayed(), linkName + " link is not displayed!");

            link.click();

            SeleniumHelper.realisticDelay();
            wait.until(ExpectedConditions.stalenessOf(link));

            List<WebElement> pageElements = driver.findElements(By.cssSelector("body *"));
            Assert.assertFalse(pageElements.isEmpty(), linkName + " page did not load properly!");

            logger.info(linkName + " link verification successful!");
        }

        logger.info("All header links are verified and clickable!");
    }

    @Test(groups = {"functional"}, description = "TC-046")
    public void verifyCategoryNavigation() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        String category = "category.electronics";
        String subCategory = "subcategory.cellPhones";

        // Step 1: Select a category from the homepage
        main.selectCategory(category);

        // Step 2: Select a sub-category from the category page
        main.selectSubCategory(subCategory);
    }

    @Test(groups = {"functional"}, description = "TC-261")
    public void verifyBreadcrumbNavigation() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        // Step 1: Navigate to the product page
        driver.get(soundForgeUrl);
        logger.info("Navigated to product page: Sound Forge Pro (recurring)");

        // Expected breadcrumb links in order
        String[] expectedBreadcrumbs = {"Home", "Computers", "Software"};

        // Navigate through the breadcrumbs
        main.breadcrumbNavigation(expectedBreadcrumbs);

    }

    @Test(groups = {"functional"}, description = "TC-262")
    public void validateHomepageRedirection() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);

        // Step 1: Navigate to any page other than homepage
        driver.get("https://demo.nopcommerce.com/computers");
        logger.info("Navigated to the Computers category page.");

        // Step 2: Locate and click on the homepage link (logo)
        WebElement homeLogo = helper.waitForClickable(helper.getByLocator("homeLogo"));
        homeLogo.click();
        logger.info("Clicked on the homepage logo.");

        // Step 3: Wait for the homepage to load and verify redirection
        helper.realisticDelay();
        Assert.assertEquals(driver.getCurrentUrl(), "https://demo.nopcommerce.com/", "Homepage redirection failed!");
        logger.info("Successfully redirected to the homepage.");
    }
}
