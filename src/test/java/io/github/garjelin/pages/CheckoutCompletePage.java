package io.github.garjelin.pages;

import org.openqa.selenium.By;

/** Final screen after a successful order. */
public class CheckoutCompletePage extends BasePage {

    private static final By TITLE = byPlatform(
            androidId("completeTV"),
            accessibilityId("Checkout Complete"));

    public CheckoutCompletePage() {
        super(TITLE);
    }

    public String title() {
        return textOf(TITLE);
    }
}
