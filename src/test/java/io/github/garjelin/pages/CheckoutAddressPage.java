package io.github.garjelin.pages;

import io.github.garjelin.data.Address;
import org.openqa.selenium.By;

/** Checkout step 1: shipping address. */
public class CheckoutAddressPage extends BasePage {

    private static final By SCREEN = byPlatform(
            androidId("fullNameET"),
            accessibilityId("ShippingAddress-screen"));

    // iOS: fields have no ids; positions follow the tree order in page-sources/ios/08-address.xml.
    private static final By FULL_NAME = byPlatform(androidId("fullNameET"), iosTextField(1));
    private static final By ADDRESS_LINE_1 = byPlatform(androidId("address1ET"), iosTextField(2));
    private static final By ADDRESS_LINE_2 = byPlatform(androidId("address2ET"), iosTextField(3));
    private static final By CITY = byPlatform(androidId("cityET"), iosTextField(4));
    private static final By ZIP_CODE = byPlatform(androidId("zipET"), iosTextField(5));
    private static final By STATE = byPlatform(androidId("stateET"), iosTextField(6));
    private static final By COUNTRY = byPlatform(androidId("countryET"), iosTextField(7));

    private static final By TO_PAYMENT = byPlatform(
            accessibilityId("Saves user info for checkout"),
            iosButton("To Payment"));

    public CheckoutAddressPage() {
        super(SCREEN);
    }

    public CheckoutAddressPage fillShippingAddress(Address address) {
        type(FULL_NAME, address.fullName());
        type(ADDRESS_LINE_1, address.addressLine1());
        typeIfPresent(ADDRESS_LINE_2, address.addressLine2());
        type(CITY, address.city());
        type(ZIP_CODE, address.zipCode());
        typeIfPresent(STATE, address.state());
        type(COUNTRY, address.country());
        return this;
    }

    public CheckoutPaymentPage toPayment() {
        tap(TO_PAYMENT);
        return new CheckoutPaymentPage();
    }

    /** Optional fields: skip when the test data leaves them empty. */
    private void typeIfPresent(By field, String value) {
        if (value != null && !value.isBlank()) {
            type(field, value);
        }
    }
}
