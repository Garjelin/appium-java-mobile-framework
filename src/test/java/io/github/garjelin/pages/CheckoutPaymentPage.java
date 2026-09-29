package io.github.garjelin.pages;

import io.github.garjelin.data.Card;
import org.openqa.selenium.By;

/** Checkout step 2: payment card. */
public class CheckoutPaymentPage extends BasePage {

    private static final By SCREEN = byPlatform(
            androidId("cardNumberET"),
            accessibilityId("Payment-screen"));

    // iOS: positions follow page-sources/ios/10-payment.xml.
    private static final By HOLDER_NAME = byPlatform(androidId("nameET"), iosTextField(1));
    private static final By CARD_NUMBER = byPlatform(androidId("cardNumberET"), iosTextField(2));
    private static final By EXPIRATION_DATE = byPlatform(androidId("expirationDateET"), iosTextField(3));
    private static final By SECURITY_CODE = byPlatform(androidId("securityCodeET"), iosTextField(4));

    // Android reuses the id "paymentBtn" for the main button of every checkout step.
    // Safe here: the constructor has already confirmed we are on the payment screen.
    private static final By REVIEW_ORDER = byPlatform(
            androidId("paymentBtn"),
            iosButton("Review Order"));

    public CheckoutPaymentPage() {
        super(SCREEN);
    }

    public CheckoutPaymentPage fillCard(Card card) {
        type(HOLDER_NAME, card.holderName());
        type(CARD_NUMBER, card.number());
        type(EXPIRATION_DATE, card.expirationDate());
        type(SECURITY_CODE, card.securityCode());
        return this;
    }

    public ReviewOrderPage reviewOrder() {
        tap(REVIEW_ORDER);
        return new ReviewOrderPage();
    }
}
