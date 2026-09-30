package io.github.garjelin.tests;

import io.github.garjelin.config.Platform;
import io.github.garjelin.data.TestData;
import io.github.garjelin.pages.CatalogPage;
import io.github.garjelin.pages.CheckoutCompletePage;
import io.github.garjelin.support.KnownIssues;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CheckoutTest extends BaseTest {

    /**
     * Main business flow, end to end. Reads as the user's journey because every step
     * returns the page it leads to (fluent Page Objects), and every page waits for itself.
     */
    @Test
    public void userCompletesPurchase() {
        KnownIssues.skipOn(Platform.IOS, "KNOWN-1", "on-screen keyboard cannot be closed and covers checkout buttons");

        CheckoutCompletePage complete = new CatalogPage()
                .openFirstProduct()
                .addToCart()
                .openCart()
                .proceedToCheckout()
                .loginAs(TestData.STANDARD_USER)
                .fillShippingAddress(TestData.SHIPPING_ADDRESS)
                .toPayment()
                .fillCard(TestData.CARD)
                .reviewOrder()
                .placeOrder();

        // Fixed UI copy, identical on both platforms: fine to assert exactly (unlike catalog data).
        Assert.assertEquals(complete.title(), "Checkout Complete");
    }
}
