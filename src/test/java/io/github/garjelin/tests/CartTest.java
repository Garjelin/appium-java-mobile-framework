package io.github.garjelin.tests;

import io.github.garjelin.pages.CartPage;
import io.github.garjelin.pages.CatalogPage;
import io.github.garjelin.pages.ProductPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CartTest extends BaseTest {

    @Test
    public void addedProductAppearsInCart() {
        CatalogPage catalog = new CatalogPage();
        // Take the name from the app instead of hardcoding it: the catalog content may change,
        // the test checks that WHATEVER we added is what the cart shows.
        String product = catalog.visibleProductTitles().get(0);
        catalog.openFirstProduct();

        ProductPage productPage = new ProductPage();
        productPage.addToCart();
        productPage.openCart();

        CartPage cart = new CartPage();
        Assert.assertTrue(cart.containsProduct(product), "Cart should contain '" + product + "'");
    }
}
