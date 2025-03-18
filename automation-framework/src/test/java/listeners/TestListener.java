package listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.*;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITest;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.lang.reflect.Field;

import static base.BaseTest.getDriver;


public class TestListener implements ITestListener {

    private ExtentReports extentReports;
    private ExtentTest extentTest;

    private int passedTests = 0;
    private int failedTests = 0;
    private int skippedTests = 0;
    private StringBuilder reportBody = new StringBuilder();

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();

        String testCaseID = (String) result.getTestContext().getAttribute("TestCaseID");
        if (testCaseID == null) testCaseID = "UnknownTC";

        result.setAttribute("startTime", System.currentTimeMillis()); // Save start time

        Logger testLogger = LogManager.getLogger(testCaseID + "_" + testName);
        testLogger.info("🟢 Starting test: " + testName + " (🔢 Test Case ID: " + testCaseID + ")");

        // Log Test Case ID in TestNG Reporter
        Reporter.log("🟢 Test Started: " + testName + " (🔢 Test Case ID: " + testCaseID + ")", true);
        reportBody.append("🟢 Test Started: ").append(testName).append("\n");

        // Add to Extent Reports
        extentTest = extentReports.createTest(testCaseID + " - " + testName);
        extentTest.info("🔢 Test Case ID: " + testCaseID);

        // 🛠️ Environment Details (Logged Once Per Test)
        String os = System.getProperty("os.name") + " " + System.getProperty("os.version");
        String javaVersion = System.getProperty("java.version");
        String browserInfo = "Chrome 134.0.6998.89"; // Default

        WebDriver driver = getDriver();
        if (driver instanceof RemoteWebDriver) {
            var caps = ((RemoteWebDriver) driver).getCapabilities();
            browserInfo = caps.getBrowserName() + " " + caps.getBrowserVersion();
        }

        String environmentDetails = "\n🌍 Test Environment:\n" +
                "🖥️ OS: " + os + "\n" +
                "☕ Java Version: " + javaVersion + "\n" +
                "🌐 Browser: " + browserInfo + "\n";

        // Print to Console, Reporter, and ExtentReports
        Reporter.log(environmentDetails, true);
        reportBody.append(environmentDetails);

        extentTest.info("🖥️ OS: " + os);
        extentTest.info("☕ Java Version: " + javaVersion);
        extentTest.info("🌐 Browser: " + browserInfo);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        passedTests++;
        long duration = System.currentTimeMillis() - (long) result.getAttribute("startTime");

        Logger testLogger = LogManager.getLogger(TestListener.class);
        testLogger.info("✅ Test PASSED: " + result.getMethod().getMethodName() + " (Duration: " + duration + "ms)");

        Reporter.log("✅ Test PASSED: " + result.getMethod().getMethodName() + " (Duration: " + duration + "ms)");
        reportBody.append("✅ Test PASSED: ").append(result.getMethod().getMethodName())
                .append(" (Duration: ").append(duration).append("ms)\n");

        extentTest.pass("Test PASSED: " + result.getMethod().getMethodName() + " (Duration: " + duration + "ms)");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        failedTests++;
        Logger testLogger = LogManager.getLogger(TestListener.class);
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();
        testLogger.error("❌ Test FAILED: " + testName, throwable);

        // Log failure in TestNG Reporter
        Reporter.log("❌ Test FAILED: " + testName + " - " + throwable.getMessage());
        reportBody.append("❌ Test FAILED: ").append(testName)
                .append(" \n- ").append(throwable.getMessage()).append("\n");

        // Capture Screenshot and Attach to Report
        WebDriver driver = getDriver();
        if (driver != null) {
            String testCaseID = (String) result.getTestContext().getAttribute("TestCaseID");
            if (testCaseID == null) testCaseID = "UnknownTC"; // Handle missing ID

            String screenshotPath = captureScreenshot(driver, testCaseID);
            if (screenshotPath != null) {
                Reporter.log("<br><img src='" + screenshotPath + "' height='300' width='400'/><br>");
                extentTest.fail("Test FAILED: " + testName + " \n- " + throwable.getMessage()).addScreenCaptureFromPath(screenshotPath);
            }
        } else {
            extentTest.fail("Test FAILED: " + testName + " \n- " + throwable.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        skippedTests++;
        Logger testLogger = (Logger) result.getAttribute("logger");
        if (testLogger != null) {
            testLogger.warn("⚠️ Test SKIPPED: " + result.getMethod().getMethodName());
        }

        Reporter.log("⚠️ Test SKIPPED: " + result.getMethod().getMethodName());
        reportBody.append("⚠️ Test SKIPPED: ").append(result.getMethod().getMethodName()).append("\n");

        // Log the skip in ExtentReports
        extentTest.skip("Test SKIPPED: " + result.getMethod().getMethodName());
    }

    @Override
    public void onStart(ITestContext context) {
        System.out.println("Test Suite Started: " + context.getName());

        // Define the directory for extent reports
        String reportDir = "E:/Folder/Galal/Courses/Digital Egypt Pioneers Initiative (DEPI) - Software Testing/DEPI Software Tester Graduation Project/automation-framework/reports/extent-reports";
        String reportFilePath = reportDir + "/extent-report.html";

        // Ensure the report directory exists
        try {
            Files.createDirectories(Paths.get(reportDir));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("❌ Failed to create Extent Reports directory: " + reportDir);
        }

        // Initialize Extent Reports
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFilePath);
        sparkReporter.config().setReportName("(demo.nopcommerce.com) Test Automation Report");

        extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);

        Logger logger = LogManager.getLogger(TestListener.class);
        logger.info("🚀 Starting Test Suite: " + context.getName());
        Reporter.log("🚀 Starting Test Suite: " + context.getName());

        // 🛠️ Environment Details
        String os = System.getProperty("os.name") + " " + System.getProperty("os.version");
        String javaVersion = System.getProperty("java.version");
        String browserInfo = "chrome 134.0.6998.89";

        String environmentDetails = "\n🌍 Test Environment:\n" +
                "🖥️ OS: " + os + "\n" +
                "☕ Java Version: " + javaVersion + "\n" +
                "🌐 Browser: " + browserInfo + "\n";

        // Log to TestNG Reporter
        Reporter.log(environmentDetails, true);
        reportBody.append(environmentDetails);

        // Add to ExtentReports
        extentReports.setSystemInfo("Operating System", os);
        extentReports.setSystemInfo("Java Version", javaVersion);
        extentReports.setSystemInfo("Browser", browserInfo);
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("Test Suite Finished: " + context.getName());
        Logger logger = LogManager.getLogger(TestListener.class);
        logger.info("🏁 Finished Test Suite: " + context.getName());
        Reporter.log("🏁 Finished Test Suite: " + context.getName());

        // 📝 Add Summary to TestNG Reporter Output
        String summary = "\n🏁 Test Suite Summary:\n" +
                "✅ Passed: " + passedTests + "\n" +
                "❌ Failed: " + failedTests + "\n" +
                "⚠️ Skipped: " + skippedTests + "\n";

        Reporter.log(summary, true);
        reportBody.append(summary);

        // Add to ExtentReports
        ExtentTest summaryTest = extentReports.createTest("🏁 Test Suite Summary");
        summaryTest.info("✅ Passed: " + passedTests);
        summaryTest.info("❌ Failed: " + failedTests);
        summaryTest.info("⚠️ Skipped: " + skippedTests);

        // Flush ExtentReports at the end
        extentReports.flush();
    }

    private String captureScreenshot(WebDriver driver, String testCaseID) {
        try {
            // 🏁 Define the base screenshot directory
            String baseDir = "E:/Folder/Galal/Courses/Digital Egypt Pioneers Initiative (DEPI) - Software Testing/DEPI Software Tester Graduation Project/automation-framework/reports/screenshots";

            // 🗂️ Ensure the base directory exists
            File baseDirectory = new File(baseDir);
            if (!baseDirectory.exists()) {
                boolean baseCreated = baseDirectory.mkdirs();
                System.out.println("📂 Base directory created: " + baseDirectory.getAbsolutePath() + " -> " + baseCreated);
            }

            // 📂 Create a test-specific folder inside the base directory
            String testFolderPath = baseDir + File.separator + testCaseID;
            File testFolder = new File(testFolderPath);
            if (!testFolder.exists()) {
                boolean created = testFolder.mkdirs();
                if (!created) {
                    System.out.println("❌ Failed to create test folder: " + testFolder.getAbsolutePath());
                    return null;
                }
                System.out.println("📂 Test folder created: " + testFolder.getAbsolutePath());
            }

            // 📸 Define the screenshot file path
            String screenshotPath = testFolderPath + File.separator + testCaseID + "_screenshot.png";
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destinationFile = new File(screenshotPath);

            // 📝 Save the screenshot
            Files.copy(screenshot.toPath(), destinationFile.toPath());

            System.out.println("📸 Screenshot saved at: " + destinationFile.getAbsolutePath());

            return screenshotPath;
        } catch (IOException e) {
            System.out.println("❌ Failed to save screenshot for " + testCaseID);
            e.printStackTrace();
            return null;
        }
    }

//    private void sendTestReport() {
//        final String username = "your-email@gmail.com";
//        final String password = "your-password";
//        final String recipient = "recipient-email@example.com";
//
//        Properties properties = new Properties();
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.smtp.starttls.enable", "true");
//        properties.put("mail.smtp.host", "smtp.gmail.com");
//        properties.put("mail.smtp.port", "587");
//
//        Session session = Session.getInstance(properties, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(username, password);
//            }
//        });
//
//        try {
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(username));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
//            message.setSubject("📝 Test Report");
//            message.setText(reportBody.toString());
//
//            Transport.send(message);
//            System.out.println("📩 Test Report Sent Successfully!");
//
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }
}
