package io.github.garjelin.support;

import io.github.garjelin.config.Config;
import io.github.garjelin.config.Platform;
import org.testng.SkipException;

/**
 * Skips a test on a platform where a KNOWN APP BUG makes it impossible to pass.
 *
 * Why skip instead of deleting or commenting out the test:
 * the report keeps showing the test as SKIPPED with the bug id, so nobody forgets
 * to re-enable it when the bug is fixed. Every id must be described in docs/known-issues.md.
 *
 * TODO(stage 3): replace with an annotation @KnownIssue handled by a TestNG listener.
 */
public final class KnownIssues {

    private KnownIssues() {
    }

    public static void skipOn(Platform platform, String issueId, String summary) {
        if (Config.platform() == platform) {
            throw new SkipException(issueId + " on " + platform + ": " + summary
                    + " (see docs/known-issues.md)");
        }
    }
}
