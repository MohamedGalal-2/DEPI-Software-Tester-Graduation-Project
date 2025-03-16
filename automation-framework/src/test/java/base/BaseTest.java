package base;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.Cookie;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.ITestResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Map;

public class BaseTest {
    protected WebDriver driver;
    protected Logger logger;

    @BeforeMethod
    public void setUp(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String logFilePath = "logs/" + testName + ".log";

        // Create logs directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get("logs"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        configureLogger(testName, logFilePath);
        logger = LogManager.getLogger(testName);
        logger.info("Starting test setup for: " + testName);

        try {
            // Fetch Cloudflare-bypassed HTML and cookies using Puppeteer
            String url = "https://demo.nopcommerce.com";
            Map<String, String> bypassedData = CloudflareBypass.getBypassedData(url);

            if (bypassedData == null || bypassedData.isEmpty()) {
                throw new RuntimeException("Failed to retrieve bypassed HTML.");
            }

            String bypassedHTML = bypassedData.get("content");
            String cookiesJson = bypassedData.get("cookies");

            // Set up Selenium with stealth mode
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.setExperimentalOption("useAutomationExtension", false);

            driver = WebDriverManager.chromedriver().capabilities(options).create();
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

            // Navigate to the site normally
            driver.get(url);
            logger.info("Navigated to the site: " + url);

            // Inject bypassed cookies
            JSONArray cookiesArray;
            try {
                cookiesArray = new JSONArray(cookiesJson);
            } catch (Exception e) {
                logger.error("Invalid JSON for cookies: " + cookiesJson, e);
                throw new RuntimeException("Failed to parse cookies JSON!", e);
            }
            for (int i = 0; i < cookiesArray.length(); i++) {
                JSONObject jsonObject = cookiesArray.getJSONObject(i);
                Cookie cookie = new Cookie.Builder(jsonObject.getString("name"), jsonObject.getString("value"))
                        .domain(jsonObject.getString("domain"))
                        .path(jsonObject.getString("path"))
                        .isSecure(jsonObject.getBoolean("secure"))
                        .build();
                driver.manage().addCookie(cookie);
                logger.info("Injected cookie: " + jsonObject.getString("name"));
            }

            // Reload the page with the bypassed cookies
            driver.navigate().refresh();
            logger.info("Page refreshed with bypassed cookies.");

        } catch (Exception e) {
            logger.error("Failed to set up the browser", e);
            throw new RuntimeException("Browser setup failed!", e);
        }
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (driver != null) {
            logger.info("Closing the browser for test: " + result.getMethod().getMethodName());
            driver.quit();
            logger.info("Browser closed.");
        }
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
