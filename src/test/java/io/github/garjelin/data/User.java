package io.github.garjelin.data;

/** Login credentials. A record (Java 16+): immutable data holder with generated getters. */
public record User(String username, String password) {
}
