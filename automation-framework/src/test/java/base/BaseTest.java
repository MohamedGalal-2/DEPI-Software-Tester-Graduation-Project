package base;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
        String testName = result.getMethod().getMethodName();
        String logFilePath = "logs/" + testName + ".log";

        try {
            Files.createDirectories(Paths.get("logs"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        configureLogger(testName, logFilePath);
        logger = LogManager.getLogger(testName);
        logger.info("Starting test setup for: " + testName);

        String browser = System.getProperty("browser", "chrome");
        String timeout = System.getProperty("timeout", "5");

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
                    ChromeOptions options = new ChromeOptions();

                    // Prevent bot detection
                    options.addArguments("--disable-blink-features=AutomationControlled");
                    options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
                    options.addArguments("--disable-popup-blocking");
                    options.addArguments("--disable-infobars");
                    options.addArguments("start-maximized");
                    options.addArguments("--disable-blink-features=AutomationControlled");
                    options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                    options.setExperimentalOption("useAutomationExtension", false);


                    // Dynamically fetch the latest User-Agent string
                    String userAgent = System.getProperty("user.agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

                    options.addArguments("user-agent=" + userAgent);

                    driver = new ChromeDriver(options);
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
