// Build configuration for the Appium mobile test framework.
// This project contains ONLY tests: the app under test is a prebuilt binary in /apps.

plugins {
    // Plain Java plugin: compiles src/main and src/test, adds the `test` task.
    java
}

group = "io.github.garjelin"
version = "0.1.0"

java {
    // Toolchain pins the JDK used to compile and run tests, independent of
    // whatever `java` is on PATH. Keeps local runs and CI identical.
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Appium Java client: AndroidDriver, IOSDriver, AppiumBy, typed Options.
    // Brings Selenium as a transitive dependency.
    testImplementation("io.appium:java-client:10.1.1")

    // Test runner: annotations, assertions, parallel execution, DataProvider.
    testImplementation("org.testng:testng:7.12.0")

    // java-client logs through SLF4J; without a provider it prints a warning
    // and drops all logs. slf4j-simple writes them to the console.
    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.17")

    // Allure report: TestNG integration (steps, attachments, retries).
    // Its TestNG listener is registered automatically via Java ServiceLoader.
    testImplementation("io.qameta.allure:allure-testng:2.35.3")
}

tasks.test {
    // Tell Gradle to run tests with TestNG instead of the default JUnit.
    useTestNG {
        // Attaches RetryAnalyzer to every @Test (see support/RetryTransformer).
        listeners.add("io.github.garjelin.support.RetryTransformer")
    }

    // Max retries of a failed test: ./gradlew test -Dretries=0 disables them.
    systemProperty("retries", providers.systemProperty("retries").getOrElse("1"))

    // Where Allure writes raw results. Report: `allure serve build/allure-results`.
    systemProperty("allure.results.directory", layout.buildDirectory.dir("allure-results").get().asFile.absolutePath)

    // `./gradlew test -Dplatform=ios` sets the property in the Gradle JVM only.
    // Tests run in a separate forked JVM, so forward it explicitly (default: android).
    systemProperty("platform", providers.systemProperty("platform").getOrElse("android"))

    // UI tests depend on things Gradle cannot see (device, app build, Appium server),
    // so never treat the test task as UP-TO-DATE: `./gradlew test` always really runs.
    outputs.upToDateWhen { false }

    // Print each test result and full stack traces in the terminal,
    // so failures are readable without opening the HTML report.
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }
}