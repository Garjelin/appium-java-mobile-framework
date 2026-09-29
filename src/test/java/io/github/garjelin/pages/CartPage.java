package io.github.garjelin.pages;

import io.appium.java_client.AppiumBy;
import io.github.garjelin.config.Config;
import org.openqa.selenium.By;

/** Cart with selected products and "Proceed To Checkout". */
public class CartPage extends BasePage {

    private static final By PROCEED_TO_CHECKOUT = byPlatform(
            accessibilityId("Confirms products for checkout"),
            iosButton("ProceedToCheckout"));

    public CartPage() {
        super(PROCEED_TO_CHECKOUT);
    }

    /**
     * Cart item titles have no accessibility id on iOS, so we look for an item with the exact name
     * using each platform's NATIVE query language:
     * Android - UiSelector (UiAutomator), iOS - NSPredicate (XCTest). Both are faster than XPath.
     */
    public boolean containsProduct(String productName) {
        By item = switch (Config.platform()) {
            case ANDROID -> AppiumBy.androidUIAutomator(
                    "new UiSelector().resourceId(\"com.saucelabs.mydemoapp.android:id/titleTV\")"
                            + ".text(\"" + productName + "\")");
            case IOS -> AppiumBy.iOSNsPredicateString(
                    "type == 'XCUIElementTypeStaticText' AND label == '" + productName + "'");
        };
        // The page is already loaded (constructor waited), so a non-waiting check is enough.
        return isVisibleNow(item);
    }

    public LoginPage proceedToCheckout() {
        tap(PROCEED_TO_CHECKOUT);
        return new LoginPage();
    }
}
