package io.github.garjelin.driver;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.github.garjelin.config.Config;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Creates a new Appium session for the platform selected in {@link Config}.
 * The ONLY place in the framework that knows about AndroidDriver / IOSDriver and their options:
 * tests and pages work with the common {@link AppiumDriver} type.
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static AppiumDriver create() {
        URL serverUrl = toUrl(Config.get("appium.url"));

        // Switch expression (Java 14+): the compiler checks that every Platform value is handled.
        // Adding a new enum constant without a branch here becomes a compile error, not a runtime bug.
        return switch (Config.platform()) {
            case ANDROID -> new AndroidDriver(serverUrl, androidOptions());
            case IOS -> new IOSDriver(serverUrl, iosOptions());
        };
    }

    private static UiAutomator2Options androidOptions() {
        // Sets platformName=Android and appium:automationName=UiAutomator2 by itself.
        return new UiAutomator2Options()
                .setDeviceName(Config.get("device.name"))
                .setApp(appPath());
    }

    private static XCUITestOptions iosOptions() {
        // Sets platformName=iOS and appium:automationName=XCUITest by itself.
        XCUITestOptions options = new XCUITestOptions()
                .setDeviceName(Config.get("device.name"))
                .setApp(appPath());
        // Optional: without a version the driver picks any simulator with a matching name.
        Config.find("platform.version").ifPresent(options::setPlatformVersion);
        return options;
    }

    /**
     * Resolves app.path against the project root and checks it exists BEFORE contacting Appium.
     * Otherwise a missing binary surfaces as a long server-side stack trace.
     */
    private static String appPath() {
        Path path = Path.of(Config.get("app.path")).toAbsolutePath();
        if (!Files.exists(path)) {
            throw new IllegalStateException(
                    "App under test not found: " + path + ". Download it into apps/ first.");
        }
        // Absolute, because the Appium server resolves relative paths against ITS working dir.
        return path.toString();
    }

    private static URL toUrl(String value) {
        try {
            // URI.create(...).toURL() instead of new URL(String), deprecated since JDK 20.
            return URI.create(value).toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid appium.url in config: " + value, e);
        }
    }
}
