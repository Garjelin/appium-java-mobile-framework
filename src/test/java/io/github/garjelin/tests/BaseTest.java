package io.github.garjelin.tests;

import io.appium.java_client.AppiumDriver;
import io.github.garjelin.driver.DriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Parent of every UI test: owns the session lifecycle, so tests contain only test logic.
 * A new session per test method keeps tests independent (clean app state) at the cost of speed.
 */
public abstract class BaseTest {

    // alwaysRun: run even when tests are filtered by groups, otherwise the driver would be missing.
    @BeforeMethod(alwaysRun = true)
    public void startDriver() {
        DriverManager.start();
    }

    // alwaysRun: close the session even after a failure, so the device is free for the next test.
    @AfterMethod(alwaysRun = true)
    public void quitDriver() {
        DriverManager.quit();
    }

    protected AppiumDriver driver() {
        return DriverManager.driver();
    }
}
