package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.ITestResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;
    protected Logger logger;

    @BeforeMethod
    public void setUp(ITestResult result) {
        // Get test name and create log file dynamically
        String testName = result.getMethod().getMethodName();
        String logFilePath = "logs/" + testName + ".log";

        // Ensure the logs directory exists
        try {
            Files.createDirectories(Paths.get("logs"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configure Log4j2 dynamically for each test case
        configureLogger(testName, logFilePath);

        logger = LogManager.getLogger(testName);
        logger.info("Starting test setup for: " + testName);

        // Set up WebDriver
        String browser = System.getProperty("browser", "chrome"); // Default is Chrome
        String timeout = System.getProperty("timeout", "5"); // Default timeout is 5 sec

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

            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.parseInt(timeout)));
            logger.info("Browser setup complete.");
        } catch (Exception e) {
            logger.error("Failed to set up the browser driver.", e);
            throw new RuntimeException("Browser setup failed!", e);
        }
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        logger.info("Closing the browser for test: " + result.getMethod().getMethodName());
        if (driver != null) {
            driver.quit();
            logger.info("Browser closed.");
        }
    }

    private void configureLogger(String loggerName, String logFilePath) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        // Create a file appender
        AppenderComponentBuilder appenderBuilder = builder.newAppender(loggerName, "File")
                .addAttribute("fileName", logFilePath)
                .add(builder.newLayout("PatternLayout").addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss} [%t] %-5level %logger{36} - %msg%n"));

        builder.add(appenderBuilder);

        // Create a logger
        builder.add(builder.newLogger(loggerName, org.apache.logging.log4j.Level.INFO)
                .add(builder.newAppenderRef(loggerName))
                .addAttribute("additivity", false));

        // Apply the new configuration
        context.start(builder.build());
    }
}
