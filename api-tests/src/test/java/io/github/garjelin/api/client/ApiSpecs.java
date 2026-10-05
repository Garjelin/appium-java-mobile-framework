package io.github.garjelin.api.client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Common request settings, defined once (like BasePage for UI):
 * base URI, JSON in/out, and an Allure filter that attaches every request and response to the report.
 */
public final class ApiSpecs {

    // Overridable for another environment: ./gradlew :api-tests:test -Dapi.baseUri=...
    // (needs forwarding in build.gradle.kts, like -Dplatform; not needed yet).
    private static final String BASE_URI =
            System.getProperty("api.baseUri", "https://restful-booker.herokuapp.com");

    static {
        // Console: full request + response only when an assertion on them fails.
        // Passing tests stay quiet, failing ones show everything needed to debug.
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private ApiSpecs() {
    }

    public static RequestSpecification base() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setContentType(ContentType.JSON)
                // Exactly "application/json". ContentType.JSON would send a LIST of JSON-like types
                // ("application/json, application/javascript, text/javascript, text/json"),
                // and this API answers 418 I'm a teapot to any Accept it does not support.
                .setAccept("application/json")
                .addFilter(new AllureRestAssured())
                .build();
    }
}
