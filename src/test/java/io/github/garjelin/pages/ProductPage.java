package io.github.garjelin.pages;

import org.openqa.selenium.By;

/** Product details: color, quantity, "Add to cart". */
public class ProductPage extends BasePage {

    private static final By ADD_TO_CART = byPlatform(
            accessibilityId("Tap to add product to cart"),
            iosButton("AddToCart"));

    // Android: cart icon in the top bar. iOS: "Cart" tab in the bottom tab bar.
    private static final By CART = byPlatform(
            accessibilityId("View cart"),
            accessibilityId("Cart-tab-item"));

    public ProductPage() {
        super(ADD_TO_CART);
    }

    public ProductPage addToCart() {
        tap(ADD_TO_CART);
        return this; // stay on the same screen: allows product.addToCart().openCart()
    }

    public CartPage openCart() {
        tap(CART);
        return new CartPage();
    }
}
