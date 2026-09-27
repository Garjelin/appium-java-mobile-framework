package io.github.garjelin.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

/**
 * Single source of run configuration.
 * Platform comes from -Dplatform (default: android); everything else from
 * src/test/resources/config/<platform>.properties.
 */
public final class Config {

    private static final Platform PLATFORM =
            Platform.from(System.getProperty("platform", "android"));

    // Loaded once per JVM: static fields are initialized when the class is first used.
    private static final Properties PROPERTIES = load(PLATFORM);

    // Utility class: no instances.
    private Config() {
    }

    public static Platform platform() {
        return PLATFORM;
    }

    /** Required key: throws if it is missing, so a typo in a .properties file fails loudly. */
    public static String get(String key) {
        return find(key).orElseThrow(() -> new IllegalStateException(
                "Missing config key '" + key + "' in " + resourceName(PLATFORM)));
    }

    /** Optional key: e.g. platform.version exists for iOS but not for Android. */
    public static Optional<String> find(String key) {
        return Optional.ofNullable(PROPERTIES.getProperty(key))
                .map(String::trim)
                .filter(value -> !value.isEmpty());
    }

    private static Properties load(Platform platform) {
        String resource = resourceName(platform);
        // Read from the test classpath, so the code does not depend on absolute disk paths.
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Config file not found on classpath: " + resource);
            }
            Properties properties = new Properties();
            // Explicit UTF-8: Properties.load(InputStream) assumes ISO-8859-1.
            properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            return properties;
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read config file: " + resource, e);
        }
    }

    private static String resourceName(Platform platform) {
        return "config/" + platform.name().toLowerCase(Locale.ROOT) + ".properties";
    }
}
