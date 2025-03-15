package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.builder.impl.DefaultConfigurationBuilder;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String logFileName = "logs/" + testName + ".log";

        Logger logger = createTestLogger(testName, logFileName);
        logger.info("Starting test: " + testName);
        result.setAttribute("logger", logger); // Store logger in test result for later use
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Logger logger = (Logger) result.getAttribute("logger");
        if (logger != null) {
            logger.info("Test PASSED: " + result.getMethod().getMethodName());
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Logger logger = (Logger) result.getAttribute("logger");
        if (logger != null) {
            logger.error("Test FAILED: " + result.getMethod().getMethodName(), result.getThrowable());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Logger logger = (Logger) result.getAttribute("logger");
        if (logger != null) {
            logger.warn("Test SKIPPED: " + result.getMethod().getMethodName());
        }
    }

    @Override
    public void onStart(ITestContext context) {
        System.out.println("Starting Test Suite: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("Finished Test Suite: " + context.getName());
    }

    private Logger createTestLogger(String loggerName, String logFileName) {
        try {
            Files.createDirectories(Paths.get("logs")); // Ensure logs directory exists
        } catch (Exception e) {
            e.printStackTrace();
        }

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
                .addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss} [%t] %-5level %logger{36} - %msg%n");

        AppenderComponentBuilder appenderBuilder = builder.newAppender(loggerName, "File")
                .addAttribute("fileName", logFileName)
                .add(layoutBuilder);

        builder.add(appenderBuilder);

        builder.add(builder.newLogger(loggerName, org.apache.logging.log4j.Level.INFO)
                .add(builder.newAppenderRef(loggerName))
                .addAttribute("additivity", false));

        context.start(builder.build());
        return LogManager.getLogger(loggerName);
    }
}
