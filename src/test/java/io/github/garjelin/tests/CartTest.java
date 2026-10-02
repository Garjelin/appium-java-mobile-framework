package io.github.garjelin.tests;

import io.github.garjelin.pages.CartPage;
import io.github.garjelin.pages.CatalogPage;
import io.github.garjelin.pages.ProductPage;
import io.github.garjelin.support.Groups;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CartTest extends BaseTest {

    /**
     * Data for addedProductAppearsInCart: one test run per row.
     * Positions, not product names: names and order of the catalog may change,
     * positions on the first screen always exist (4 cards are visible on a phone).
     */
    @DataProvider(name = "productPositions")
    public Object[][] productPositions() {
        return new Object[][] {
                {0},
                {1},
                {2},
        };
    }

    @Test(dataProvider = "productPositions", groups = Groups.REGRESSION)
    public void addedProductAppearsInCart(int position) {
        CatalogPage catalog = new CatalogPage();
        // Take the name from the app instead of hardcoding it: the catalog content may change,
        // the test checks that WHATEVER we added is what the cart shows.
        String product = catalog.visibleProductTitles().get(position);
        catalog.openProduct(position);

        ProductPage productPage = new ProductPage();
        productPage.addToCart();
        productPage.openCart();

        CartPage cart = new CartPage();
        Assert.assertTrue(cart.containsProduct(product), "Cart should contain '" + product + "'");
    }
}
