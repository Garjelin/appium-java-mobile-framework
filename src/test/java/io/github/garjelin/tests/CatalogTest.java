package io.github.garjelin.tests;

import io.github.garjelin.pages.CatalogPage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/** Catalog smoke test. Evolved from FirstSessionTest: locators and waits now live in CatalogPage. */
public class CatalogTest extends BaseTest {

    @Test
    public void catalogShowsProducts() {
        // Non-empty by construction: the page waits until at least one title is visible.
        List<String> titles = new CatalogPage().visibleProductTitles();

        // Check every visible title, not "the first product": catalog data and order change.
        for (String title : titles) {
            Assert.assertFalse(title.isBlank(), "Product title should not be blank");
        }
    }
}
