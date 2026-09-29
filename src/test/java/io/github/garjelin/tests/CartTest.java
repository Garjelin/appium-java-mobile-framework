package io.github.garjelin.tests;

import io.github.garjelin.pages.CartPage;
import io.github.garjelin.pages.CatalogPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CartTest extends BaseTest {

    @Test
    public void addedProductAppearsInCart() {
        CatalogPage catalog = new CatalogPage();
        // Take the name from the app instead of hardcoding it: the catalog content may change,
        // the test checks that WHATEVER we added is what the cart shows.
        String product = catalog.visibleProductTitles().get(0);

        CartPage cart = catalog.openFirstProduct()
                .addToCart()
                .openCart();

        Assert.assertTrue(cart.containsProduct(product), "Cart should contain '" + product + "'");
    }
}
