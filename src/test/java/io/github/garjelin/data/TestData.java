package io.github.garjelin.data;

/** Test data in one place: tests say WHAT data they use, not the raw values. */
public final class TestData {

    // Demo account listed on the app's login screen. The app accepts any credentials,
    // but using the documented account keeps the test meaningful if validation is added.
    public static final User STANDARD_USER = new User("bob@example.com", "10203040");

    public static final Address SHIPPING_ADDRESS = new Address(
            "Rebecca Winter", "Mandorley 112", "Entrance 1", "Truro", "89750", "Cornwall", "United Kingdom");

    public static final Card CARD = new Card("Rebecca Winter", "3258 1265 7568 7896", "03/30", "123");

    private TestData() {
    }
}
