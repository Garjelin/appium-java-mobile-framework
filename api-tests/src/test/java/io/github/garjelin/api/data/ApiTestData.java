package io.github.garjelin.api.data;

import io.github.garjelin.api.model.AuthRequest;
import io.github.garjelin.api.model.Booking;
import io.github.garjelin.api.model.BookingDates;

import java.util.UUID;

/** Test data for the Restful-Booker API. */
public final class ApiTestData {

    // Public demo credentials from the API documentation, not a secret.
    public static final AuthRequest ADMIN = new AuthRequest("admin", "password123");

    private ApiTestData() {
    }

    /**
     * A new booking with a unique last name. The API is a PUBLIC sandbox shared by everyone,
     * so tests create their own data instead of relying on existing ids that others may change or delete.
     */
    public static Booking newBooking(boolean depositPaid, String additionalNeeds) {
        String unique = UUID.randomUUID().toString().substring(0, 8);
        return new Booking("Rebecca", "Winter-" + unique, 150, depositPaid,
                new BookingDates("2026-11-01", "2026-11-05"), additionalNeeds);
    }

    public static Booking newBooking() {
        return newBooking(true, "Breakfast");
    }

    /** Same booking with different price and needs: records are immutable, so we build a new one. */
    public static Booking changed(Booking booking) {
        return new Booking(booking.firstName(), booking.lastName(), 999, booking.depositPaid(),
                booking.bookingDates(), "Late checkout");
    }
}
