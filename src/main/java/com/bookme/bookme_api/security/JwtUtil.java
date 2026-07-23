package com.bookme.bookme_api.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expirationMs = 86400000; // 24 hours

    public JwtUtil() {
        // In production this comes from environment variables
        String secret = "my-super-secret-key-that-must-be-at-least-32-characters-long";
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Creates a token with the user's email and role inside
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    // Reads the email from inside a token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // Reads the role from inside a token
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    // Checks if the token is still valid (not expired, not tampered)
    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Internal method — decodes the token and returns its contents
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}