// API tests module: REST API checks with RestAssured. No Appium, no devices needed.
// Run only this module: ./gradlew :api-tests:test

plugins {
    java
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // HTTP client + given/when/then DSL for API tests.
    testImplementation("io.rest-assured:rest-assured:5.5.7")
    // Response validation against a JSON Schema (contract check).
    testImplementation("io.rest-assured:json-schema-validator:5.5.7")
    // JSON <-> Java objects (records). RestAssured picks Jackson up automatically when present.
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.19.2")

    testImplementation("org.testng:testng:7.12.0")

    // Allure + a RestAssured filter that attaches every request/response to the report.
    testImplementation("io.qameta.allure:allure-testng:2.35.3")
    testImplementation("io.qameta.allure:allure-rest-assured:2.35.3")

    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.17")
}

tasks.test {
    useTestNG()

    // Same results folder as the UI tests: one Allure report for UI + API.
    systemProperty(
        "allure.results.directory",
        rootProject.layout.buildDirectory.dir("allure-results").get().asFile.absolutePath
    )

    // The API under test is external: its state is not a Gradle input, always really run.
    outputs.upToDateWhen { false }

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }
}
