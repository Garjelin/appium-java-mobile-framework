package io.github.garjelin.api.model;

/** Body of POST /auth. Field names already match the JSON, no mapping needed. */
public record AuthRequest(String username, String password) {
}
