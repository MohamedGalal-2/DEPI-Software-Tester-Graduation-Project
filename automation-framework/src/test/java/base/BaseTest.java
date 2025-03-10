package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;
    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    @BeforeMethod
    public void setUp() {
        String browser = System.getProperty("browser", "chrome"); // Default browser is Chrome
        String timeout = System.getProperty("timeout", "5"); // Default timeout is 5 seconds
        logger.info("Starting test setup...");

        try {
            switch (browser.toLowerCase()) {
                case "edge":
                    logger.info("Setting up Edge browser...");
                    WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver();
                    break;
                case "firefox":
                    logger.info("Setting up Firefox browser...");
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver();
                    break;
                case "chrome":
                default:
                    logger.info("Setting up Chrome browser...");
                    WebDriverManager.chromedriver().setup();
                    driver = new ChromeDriver();
                    break;
            }

            // Maximize window and set timeout
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.parseInt(timeout)));
            logger.info("Browser setup complete.");
        } catch (Exception e) {
            logger.error("Failed to set up the browser driver.", e);
            throw new RuntimeException("Browser setup failed!", e);
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            logger.info("Closing the browser...");
            driver.quit(); // Close the browser
            logger.info("Browser closed.");
        }
    }
}
