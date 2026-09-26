package io.github.garjelin.tests;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

/**
 * Stage 0 smoke test: proves the whole chain works end to end
 * (Java client -> Appium server -> UiAutomator2 driver -> emulator -> app).
 *
 * Intentionally "raw": session setup, config and locators all live here.
 * Stage 1 will extract them into a DriverFactory and config files.
 */
public class FirstSessionTest {

    // Local Appium server started with `appium` in a separate terminal.
    private static final String APPIUM_SERVER = "http://127.0.0.1:4723";

    // Every product card title shares this content-desc, so it matches ALL visible titles.
    private static final By PRODUCT_TITLES = AppiumBy.accessibilityId("Product Title");

    // Upper bound for explicit waits. The app usually renders in 1-3 s;
    // 15 s leaves room for a cold emulator without hiding real hangs.
    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    private AndroidDriver driver;

    @BeforeMethod
    public void setUp() throws MalformedURLException {
        // Relative to the project root (Gradle's working dir for tests),
        // converted to absolute because the Appium server has its own working dir.
        String appPath = Path.of("apps", "mda-2.3.0.apk").toAbsolutePath().toString();

        // Typed wrapper over W3C capabilities. It already sets
        // platformName=Android and appium:automationName=UiAutomator2.
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("Pixel_8_API_34")
                .setApp(appPath);

        // URI.create(...).toURL() instead of new URL(String), which is deprecated since JDK 20.
        URL serverUrl = URI.create(APPIUM_SERVER).toURL();

        // This line sends POST /session - the same request Inspector sent.
        driver = new AndroidDriver(serverUrl, options);
    }

    @Test
    public void catalogShowsProducts() {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);

        // Explicit wait: polls until at least one title is visible, then returns all of them.
        // Throws TimeoutException with a clear message if the catalog never appears.
        List<WebElement> titles = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(PRODUCT_TITLES));

        // RecyclerView keeps only on-screen cards in the tree, so this is
        // "visible products", not the full catalog size.
        Assert.assertFalse(titles.isEmpty(), "Catalog should show at least one product");

        String firstTitle = titles.get(0).getText();
        Assert.assertFalse(firstTitle.isBlank(), "First product title should not be blank");

        System.out.println("Visible products: " + titles.size() + ", first: " + firstTitle);
    }

    // alwaysRun: clean up even if the test failed, so the device is free for the next run.
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        // null check: if setUp failed before the session was created, there is nothing to quit.
        if (driver != null) {
            driver.quit(); // sends DELETE /session/<id>
        }
    }
}
