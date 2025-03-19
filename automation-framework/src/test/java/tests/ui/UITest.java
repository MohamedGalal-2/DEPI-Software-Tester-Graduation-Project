package tests.ui;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import base.BaseTest;
import utils.SeleniumHelper;
import utils.ConfigReader;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class UITest extends BaseTest {
    private final String url = ConfigReader.getProperty("baseURL");
    private final String loginUrl = ConfigReader.getProperty("loginURL");
    private final String cartUrl = ConfigReader.getProperty("cartURL");
    private final String wishlistUrl = ConfigReader.getProperty("wishlistURL");
    private final String registrationUrl = ConfigReader.getProperty("registrationURL");

    @Test(groups = {"smoke", "functional"}, description = "TC-023")
    public void verifyUserRegistrationWithValidDetails() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        String firstName = "Test";
        String lastName = "User";
        String emailAddress = "TestUserForTesting123@testmail.test";
        String validPassword = "123456";

        // Step 1: Navigate to the registration page
        driver.get(registrationUrl);
        logger.info("Navigating to registration page.");

        // Step 2: Fill in the registration form with valid details
        SeleniumHelper.realisticDelay();
        WebElement firstNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("FirstName")));
        WebElement lastNameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("LastName")));
        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Email")));
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Password")));
        WebElement confirmPasswordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ConfirmPassword")));
        WebElement registerButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-button")));

        // Enter valid details
        firstNameField.sendKeys(firstName);
        lastNameField.sendKeys(lastName);
        emailField.sendKeys(emailAddress);
        passwordField.sendKeys(validPassword);
        confirmPasswordField.sendKeys(validPassword);

        // Step 3: Click the "Register" button
        SeleniumHelper.realisticDelay();
        registerButton.click();
        logger.info("Clicking 'Register' button.");

        // Step 4: Verify that the user is registered successfully
        SeleniumHelper.realisticDelay();
        WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".result")));

        // Step 5: Assert success message is displayed
        Assert.assertTrue(successMessage.isDisplayed(), "User registration was not successful!");
        logger.info("User successfully registered.");

        // Optional: You can also verify user redirection after successful registration
        // For example, checking if the user is redirected to a login page or dashboard
    }




}