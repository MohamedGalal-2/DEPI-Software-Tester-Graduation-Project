package tests.ui;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class UITest extends BaseTest {

    @Test
    public void testLoginFunctionality() {
        driver.get("https://www.demoblaze.com/");

        // Locate and click the login button
        WebElement loginButton = driver.findElement(By.id("login2"));
        loginButton.click();

        // Wait for the login modal to appear
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement loginModal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logInModal")));

        // Assert that the login modal is displayed
        Assert.assertTrue(loginModal.isDisplayed(), "Login modal is not displayed!");

        // Enter username
        WebElement usernameField = driver.findElement(By.id("loginusername"));
        usernameField.sendKeys("Mohamedtestuser");

        // Enter password
        WebElement passwordField = driver.findElement(By.id("loginpassword"));
        passwordField.sendKeys("password123"); // Right password scenario

        // Click login button inside modal
        WebElement loginSubmit = driver.findElement(By.xpath("//button[contains(text(), 'Log in')]"));
        loginSubmit.click();

        // ✅ Handle the alert if login fails
        try {
            wait.until(ExpectedConditions.alertIsPresent()); // Wait for the alert
            driver.switchTo().alert().accept(); // Accept (close) the alert
            System.out.println("Login failed: Alert detected and closed.");
            Assert.fail("Login failed: Wrong password.");
        } catch (Exception e) {
            System.out.println("No alert found, continuing with login validation.");
        }
    }
}
