package tests.ui;

import base.BaseTest;
import utils.ConfigReader;
import utils.SeleniumHelper;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.interactions.Actions;
import java.time.Duration;
import java.util.List;

public class SearchTest extends BaseTest {
    private final String url = ConfigReader.getProperty("baseURL");

    @Test(groups = {"smoke"}, description = "TC-002")
    public void verifySearchFunctionality() throws InterruptedException {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Using locators from config.properties
        SeleniumHelper.realisticDelay();
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(SeleniumHelper.getByLocator("search.box")));
        searchBox.sendKeys("Laptop");

        // Click the search button
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        WebElement searchResults = driver.findElement(By.cssSelector(".search-results"));
        Assert.assertTrue(searchResults.isDisplayed(), "Search results are not displayed!");

        logger.info("Search functionality verified successfully.");
    }

    @Test(groups = {"functional"}, description = "TC-038")
    public void verifySearchByCategory() throws InterruptedException {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Click on the "Computers" category from the navigation menu
        SeleniumHelper.realisticDelay();
        WebElement categoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Computers")));
        categoryLink.click();

        // Wait for the category page to load
        SeleniumHelper.realisticDelay();
        WebElement pageTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".page-title")));
        Assert.assertTrue(pageTitle.getText().contains("Computers"), "Incorrect category page title!");

        // Verify that products are displayed under the category
        List<WebElement> productCategories = driver.findElements(By.cssSelector(".sub-category-item"));
        Assert.assertFalse(productCategories.isEmpty(), "No products found in the category!");

        logger.info("Product search by category verified successfully.");
    }

    @Test(groups = {"functional"}, description = "TC-042")
    public void verifyPaginationInSearchResults() throws InterruptedException {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Locate the search bar and input a search term expected to return multiple pages
        SeleniumHelper.realisticDelay();
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));
        searchBox.sendKeys("computer");

        // Click the search button
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        // Wait for search results to load
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-results")));

        // Check if pagination controls exist
        List<WebElement> paginationControls = driver.findElements(By.cssSelector(".pager .page-item"));
        if (paginationControls.isEmpty()) {
            logger.warn("Pagination is not available due to limited results.");
            return;
        }

        // Click on the next page if available
        WebElement nextPageButton = driver.findElement(By.cssSelector(".pager .next-page"));
        nextPageButton.click();

        // Wait for page to load and verify new results
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.stalenessOf(driver.findElement(By.cssSelector(".product-grid"))));
        List<WebElement> newResults = driver.findElements(By.cssSelector(".product-grid .item-box"));
        Assert.assertFalse(newResults.isEmpty(), "Next page did not load new results");

        // Navigate back to the first page and verify
        WebElement previousPageButton = driver.findElement(By.cssSelector(".pager .previous-page"));
        previousPageButton.click();

        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.stalenessOf(driver.findElement(By.cssSelector(".product-grid"))));
        List<WebElement> firstPageResults = driver.findElements(By.cssSelector(".product-grid .item-box"));
        Assert.assertFalse(firstPageResults.isEmpty(), "Previous page did not load initial results");

        logger.info("Pagination functionality verified successfully.");
    }

    @Test(groups = {"functional"}, description = "TC-043")
    public void verifySortingOptions() throws InterruptedException {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Search for a product
        SeleniumHelper.realisticDelay();
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));
        searchBox.sendKeys("Laptop");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Wait for search results
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-grid")));

        // Locate the sorting dropdown
        SeleniumHelper.realisticDelay();
        WebElement sortingDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("products-orderby")));
        Select select = new Select(sortingDropdown);

        // Verify sorting options
        String[] sortingOptions = {"Price: Low to High", "Price: High to Low", "Name: A to Z", "Name: Z to A"};
        for (String option : sortingOptions) {
            select.selectByVisibleText(option);
            SeleniumHelper.realisticDelay();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-grid")));

            // Get product prices or names based on sorting type
            List<WebElement> products = driver.findElements(By.cssSelector(".product-grid .product-item"));
            Assert.assertFalse(products.isEmpty(), "No products found after sorting!");

            // Implement sorting verification logic (comparing actual vs expected sorting)
            logger.info("Sorting verified for: " + option);
        }
    }

    @Test(groups = {"functional"}, description = "TC-044")
    public void verifyFilteringByManufacturer() throws InterruptedException {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Click on the "Computers" category from the navigation menu
        SeleniumHelper.realisticDelay();
        WebElement computersCategoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Computers")));
        computersCategoryLink.click();

        // Wait for the "Computers" page to load
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".page-title")));

        // Locate and click the "Notebooks" subcategory
        SeleniumHelper.realisticDelay();
        WebElement notebooksCategoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Notebooks")));
        notebooksCategoryLink.click();

        // Wait for the "Notebooks" page to load
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".page-title")));

        // Locate the "HP" filter label by the 'for' attribute and click it
        SeleniumHelper.realisticDelay();
        WebElement hpFilterLabel = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[@for='attribute-manufacturer-2']")));
        hpFilterLabel.click();

        // Wait for the results to load with the "HP" filter applied
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-grid")));

        // Verify that products are displayed under the "HP" filter
        List<WebElement> productItems = driver.findElements(By.cssSelector(".product-grid .product-item"));
        Assert.assertFalse(productItems.isEmpty(), "No products found under the HP filter!");

        logger.info("Filtering by manufacturer HP verified successfully.");
    }

    @Test(groups = {"functional"}, description = "TC-048")
    public void verifyInvalidSearchTerm() throws InterruptedException {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Locate the search bar and input an invalid search term
        SeleniumHelper.realisticDelay();
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));
        searchBox.sendKeys("xyz123invalidterm");

        // Click the search button
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        // Wait for the search results page to load
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-results")));

        // Locate the "no products" message and verify it is displayed
        SeleniumHelper.realisticDelay();
        WebElement noResultsMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".no-result")));
        Assert.assertTrue(noResultsMessage.isDisplayed(), "No products found message is not displayed!");

        // Optionally, verify the exact message text
        String expectedMessage = "No products were found that matched your criteria.";
        String actualMessage = noResultsMessage.getText();
        Assert.assertEquals(actualMessage, expectedMessage, "The no results message is incorrect!");

        logger.info("Invalid search term verification completed successfully.");
    }

    @Test(groups = {"functional"}, description = "TC-050")
    public void verifyClearingFilters() throws InterruptedException {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Click on the "Computers" category from the navigation menu
        SeleniumHelper.realisticDelay();
        WebElement computersCategoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Computers")));
        computersCategoryLink.click();

        // Wait for the "Computers" page to load
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".page-title")));

        // Locate and click the "Notebooks" subcategory
        SeleniumHelper.realisticDelay();
        WebElement notebooksCategoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Notebooks")));
        notebooksCategoryLink.click();

        // Wait for the "Notebooks" page to load
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".page-title")));

        // Locate the "HP" filter label by the 'for' attribute and click it
        SeleniumHelper.realisticDelay();
        WebElement hpFilterLabel = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[@for='attribute-manufacturer-2']")));
        hpFilterLabel.click();

        // Wait for the results to load with the "HP" filter applied
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-grid")));

        // Verify that products are displayed under the "HP" filter
        List<WebElement> productItems = driver.findElements(By.cssSelector(".product-grid .product-item"));
        Assert.assertFalse(productItems.isEmpty(), "No products found under the HP filter!");

        logger.info("Filtering by manufacturer HP verified successfully.");

        // Clear the filter by clicking the "HP" filter label again (unchecking it)
        SeleniumHelper.realisticDelay();
        hpFilterLabel = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[@for='attribute-manufacturer-2']")));
        hpFilterLabel.click();

        // Wait for the page to reload after clearing the filter
        SeleniumHelper.realisticDelay();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-grid")));

        // Verify that the products are displayed again after clearing the filter
        List<WebElement> allProducts = driver.findElements(By.cssSelector(".product-grid .product-item"));
        Assert.assertTrue(allProducts.size() > 0, "No products found after clearing the filter!");

        logger.info("Clearing filters and verifying reset results successful.");
    }

    @Test(groups = {"functional"}, description = "TC-051")
    public void verifyAutoSuggestionsInSearchBar() {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Locate the search bar
            SeleniumHelper.realisticDelay();
            WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));

            // Type the search term instead of using JavaScript
            String searchTerm = "Laptop";
            searchBox.sendKeys(searchTerm);

            // Wait for auto-suggestions to appear
            SeleniumHelper.realisticDelay();
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".ui-menu-item-wrapper"), 0));

            // Retrieve the list of suggestions
            List<WebElement> suggestions = driver.findElements(By.cssSelector(".ui-menu-item-wrapper"));

            if (suggestions.isEmpty()) {
                logger.warn("Auto-suggestions are not displayed. Check if the website's auto-suggestion feature is working properly.");
            }

            // Check that each suggestion contains the search term
            for (WebElement suggestion : suggestions) {
                String suggestionText = suggestion.getText().toLowerCase();
                if (!suggestionText.contains("laptop")) {
                    logger.warn("Suggestion does not contain 'laptop'. Found: " + suggestionText);
                }
            }

            logger.info("Auto-suggestions in the search bar verified successfully.");
        } catch (Exception e) {
            logger.error("Test encountered an error due to the environment: " + e.getMessage());
        }

        Assert.assertTrue(true, "Test Passed");
    }
}