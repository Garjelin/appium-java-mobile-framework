package io.github.garjelin.tests;

import io.appium.java_client.AppiumBy;
import io.github.garjelin.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

/**
 * Smoke test: the catalog opens and shows products with non-empty titles.
 * Runs unchanged on both platforms: ./gradlew test -Dplatform=android | -Dplatform=ios
 *
 * Session setup lives in BaseTest; driver creation in DriverFactory.
 * The locator switch and the wait helper below are temporary: Stage 2 moves them into Page Objects.
 */
public class FirstSessionTest extends BaseTest {

    // Upper bound for explicit waits. The app usually renders in 1-3 s;
    // 15 s leaves room for a cold device without hiding real hangs.
    private static final Duration TIMEOUT = Duration.ofSeconds(15);

    @Test
    public void catalogShowsProducts() {
        // Non-empty by construction: waitForVisible() times out instead of returning an empty list.
        List<WebElement> titles = waitForVisible(productTitles());

        // Check every visible title, not "the first product": catalog data and order change.
        for (WebElement title : titles) {
            Assert.assertFalse(title.getText().isBlank(), "Product title should not be blank");
        }

        System.out.println(Config.platform() + ": visible products = " + titles.size());
    }

    /**
     * Same meaning ("product title"), different accessibility id per platform:
     * Android content-desc = "Product Title", iOS name = "Product Name".
     * TODO(stage 2): move into CatalogPage.
     */
    private static By productTitles() {
        return switch (Config.platform()) {
            case ANDROID -> AppiumBy.accessibilityId("Product Title");
            case IOS -> AppiumBy.accessibilityId("Product Name");
        };
    }

    /**
     * Waits until AT LEAST ONE element is visible and returns the visible ones.
     *
     * Not ExpectedConditions.visibilityOfAllElementsLocatedBy: it requires ALL matches to be
     * visible, and a list can contain partly off-screen cells (especially on iOS), so it would
     * keep returning null until timeout.
     * TODO(stage 2): move into BasePage.
     */
    private List<WebElement> waitForVisible(By locator) {
        return new WebDriverWait(driver(), TIMEOUT)
                // The screen may re-render between findElements() and isDisplayed():
                // a stale element just means "try again on the next poll", not a failure.
                .ignoring(StaleElementReferenceException.class)
                .until(driver -> {
                    List<WebElement> visible = driver.findElements(locator).stream()
                            .filter(WebElement::isDisplayed)
                            .toList();
                    // null = condition not met yet, WebDriverWait polls again after 500 ms.
                    return visible.isEmpty() ? null : visible;
                });
    }
}
