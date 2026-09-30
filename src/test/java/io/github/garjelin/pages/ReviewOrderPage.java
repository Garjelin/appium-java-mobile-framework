package io.github.garjelin.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/** Checkout step 3: order summary and "Place Order". */
public class ReviewOrderPage extends BasePage {

    // TODO(verify on iOS): no iOS dump of this screen yet; matched by the visible button text.
    private static final By PLACE_ORDER = byPlatform(
            accessibilityId("Completes the process of checkout"),
            AppiumBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' AND label == 'Place Order'"));

    public ReviewOrderPage() {
        super(PLACE_ORDER);
    }

    /** Leads to CheckoutCompletePage. */
    public void placeOrder() {
        tap(PLACE_ORDER);
    }
}
