package io.github.garjelin.support;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * TestNG listener that sets Retry on EVERY @Test at startup,
 * instead of writing @Test(retryAnalyzer = Retry.class) on each test.
 * Registered in build.gradle.kts: useTestNG { listeners.add(...) }.
 */
public class RetryTransformer implements IAnnotationTransformer {

    @Override
    @SuppressWarnings("rawtypes") // the TestNG interface itself uses raw Class / Constructor
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(Retry.class);
    }
}
