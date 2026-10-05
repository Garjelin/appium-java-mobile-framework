rootProject.name = "appium-java-mobile-framework"

// API tests live in their own module: own dependencies (RestAssured, no Appium),
// can run without devices: ./gradlew :api-tests:test
include("api-tests")
