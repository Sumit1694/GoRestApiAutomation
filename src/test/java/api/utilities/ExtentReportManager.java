package api.utilities;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportManager implements ITestListener {

    private static ExtentReports extent;
    private static ExtentTest test;

    private static String getReportName() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        return "API_Test_Report_" + timeStamp + ".html";
    }

    private static ExtentReports createInstance() {
        String reportPath = System.getProperty("user.dir") + "/reports/" + getReportName();
        ExtentSparkReporter reporter = new ExtentSparkReporter(reportPath);

        reporter.config().setDocumentTitle("Automation Report");
        reporter.config().setReportName("REST Assured Test Results");
        reporter.config().setTheme(Theme.DARK);

        ExtentReports extentReports = new ExtentReports();
        extentReports.attachReporter(reporter);

        extentReports.setSystemInfo("Tester", "Sumit Rane");
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
        extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        extentReports.setSystemInfo("Framework", "RestAssured + TestNG");

        return extentReports;
    }

    public void onStart(ITestContext context) {
        extent = createInstance();
    }

    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
    }

    public void onTestStart(ITestResult result) {
        test = extent.createTest(result.getMethod().getMethodName());
    }

    public void onTestSuccess(ITestResult result) {
        test.log(Status.PASS, "Test Passed: " + result.getMethod().getMethodName());
    }

    public void onTestFailure(ITestResult result) {
        test.log(Status.FAIL, "Test Failed: " + result.getMethod().getMethodName());
        test.log(Status.FAIL, "Reason: " + result.getThrowable());
    }

    public void onTestSkipped(ITestResult result) {
        test.log(Status.SKIP, "Test Skipped: " + result.getMethod().getMethodName());
        test.log(Status.SKIP, "Reason: " + result.getThrowable());
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not used
    }

    public void onTestFailedWithTimeout(ITestResult result) {
        onTestFailure(result);
    }
}
