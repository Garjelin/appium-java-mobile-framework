package io.github.garjelin.tests;

import io.github.garjelin.config.Platform;
import io.github.garjelin.data.TestData;
import io.github.garjelin.pages.CartPage;
import io.github.garjelin.pages.CatalogPage;
import io.github.garjelin.pages.CheckoutAddressPage;
import io.github.garjelin.pages.CheckoutCompletePage;
import io.github.garjelin.pages.CheckoutPaymentPage;
import io.github.garjelin.pages.LoginPage;
import io.github.garjelin.pages.ProductPage;
import io.github.garjelin.pages.ReviewOrderPage;
import io.github.garjelin.support.KnownIssue;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CheckoutTest extends BaseTest {

    /**
     * Main business flow, end to end.
     * Every screen is created explicitly after the action that leads to it:
     * the test shows where it is, and each "new XxxPage()" waits for that screen,
     * so a broken navigation fails at the exact step.
     */
    @Test
    @KnownIssue(id = "KNOWN-1", platform = Platform.IOS,
            summary = "on-screen keyboard cannot be closed and covers checkout buttons")
    public void userCompletesPurchase() {
        CatalogPage catalog = new CatalogPage();
        catalog.openFirstProduct();

        ProductPage product = new ProductPage();
        product.addToCart();
        product.openCart();

        CartPage cart = new CartPage();
        cart.proceedToCheckout();

        LoginPage login = new LoginPage();
        login.loginAs(TestData.STANDARD_USER);

        CheckoutAddressPage address = new CheckoutAddressPage();
        address.fillShippingAddress(TestData.SHIPPING_ADDRESS);
        address.toPayment();

        CheckoutPaymentPage payment = new CheckoutPaymentPage();
        payment.fillCard(TestData.CARD);
        payment.reviewOrder();

        ReviewOrderPage review = new ReviewOrderPage();
        review.placeOrder();

        CheckoutCompletePage complete = new CheckoutCompletePage();
        // Fixed UI copy, identical on both platforms: fine to assert exactly (unlike catalog data).
        Assert.assertEquals(complete.title(), "Checkout Complete");
    }
}
