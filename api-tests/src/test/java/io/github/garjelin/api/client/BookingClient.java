package io.github.garjelin.api.client;

import io.github.garjelin.api.model.AuthRequest;
import io.github.garjelin.api.model.Booking;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * "API object": the API counterpart of a Page Object.
 * Knows endpoints, HTTP methods and how auth is passed; tests only call business operations.
 *
 * Methods return the raw Response on purpose: the TEST decides what to assert
 * (status, body, schema), including negative cases like "403 without a token".
 */
public class BookingClient {

    public Response ping() {
        return given().spec(ApiSpecs.base())
                .when().get("/ping");
    }

    public Response createToken(AuthRequest credentials) {
        return given().spec(ApiSpecs.base())
                .body(credentials) // serialized to JSON by Jackson
                .when().post("/auth");
    }

    public Response createBooking(Booking booking) {
        return given().spec(ApiSpecs.base())
                .body(booking)
                .when().post("/booking");
    }

    public Response getBooking(int bookingId) {
        return given().spec(ApiSpecs.base())
                .pathParam("id", bookingId)
                .when().get("/booking/{id}");
    }

    /** token == null sends the request without auth (for negative tests). */
    public Response updateBooking(int bookingId, Booking booking, String token) {
        return withToken(token)
                .pathParam("id", bookingId)
                .body(booking)
                .when().put("/booking/{id}");
    }

    public Response deleteBooking(int bookingId, String token) {
        return withToken(token)
                .pathParam("id", bookingId)
                .when().delete("/booking/{id}");
    }

    // This API takes the auth token as a cookie: "Cookie: token=<value>".
    private RequestSpecification withToken(String token) {
        RequestSpecification request = given().spec(ApiSpecs.base());
        return token == null ? request : request.cookie("token", token);
    }
}
