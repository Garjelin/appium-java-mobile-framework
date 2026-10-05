package io.github.garjelin.api.tests;

import io.github.garjelin.api.client.BookingClient;
import io.github.garjelin.api.data.ApiTestData;
import io.github.garjelin.api.model.AuthRequest;
import io.github.garjelin.api.model.AuthResponse;
import io.github.garjelin.api.model.Booking;
import io.github.garjelin.api.model.CreatedBooking;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.greaterThan;

/**
 * CRUD checks of the Restful-Booker API (public sandbox: https://restful-booker.herokuapp.com).
 * API quirks (201 on DELETE, 200 on failed login, ...) are listed in docs/known-issues.md.
 */
public class BookingApiTest {

    private final BookingClient client = new BookingClient();

    // Bookings created by the current test, deleted after it: a shared sandbox must not fill up
    // with our test data, and tests must not depend on each other's leftovers.
    private final List<Integer> createdBookingIds = new ArrayList<>();

    private String adminToken;

    @AfterMethod(alwaysRun = true)
    public void deleteCreatedBookings() {
        for (int id : createdBookingIds) {
            // Best effort: some tests already deleted their booking; the response is ignored.
            client.deleteBooking(id, adminToken());
        }
        createdBookingIds.clear();
    }

    @Test
    public void apiIsUp() {
        // Health check. The API answers 201 Created to GET /ping (quirk, 200 OK would be standard).
        client.ping()
                .then()
                .statusCode(201);
    }

    @DataProvider(name = "bookings")
    public Object[][] bookings() {
        return new Object[][] {
                {ApiTestData.newBooking(true, "Breakfast")},
                {ApiTestData.newBooking(false, "Late checkout")},
        };
    }

    @Test(dataProvider = "bookings")
    public void createdBookingCanBeReadBack(Booking booking) {
        // given / when / then: RestAssured's BDD-style DSL.
        CreatedBooking created = client.createBooking(booking)
                .then()
                .statusCode(200)
                .body("bookingid", greaterThan(0))  // check a JSON field by path, with a Hamcrest matcher
                .extract().as(CreatedBooking.class); // JSON -> record (Jackson)
        createdBookingIds.add(created.bookingId());

        // Records compare all fields in equals(): "stored exactly what we sent" in one line.
        Assert.assertEquals(created.booking(), booking, "POST /booking response");

        Booking stored = client.getBooking(created.bookingId())
                .then()
                .statusCode(200)
                .extract().as(Booking.class);
        Assert.assertEquals(stored, booking, "GET /booking/{id} response");
    }

    @Test
    public void bookingMatchesContract() {
        int id = createBooking(ApiTestData.newBooking());

        // Contract check: field names, types, required fields and date format,
        // independent of the concrete values (schema: src/test/resources/schemas/booking.json).
        client.getBooking(id)
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking.json"));
    }

    @Test
    public void updateWithoutTokenIsForbidden() {
        Booking booking = ApiTestData.newBooking();
        int id = createBooking(booking);

        client.updateBooking(id, ApiTestData.changed(booking), null)
                .then()
                .statusCode(403);
    }

    @Test
    public void updateWithTokenChangesBooking() {
        Booking booking = ApiTestData.newBooking();
        int id = createBooking(booking);
        Booking changed = ApiTestData.changed(booking);

        Booking updated = client.updateBooking(id, changed, adminToken())
                .then()
                .statusCode(200)
                .extract().as(Booking.class);
        Assert.assertEquals(updated, changed, "PUT /booking/{id} response");

        // Check the stored state too, not only the PUT response.
        Booking stored = client.getBooking(id)
                .then()
                .statusCode(200)
                .extract().as(Booking.class);
        Assert.assertEquals(stored, changed, "GET after PUT");
    }

    @Test
    public void deletedBookingIsNotFound() {
        int id = createBooking(ApiTestData.newBooking());

        client.deleteBooking(id, adminToken())
                .then()
                .statusCode(201); // quirk: 201 Created for a delete (200 / 204 would be standard)

        client.getBooking(id)
                .then()
                .statusCode(404);
    }

    @Test
    public void wrongPasswordGivesNoToken() {
        AuthResponse response = client.createToken(new AuthRequest("admin", "wrong-password"))
                .then()
                // Status NOT asserted: the API answers 200 OK to a failed login (KNOWN-2).
                // Asserting 200 would turn the bug into "expected behavior".
                .extract().as(AuthResponse.class);

        Assert.assertNull(response.token(), "No token for wrong credentials");
        Assert.assertEquals(response.reason(), "Bad credentials");
    }

    private int createBooking(Booking booking) {
        int id = client.createBooking(booking)
                .then()
                .statusCode(200)
                .extract().as(CreatedBooking.class)
                .bookingId();
        createdBookingIds.add(id);
        return id;
    }

    /** Token is requested once per test class and reused. */
    private String adminToken() {
        if (adminToken == null) {
            adminToken = client.createToken(ApiTestData.ADMIN)
                    .then()
                    .statusCode(200)
                    .extract().as(AuthResponse.class)
                    .token();
        }
        return adminToken;
    }
}
