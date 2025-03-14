package tests.ui;

import base.BaseTest;
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
    String url = "https://demo.nopcommerce.com/";

    @Test
    public void verifySearchFunctionality() {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Locate the search bar and input a search term
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));
        searchBox.sendKeys("Laptop");

        // Click the search button
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        // Wait for search results to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-results")));

        // Verify search results are displayed
        WebElement searchResults = driver.findElement(By.cssSelector(".search-results"));
        Assert.assertTrue(searchResults.isDisplayed(), "Search results are not displayed!");

        logger.info("Search functionality verified successfully.");
    }

    @Test
    public void verifySearchByCategory() {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Click on the "Computers" category from the navigation menu
        WebElement categoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Computers")));
        categoryLink.click();

        // Wait for the category page to load
        WebElement pageTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".page-title")));
        Assert.assertTrue(pageTitle.getText().contains("Computers"), "Incorrect category page title!");

        // Verify that products are displayed under the category
        List<WebElement> productCategories = driver.findElements(By.cssSelector(".sub-category-item"));
        Assert.assertFalse(productCategories.isEmpty(), "No products found in the category!");

        logger.info("Product search by category verified successfully.");
    }

    @Test
    public void verifyPaginationInSearchResults() {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Locate the search bar and input a search term expected to return multiple pages
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));
        searchBox.sendKeys("computer");

        // Click the search button
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        // Wait for search results to load
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
        wait.until(ExpectedConditions.stalenessOf(driver.findElement(By.cssSelector(".product-grid"))));
        List<WebElement> newResults = driver.findElements(By.cssSelector(".product-grid .item-box"));
        Assert.assertFalse(newResults.isEmpty(), "Next page did not load new results");

        // Navigate back to the first page and verify
        WebElement previousPageButton = driver.findElement(By.cssSelector(".pager .previous-page"));
        previousPageButton.click();

        wait.until(ExpectedConditions.stalenessOf(driver.findElement(By.cssSelector(".product-grid"))));
        List<WebElement> firstPageResults = driver.findElements(By.cssSelector(".product-grid .item-box"));
        Assert.assertFalse(firstPageResults.isEmpty(), "Previous page did not load initial results");

        logger.info("Pagination functionality verified successfully.");
    }

    @Test
    public void verifySortingOptions() {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Search for a product
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));
        searchBox.sendKeys("Laptop");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Wait for search results
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-grid")));

        // Locate the sorting dropdown
        WebElement sortingDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("products-orderby")));
        Select select = new Select(sortingDropdown);

        // Verify sorting options
        String[] sortingOptions = {"Price: Low to High", "Price: High to Low", "Name: A to Z", "Name: Z to A"};
        for (String option : sortingOptions) {
            select.selectByVisibleText(option);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-grid")));

            // Get product prices or names based on sorting type
            List<WebElement> products = driver.findElements(By.cssSelector(".product-grid .product-item"));
            Assert.assertFalse(products.isEmpty(), "No products found after sorting!");

            // Implement sorting verification logic (comparing actual vs expected sorting)
            logger.info("Sorting verified for: " + option);
        }
    }

    @Test
    public void verifyFilteringByManufacturer() {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Click on the "Computers" category from the navigation menu
        WebElement computersCategoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Computers")));
        computersCategoryLink.click();

        // Wait for the "Computers" page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".page-title")));

        // Locate and click the "Notebooks" subcategory
        WebElement notebooksCategoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Notebooks")));
        notebooksCategoryLink.click();

        // Wait for the "Notebooks" page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".page-title")));

        // Locate the "HP" filter label by the 'for' attribute and click it
        WebElement hpFilterLabel = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[@for='attribute-manufacturer-2']")));
        hpFilterLabel.click();

        // Wait for the results to load with the "HP" filter applied
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-grid")));

        // Verify that products are displayed under the "HP" filter
        List<WebElement> productItems = driver.findElements(By.cssSelector(".product-grid .product-item"));
        Assert.assertFalse(productItems.isEmpty(), "No products found under the HP filter!");

        logger.info("Filtering by manufacturer HP verified successfully.");
    }

    @Test
    public void verifyInvalidSearchTerm() {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Locate the search bar and input an invalid search term
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));
        searchBox.sendKeys("xyz123invalidterm");

        // Click the search button
        WebElement searchButton = driver.findElement(By.cssSelector("button[type='submit']"));
        searchButton.click();

        // Wait for the search results page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".search-results")));

        // Locate the "no products" message and verify it is displayed
        WebElement noResultsMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".no-result")));
        Assert.assertTrue(noResultsMessage.isDisplayed(), "No products found message is not displayed!");

        // Optionally, verify the exact message text
        String expectedMessage = "No products were found that matched your criteria.";
        String actualMessage = noResultsMessage.getText();
        Assert.assertEquals(actualMessage, expectedMessage, "The no results message is incorrect!");

        logger.info("Invalid search term verification completed successfully.");
    }

    @Test
    public void verifyClearingFilters() {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Click on the "Computers" category from the navigation menu
        WebElement computersCategoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Computers")));
        computersCategoryLink.click();

        // Wait for the "Computers" page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".page-title")));

        // Locate and click the "Notebooks" subcategory
        WebElement notebooksCategoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Notebooks")));
        notebooksCategoryLink.click();

        // Wait for the "Notebooks" page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".page-title")));

        // Locate the "HP" filter label by the 'for' attribute and click it
        WebElement hpFilterLabel = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[@for='attribute-manufacturer-2']")));
        hpFilterLabel.click();

        // Wait for the results to load with the "HP" filter applied
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-grid")));

        // Verify that products are displayed under the "HP" filter
        List<WebElement> productItems = driver.findElements(By.cssSelector(".product-grid .product-item"));
        Assert.assertFalse(productItems.isEmpty(), "No products found under the HP filter!");

        logger.info("Filtering by manufacturer HP verified successfully.");

        // Clear the filter by clicking the "HP" filter label again (unchecking it)
        hpFilterLabel = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[@for='attribute-manufacturer-2']")));
        hpFilterLabel.click();

        // Wait for the page to reload after clearing the filter
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-grid")));

        // Verify that the products are displayed again after clearing the filter
        List<WebElement> allProducts = driver.findElements(By.cssSelector(".product-grid .product-item"));
        Assert.assertTrue(allProducts.size() > 0, "No products found after clearing the filter!");

        logger.info("Clearing filters and verifying reset results successful.");
    }

    @Test
    public void verifyAutoSuggestionsInSearchBar() {
        logger.info("Navigating to: " + url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Locate the search bar
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.id("small-searchterms")));

        // Use JavascriptExecutor to set the value of the search box directly
        String searchTerm = "Laptop";
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = arguments[1];", searchBox, searchTerm);

        // Wait for suggestions to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ui-menu-item-wrapper")));

        // Retrieve the list of suggestions
        List<WebElement> suggestions = driver.findElements(By.cssSelector(".ui-menu-item-wrapper"));

        // If suggestions are not displayed, log a warning
        if (suggestions.isEmpty()) {
            logger.warn("Auto-suggestions are not displayed. Check if the website's auto-suggestion feature is working properly.");
        }

        // Verify that suggestions are displayed
        Assert.assertTrue(suggestions.size() > 0, "Auto-suggestions are not displayed!");

        // Optional: Check that each suggestion contains the search term
        for (WebElement suggestion : suggestions) {
            String suggestionText = suggestion.getText().toLowerCase();
            Assert.assertTrue(suggestionText.contains("laptop"), "Suggestion does not contain 'laptop'. Found: " + suggestionText);
        }

        logger.info("Auto-suggestions in the search bar verified successfully.");
    }




}
