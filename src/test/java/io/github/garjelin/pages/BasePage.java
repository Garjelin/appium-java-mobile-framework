package io.github.garjelin.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.HasOnScreenKeyboard;
import io.appium.java_client.HidesKeyboard;
import io.github.garjelin.config.Config;
import io.github.garjelin.driver.DriverManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Parent of every Page Object.
 *
 * Responsibilities:
 * 1. Every UI interaction (wait, tap, type, read) goes through ONE place, so waiting
 *    rules and fixes (stale elements, keyboard) apply to the whole framework at once.
 * 2. A page verifies it is really on screen when it is created (screen marker).
 *    Tests create the next page explicitly after a navigation action
 *    (catalog.openFirstProduct(); ProductPage product = new ProductPage();),
 *    so the test shows which screen it is on, and creating the page IS the check
 *    that navigation worked: a wrong screen fails right there, with a clear message.
 * 3. Helpers to declare "same element, different locator per platform".
 */
public abstract class BasePage {

    // Upper bound for explicit waits: the app usually renders in 1-3 s;
    // 15 s leaves room for a cold device without hiding real hangs.
    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    private static final String ANDROID_APP_ID = "com.saucelabs.mydemoapp.android";

    protected final AppiumDriver driver;

    /**
     * @param screenMarker an element that exists only on this screen; the constructor waits for it
     */
    protected BasePage(By screenMarker) {
        this.driver = DriverManager.driver();
        // Shown in the Allure report as a step: "Screen: CartPage".
        Allure.step("Screen: " + getClass().getSimpleName(), () -> {
            waitVisible(screenMarker);
        });
    }

    // ---------- Locator helpers ----------

    /** Same element, different locator per platform. The ONLY place where pages branch on platform. */
    protected static By byPlatform(By android, By ios) {
        return switch (Config.platform()) {
            case ANDROID -> android;
            case IOS -> ios;
        };
    }

    protected static By accessibilityId(String id) {
        return AppiumBy.accessibilityId(id);
    }

    /** Android resource-id with the app package prefix, e.g. "nameET" -> "com...android:id/nameET". */
    protected static By androidId(String id) {
        return AppiumBy.id(ANDROID_APP_ID + ":id/" + id);
    }

    /**
     * iOS button by name. Needed when a StaticText has the same name as the button
     * (e.g. title "Login" and button "Login"): accessibilityId would return the first match.
     */
    protected static By iosButton(String name) {
        return AppiumBy.iOSClassChain("**/XCUIElementTypeButton[`name == \"" + name + "\"`]");
    }

    /**
     * iOS text field by position on the screen (1-based). Fallback for fields WITHOUT an
     * accessibility identifier: fragile by nature (breaks if fields are reordered).
     * In a real project: ask developers to add accessibilityIdentifier and delete this helper.
     */
    protected static By iosTextField(int position) {
        return AppiumBy.iOSClassChain("**/XCUIElementTypeTextField[" + position + "]");
    }

    // ---------- Waits ----------

    /**
     * Waits until AT LEAST ONE matching element is visible and returns the visible ones.
     * Not ExpectedConditions.visibilityOfAllElementsLocatedBy: it needs ALL matches to be visible,
     * and iOS keeps hidden and off-screen elements in the tree.
     */
    protected List<WebElement> waitAllVisible(By locator) {
        return newWait("visible element: " + locator).until(d -> {
            List<WebElement> visible = d.findElements(locator).stream()
                    .filter(WebElement::isDisplayed)
                    .toList();
            return visible.isEmpty() ? null : visible; // null = not yet, poll again in 500 ms
        });
    }

    protected WebElement waitVisible(By locator) {
        return waitAllVisible(locator).get(0);
    }

    /** Non-waiting check: is the element on screen RIGHT NOW. Use after the page has loaded. */
    protected boolean isVisibleNow(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    // ---------- Actions ----------

    // Every action is an Allure step, so the report shows exactly where a test stopped.
    // Block lambdas "() -> { ... }" on purpose: Allure.step has overloads for
    // "returns a value" and "returns nothing", and a block without return picks the second.

    protected void tap(By locator) {
        Allure.step("Tap " + describe(locator), () -> {
            // An open keyboard can cover the target (e.g. the "next" button under a form).
            hideKeyboardIfShown();
            waitVisible(locator).click();
        });
    }

    protected void type(By locator, String text) {
        Allure.step("Type '" + text + "' into " + describe(locator), () -> {
            enterText(locator, text);
        });
    }

    /** Same as type(), but keeps the value out of the report (passwords, card codes). */
    protected void typeSecret(By locator, String text) {
        Allure.step("Type ***** into " + describe(locator), () -> {
            enterText(locator, text);
        });
    }

    private void enterText(By locator, String text) {
        WebElement field = waitVisible(locator);
        field.clear();
        field.sendKeys(text);
        hideKeyboardIfShown();
    }

    /** "AppiumBy.accessibilityId: View cart" -> "accessibilityId: View cart" for readable step names. */
    private static String describe(By locator) {
        return locator.toString().replaceFirst("^AppiumBy\\.", "");
    }

    protected String textOf(By locator) {
        return waitVisible(locator).getText();
    }

    protected void hideKeyboardIfShown() {
        // Pattern matching instanceof (Java 16+): check the type and cast in one step.
        // Both AndroidDriver and IOSDriver implement these interfaces; AppiumDriver itself does not.
        if (driver instanceof HasOnScreenKeyboard keyboard && keyboard.isKeyboardShown()
                && driver instanceof HidesKeyboard hider) {
            hider.hideKeyboard();
        }
    }

    private WebDriverWait newWait(String what) {
        WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
        // The screen may re-render between findElements() and isDisplayed():
        // a stale reference means "try again on the next poll", not a failure.
        wait.ignoring(StaleElementReferenceException.class);
        wait.withMessage(() -> "waited " + TIMEOUT.toSeconds() + " s for " + what);
        return wait;
    }
}
