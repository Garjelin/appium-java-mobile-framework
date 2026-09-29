package io.github.garjelin.data;

/** Payment card form. Demo-app data only, not a real card. */
public record Card(String holderName, String number, String expirationDate, String securityCode) {
}
