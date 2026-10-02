package io.github.garjelin.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Product catalog: the first screen after app launch. */
public class CatalogPage extends BasePage {

    // Same meaning, different accessibility id: Android content-desc vs iOS name.
    private static final By PRODUCT_TITLES = byPlatform(
            accessibilityId("Product Title"),
            accessibilityId("Product Name"));

    // Identical on both platforms, so no byPlatform() needed.
    private static final By PRODUCT_IMAGES = accessibilityId("Product Image");

    public CatalogPage() {
        super(PRODUCT_TITLES);
    }

    /** Titles of the cards currently on screen (lists render only visible cards, not the whole catalog). */
    public List<String> visibleProductTitles() {
        return waitAllVisible(PRODUCT_TITLES).stream()
                .map(WebElement::getText)
                .toList();
    }

    /** Taps the first visible product. Its title is visibleProductTitles().get(0). Leads to ProductPage. */
    public void openFirstProduct() {
        openProduct(0);
    }

    /**
     * Taps the visible product at a position (0-based, in reading order).
     * Its title is visibleProductTitles().get(position). Leads to ProductPage.
     */
    public void openProduct(int position) {
        tap(PRODUCT_IMAGES, position);
    }
}
