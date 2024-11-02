package it.fartingbrains.fitness.pojo;

import java.time.Instant;

public class Token {
    private final String token;
    private final Instant issuedAt;
    private final Instant expiresAt;

    public Token(String token, Instant issuedAt, Instant expiresAt) {
        this.token = token;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public String getToken() { return token; }
    public Instant getIssuedAt() { return issuedAt; }
    public Instant getExpiresAt() { return expiresAt; }

    @Override
    public String toString() {
        return "Token{" +
                "token='" + token + '\'' +
                ", issuedAt=" + issuedAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
