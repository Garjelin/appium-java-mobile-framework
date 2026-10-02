package io.github.garjelin.support;

import io.github.garjelin.config.Platform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test that cannot pass on a platform because of a KNOWN APP BUG.
 * BaseTest reads it before the session starts and skips the test on that platform
 * with the bug id in the report. Every id must be described in docs/known-issues.md.
 *
 * Why skip instead of deleting or commenting out the test: the report keeps showing it
 * as SKIPPED with the bug id, so nobody forgets to re-enable it when the bug is fixed.
 */
// RUNTIME: the annotation must survive compilation, so it can be read by reflection during the run.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface KnownIssue {

    /** Bug id from docs/known-issues.md, e.g. "KNOWN-1". */
    String id();

    /** Platform where the bug blocks the test. */
    Platform platform();

    /** One line for the report: what is broken. */
    String summary();
}
