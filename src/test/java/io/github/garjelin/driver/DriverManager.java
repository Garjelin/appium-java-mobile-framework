package io.github.garjelin.driver;

import io.appium.java_client.AppiumDriver;

/**
 * Holds the Appium driver of the CURRENT THREAD.
 *
 * Why ThreadLocal: one driver = one Appium session = one device. When TestNG runs tests in
 * parallel, each thread must get its own driver; a plain static field would be shared by all
 * threads and they would send commands into each other's sessions.
 */
public final class DriverManager {

    private static final ThreadLocal<AppiumDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    /** Creates a session for the current thread. Called from BaseTest before each test. */
    public static void start() {
        if (DRIVER.get() != null) {
            throw new IllegalStateException(
                    "Driver already started in thread " + Thread.currentThread().getName());
        }
        DRIVER.set(DriverFactory.create());
    }

    /** Driver of the current thread. Fails loudly instead of returning null. */
    public static AppiumDriver driver() {
        AppiumDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "No driver in thread " + Thread.currentThread().getName()
                            + ". Does the test class extend BaseTest?");
        }
        return driver;
    }

    /** Ends the session (DELETE /session/<id>) and clears the thread slot. Safe to call twice. */
    public static void quit() {
        AppiumDriver driver = DRIVER.get();
        if (driver == null) {
            return; // start() failed or quit() already ran: nothing to close
        }
        try {
            driver.quit();
        } finally {
            // remove(), not set(null): TestNG reuses pool threads, and a leftover value would leak
            // a dead driver into the next test that happens to run on the same thread.
            DRIVER.remove();
        }
    }
}
