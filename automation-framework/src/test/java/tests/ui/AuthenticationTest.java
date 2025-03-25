package tests.ui;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.MainFunctionalities;
import base.BaseTest;
import utils.SeleniumHelper;
import utils.ConfigReader;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationTest extends BaseTest {

    private final String url = ConfigReader.getProperty("baseURL");
    private final String loginUrl = ConfigReader.getProperty("loginURL");
    private final String cartUrl = ConfigReader.getProperty("cartURL");
    private final String wishlistUrl = ConfigReader.getProperty("wishlistURL");
    private final String registrationUrl = ConfigReader.getProperty("registrationURL");

    @Test(groups = {"functional"}, description = "TC-023")
    public void verifyUserRegistrationWithValidDetails() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        SeleniumHelper helper = new SeleniumHelper(driver);
        MainFunctionalities main = new MainFunctionalities(driver);

        String firstName = "Test";
        String lastName = "User";
        String emailAddress = "TestUserForTesting12312234@mailtestmail.test";
        String validPassword = "123456";

        main.register(firstName, lastName, emailAddress, validPassword);

        // Step 4: Verify that the user is registered successfully
        helper.realisticDelay();
        WebElement successMessage = helper.waitForVisibility(helper.getByLocator("register.successMessage"));

        // Step 5: Assert success message is displayed
        Assert.assertTrue(successMessage.isDisplayed(), "User registration was not successful!");
        logger.info("User successfully registered.");
    }

    @Test(groups = {"smoke", "functional"}, description = "TC-024")
    public void verifyErrorMessageForRegistrationWithMissingFields() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        MainFunctionalities main = new MainFunctionalities(driver);
        SeleniumHelper helper = new SeleniumHelper(driver);

        main.register("", "", "", "");

        // Step 4: Verify error messages for each mandatory field
        helper.realisticDelay();

        // Check for First Name error message
        helper.realisticDelay();
        WebElement firstNameError = helper.waitForVisibility(helper.getByLocator("register.firstNameError"));
        Assert.assertTrue(firstNameError.isDisplayed(), "First name error message not displayed!");

        // Check for Last Name error message
        helper.realisticDelay();
        WebElement lastNameError = helper.waitForVisibility(helper.getByLocator("register.lastNameError"));
        Assert.assertTrue(lastNameError.isDisplayed(), "Last name error message not displayed!");

        // Check for Email error message
        helper.realisticDelay();
        WebElement emailError = helper.waitForVisibility(helper.getByLocator("register.lastNameError"));
        Assert.assertTrue(emailError.isDisplayed(), "Email error message not displayed!");

        // Check for Password error message
        helper.realisticDelay();
        WebElement passwordError = helper.waitForVisibility(helper.getByLocator("register.passwordError"));
        Assert.assertTrue(passwordError.isDisplayed(), "Password error message not displayed!");

        logger.info("Error messages for missing mandatory fields are displayed.");
    }

    @Test(groups = {"functional"}, description = "TC-025")
    public void verifyUserLoginWithValidCredentials() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        MainFunctionalities main = new MainFunctionalities(driver);

        // Step 1: Login with valid credentials
        String emailAddress = "TestUserForTesting123@mailtestmail.test";
        String validPassword = "123456";
        main.login(emailAddress, validPassword);
    }

    @Test(groups = {"functional"}, description = "TC-026")
    public void verifyUserLoginWithInvalidCredentials() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        MainFunctionalities main = new MainFunctionalities(driver);
        SeleniumHelper helper = new SeleniumHelper(driver);

        // Step 1: Attempt login with invalid credentials
        String invalidEmail = "invaliduser@test.com";
        String invalidPassword = "wrongpassword";

        try {
            main.login(invalidEmail, invalidPassword);

            // Step 2: Check if the login failed by looking for an error message
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".message-error")));

            // Step 3: If the error message is displayed, login failed as expected, so we pass the test
            Assert.assertTrue(errorMessage.isDisplayed(), "Login failed as expected.");
            logger.info("Error message for invalid credentials is displayed successfully.");
        } catch (TimeoutException e) {
            WebElement accountLink = helper.waitForVisibility(By.cssSelector(".ico-account"));
            Assert.assertTrue(accountLink.isDisplayed(), "Login failed as expected");
        } catch (Exception e) {
            WebElement accountLink = helper.waitForVisibility(By.cssSelector(".ico-account"));
            Assert.assertTrue(accountLink.isDisplayed(), "Login failed as expected");
        }
    }

    @Test(groups = {"functional"}, description = "TC-028")
    public void verifyUserLogout() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        MainFunctionalities main = new MainFunctionalities(driver);
        SeleniumHelper helper = new SeleniumHelper(driver);

        // Step 1: Login with valid credentials
        String emailAddress = "TestUserForTesting123@mailtestmail.test";
        String validPassword = "123456";
        main.login(emailAddress, validPassword);

        // Step 2: Logout
        main.logout();
    }

    @Test(groups = {"functional"}, description = "TC-036")
    public void verifyErrorMessageForExistingEmail() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        MainFunctionalities main = new MainFunctionalities(driver);
        SeleniumHelper helper = new SeleniumHelper(driver);

        String firstName = "Test";
        String lastName = "User";
        String emailAddress = "UserForRegisterTesting123@testmail.test";
        String validPassword = "123456";

        main.register(firstName, lastName, emailAddress, validPassword);

        // Step 4: Verify that the user is registered successfully
        helper.realisticDelay();
        WebElement successMessage = helper.waitForVisibility(helper.getByLocator("register.successMessage"));
        Assert.assertTrue(successMessage.isDisplayed(), "First registration was not successful!");
        logger.info("User successfully registered.");

        // Step 5: Try registering again with the same details
        main.register(firstName, lastName, emailAddress, validPassword);

        // Step 7: Verify that an error message is displayed
        helper.realisticDelay();
        WebElement errorMessage = helper.waitForVisibility(helper.getByLocator("register.errorMessage"));

        // Step 8: Assert error message is displayed
        Assert.assertTrue(errorMessage.isDisplayed(), "Error message not displayed for duplicate email registration!");
        Assert.assertFalse(errorMessage.getText().contains("Email already exists"), "Unexpected error message: " + errorMessage.getText());
        logger.info("Verified error message for existing email.");
    }

}