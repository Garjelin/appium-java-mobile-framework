package io.github.garjelin.tests;

import io.appium.java_client.AppiumDriver;
import io.github.garjelin.config.Config;
import io.github.garjelin.driver.DriverManager;
import io.github.garjelin.support.KnownIssue;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Parent of every UI test: owns the session lifecycle and failure diagnostics,
 * so tests contain only test logic.
 * A new session per test method keeps tests independent (clean app state) at the cost of speed.
 */
public abstract class BaseTest {

    /** Fills the "Environment" widget of the Allure report: what the run was executed on. */
    @BeforeSuite(alwaysRun = true)
    public void writeAllureEnvironment() {
        String content = "Platform=" + Config.platform() + "\n"
                + "Device=" + Config.get("device.name") + "\n"
                + "Platform.version=" + Config.find("platform.version").orElse("any") + "\n"
                + "App=" + Config.get("app.path") + "\n";
        Path dir = Path.of(System.getProperty("allure.results.directory", "allure-results"));
        try {
            Files.createDirectories(dir);
            Files.writeString(dir.resolve("environment.properties"), content);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot write Allure environment to " + dir, e);
        }
    }

    // alwaysRun: run even when tests are filtered by groups, otherwise the driver would be missing.
    // TestNG injects the test Method, so we can read its annotations before starting a session.
    @BeforeMethod(alwaysRun = true)
    public void startDriver(Method testMethod) {
        skipIfKnownIssue(testMethod);
        DriverManager.start();
    }

    // alwaysRun: close the session even after a failure, so the device is free for the next test.
    // TestNG injects the result of the test that just ran.
    @AfterMethod(alwaysRun = true)
    public void quitDriver(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE && DriverManager.isStarted()) {
                attachFailureDiagnostics();
            }
        } finally {
            // finally: the session is closed even if taking a screenshot failed.
            DriverManager.quit();
        }
    }

    protected AppiumDriver driver() {
        return DriverManager.driver();
    }

    /**
     * Skips BEFORE creating a session: no point spending 10-30 s on a device for a test
     * that is known to fail. SkipException in @BeforeMethod marks the test SKIPPED with this message.
     */
    private static void skipIfKnownIssue(Method testMethod) {
        KnownIssue issue = testMethod.getAnnotation(KnownIssue.class);
        if (issue != null && issue.platform() == Config.platform()) {
            throw new SkipException(issue.id() + " on " + issue.platform() + ": " + issue.summary()
                    + " (see docs/known-issues.md)");
        }
    }

    /**
     * What you need to understand a failure without re-running it:
     * how the screen looked (screenshot) and what the automation saw (page source = element tree,
     * shows which locators were really there).
     */
    private void attachFailureDiagnostics() {
        AppiumDriver driver = DriverManager.driver();
        try {
            byte[] screenshot = driver.getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment("Screenshot on failure", "image/png", new ByteArrayInputStream(screenshot), "png");
            Allure.addAttachment("Page source on failure", "text/xml", driver.getPageSource(), "xml");
        } catch (RuntimeException e) {
            // Diagnostics must never hide the real failure (e.g. the session is already dead).
            System.err.println("[warn] could not attach failure diagnostics: " + e.getMessage());
        }
    }
}
