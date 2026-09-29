package io.github.garjelin.pages;

import io.appium.java_client.AppiumBy;
import io.github.garjelin.data.User;
import org.openqa.selenium.By;

/** Login screen, shown on checkout when the user is not logged in. */
public class LoginPage extends BasePage {

    // iOS fields have no accessibility id, but they are the only (secure) text fields on the screen.
    private static final By USERNAME = byPlatform(
            androidId("nameET"),
            iosTextField(1));

    private static final By PASSWORD = byPlatform(
            androidId("passwordET"),
            AppiumBy.iOSClassChain("**/XCUIElementTypeSecureTextField[1]"));

    // iOS: the screen title is also named "Login", hence iosButton() instead of accessibilityId().
    private static final By LOGIN = byPlatform(
            accessibilityId("Tap to login with given credentials"),
            iosButton("Login"));

    public LoginPage() {
        super(USERNAME);
    }

    public CheckoutAddressPage loginAs(User user) {
        type(USERNAME, user.username());
        type(PASSWORD, user.password());
        tap(LOGIN);
        return new CheckoutAddressPage();
    }
}
