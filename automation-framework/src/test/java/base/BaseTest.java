package base;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.Proxy;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.ITestResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
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
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        try {
            if (browser.equalsIgnoreCase("chrome")) {
                logger.info("Setting up Chrome browser...");
                ChromeOptions options = new ChromeOptions();

                // **ZenRows Proxy Setup with JavaScript Rendering & Rotating IP**
                String zenRowsApiKey = "YOUR_ZENROWS_API_KEY"; // Replace with your API Key
                String zenRowsProxy = "http://" + zenRowsApiKey + ":@proxy.zenrows.com:8001?js_render=true&session=random";

                Proxy proxy = new Proxy();
                proxy.setHttpProxy(zenRowsProxy)
                        .setSslProxy(zenRowsProxy);

                options.setProxy(proxy);

                // **Stealth Mode to Bypass Cloudflare**
                options.addArguments("--disable-blink-features=AutomationControlled");
                options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
                options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                options.setExperimentalOption("useAutomationExtension", false);

                if (isHeadless) {
                    options.addArguments("--headless=new");
                }

                driver = WebDriverManager.chromedriver().capabilities(options).create();
            }

            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            logger.info("Browser setup complete with ZenRows proxy.");
        } catch (Exception e) {
            logger.error("Failed to set up the browser driver.", e);
            throw new RuntimeException("Browser setup failed!", e);
        }
    }



    @AfterMethod
    public void tearDown(ITestResult result) throws InterruptedException {
        if (driver != null) {
            logger.info("Closing the browser for test: " + result.getMethod().getMethodName());
            driver.quit();
            logger.info("Browser closed.");
        }
        Thread.sleep(1000);
    }

    private void configureLogger(String loggerName, String logFilePath) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        builder.add(builder.newAppender(loggerName, "File")
                .addAttribute("fileName", logFilePath)
                .add(builder.newLayout("PatternLayout").addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss} [%t] %-5level %logger{36} - %msg%n")));

        builder.add(builder.newLogger(loggerName, org.apache.logging.log4j.Level.INFO)
                .add(builder.newAppenderRef(loggerName))
                .addAttribute("additivity", false));

        context.start(builder.build());
    }
}
