package io.github.garjelin.config;

import java.util.Locale;

/** Target mobile platform of the current test run. */
public enum Platform {
    ANDROID,
    IOS;

    /**
     * Parses user input like "android", "iOS", " IOS " into a Platform.
     * Fails fast with a readable message instead of silently falling back to a default.
     */
    public static Platform from(String value) {
        try {
            // Locale.ROOT: toUpperCase() depends on the OS locale; in Turkish "ios" would become "İOS".
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unknown platform '" + value + "'. Supported values: android, ios", e);
        }
    }
}
