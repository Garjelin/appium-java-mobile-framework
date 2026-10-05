package io.github.garjelin.api.model;

/**
 * Response of POST /auth. On success only "token" is set,
 * on failure only "reason" (e.g. "Bad credentials"); the other field stays null.
 */
public record AuthResponse(String token, String reason) {
}
