package com.bkstore.fastapi.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ISuite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReporterNG implements ITestListener, ISuiteListener{

    // ExtentReports and ExtentTest objects are crucial for reporting
    public ExtentSparkReporter sparkReporter; // For HTML report generation
    public ExtentReports extent;             // Main class for ExtentReports
    public ExtentTest test;                  // Represents a test case in the report

    // Logger for logging events
    // Using Log4j2 for logging, which is a common logging framework in Java
    private static final Logger logger = LogManager.getLogger(ExtentReporterNG.class);

    // This method is called before any test suite starts
    @Override
    public void onStart(ISuite suite)
    {
        // This method can be used to perform setup before any test suite runs
        logger.info("Starting test suite: " + suite.getName());
    }

    @Override
    public void onStart(ITestContext context) {

        // Log to Log4j2 that the test context is starting
        logger.info("Test context started: " + context.getName());

        // Generate a timestamp for the report file name to ensure uniqueness
        String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        String reportName = "Test-Report-" + timestamp + ".html";

        // Define the path where the report will be generated
        // We'll put it in a 'reports' folder at the project root
        String reportPath = System.getProperty("user.dir") + File.separator + "reports" + File.separator + reportName;

        // Initialize ExtentSparkReporter
        sparkReporter = new ExtentSparkReporter(reportPath);

        // Configure the Spark Reporter
        sparkReporter.config().setDocumentTitle("FastAPI Automation Report"); // Title of report
        sparkReporter.config().setReportName("Bookstore API Test Results");   // Name of the report
        sparkReporter.config().setTheme(Theme.DARK);                         // Report theme (DARK or STANDARD)
        sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");

        // Initialize ExtentReports and attach the Spark Reporter
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        // Set system information (optional, but good for context)
        extent.setSystemInfo("Host Name", "Localhost");
        extent.setSystemInfo("Environment", "QA/Dev"); // This could be dynamic based on ConfigReader
        extent.setSystemInfo("User", "Rohit Menon");
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));


        // Log to Log4j2 that the report has been initialized
        logger.info("Extent Report initialized at : " + reportPath);
    }

    // This method is called after all tests in a suite have run
    @Override
    public void onFinish(ITestContext context) {
        // Flush the report to write all collected data to the HTML file
        extent.flush();
        System.out.println("Extent Report generated at: " + sparkReporter.getFile().getAbsolutePath());
        // Log to Log4j2 that the test context has finished
        logger.info("Test context finished: " + context.getName());
    }

    // This method is called before each test method starts
    @Override
    public void onTestStart(ITestResult result) {
        // Create a new test entry in the report for each test method
        // The test name will be the method name, and description will be from @Test annotation
        test = extent.createTest(result.getMethod().getMethodName())
                     .assignCategory(result.getMethod().getGroups()); // Assign test to its TestNG groups
        test.info("Starting test: " + result.getMethod().getMethodName());
        if (result.getMethod().getDescription() != null && !result.getMethod().getDescription().isEmpty()) {
            test.info("Description: " + result.getMethod().getDescription());
        }

        // Log to Log4j2 that the test context is starting
        logger.info("Starting test method: " + result.getMethod().getMethodName());
    }

    // This method is called when a test method succeeds
    @Override
    public void onTestSuccess(ITestResult result) {
        // Log the test status as PASS
        test.log(Status.PASS, MarkupHelper.createLabel(result.getMethod().getMethodName() + " PASSED ", ExtentColor.GREEN));
        // Log to Log4j2 that the test context is starting
        logger.info("Test method passed : " + result.getMethod().getMethodName());
    }

    // This method is called when a test method fails
    @Override
    public void onTestFailure(ITestResult result) {
        // Log the test status as FAIL and include the exception details
        test.log(Status.FAIL, MarkupHelper.createLabel(result.getMethod().getMethodName() + " FAILED ", ExtentColor.RED));
        test.fail(result.getThrowable()); // Log the exception/error that caused the failure

        // Log to Log4j2 that the test context is starting
        logger.info("Test method failed! : " + result.getThrowable());
    }

    // This method is called when a test method is skipped
    @Override
    public void onTestSkipped(ITestResult result) {
        // Log the test status as SKIP and include the skip reason
        test.log(Status.SKIP, MarkupHelper.createLabel(result.getMethod().getMethodName() + " SKIPPED ", ExtentColor.ORANGE));
        test.skip(result.getThrowable()); // Log the reason for skipping if available

        // Log to Log4j2 that the test context is starting
        logger.info("Test skipped! : " + result.getThrowable());
    }

    // These methods are less commonly used for basic reporting but are part of the ITestListener interface
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not implemented for this basic setup
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        // Not implemented for this basic setup
    }
}