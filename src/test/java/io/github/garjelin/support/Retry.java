package io.github.garjelin.support;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Re-runs a failed test (default: once; ./gradlew test -Dretries=0 turns it off).
 *
 * A retry is a TEMPORARY measure, not a fix: it keeps the pipeline green while a flaky test
 * is investigated. Every retried test stays visible in the Allure report (Retries tab),
 * so flakiness is tracked, not hidden. A test that needs retries regularly must be fixed:
 * wrong wait, test data dependency, or a real bug in the app.
 */
public class Retry implements IRetryAnalyzer {

    private static final int MAX_RETRIES = Integer.getInteger("retries", 1);

    // TestNG creates one analyzer per test method, so this counter is per test.
    private int retries = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retries >= MAX_RETRIES) {
            return false;
        }
        retries++;
        System.out.println("[retry] " + result.getName() + " failed, retry " + retries + "/" + MAX_RETRIES
                + ": " + result.getThrowable());
        return true;
    }
}
