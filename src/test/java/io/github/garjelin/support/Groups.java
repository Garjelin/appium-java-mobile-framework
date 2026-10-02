package io.github.garjelin.support;

/**
 * Test groups. Run a subset with: ./gradlew test -Dgroups=smoke   (several: -Dgroups=smoke,e2e)
 * Constants instead of string literals in @Test(groups = ...): a typo becomes a compile error,
 * not a silently empty test run.
 */
public final class Groups {

    /** Fast check that the app starts and the main screen works. Run first / on every commit. */
    public static final String SMOKE = "smoke";

    /** Feature checks of single screens and flows. */
    public static final String REGRESSION = "regression";

    /** Full business flows across many screens. Slowest. */
    public static final String E2E = "e2e";

    private Groups() {
    }
}
