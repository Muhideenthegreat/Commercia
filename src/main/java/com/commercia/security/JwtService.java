package com.commercia.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private final Key signingKey;
  private final String issuer;
  private final long expirationMinutes;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.issuer}") String issuer,
      @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
    this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(encodeSecret(secret)));
    this.issuer = issuer;
    this.expirationMinutes = expirationMinutes;
  }

  public String generateToken(String subject, List<String> roles) {
    Instant now = Instant.now();
    Instant expiresAt = now.plusSeconds(expirationMinutes * 60);

    return Jwts.builder()
        .setIssuer(issuer)
        .setSubject(subject)
        .claim("roles", roles)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(expiresAt))
        .signWith(signingKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractUsername(String token) {
    return parseToken(token).getSubject();
  }

  public boolean isTokenValid(String token) {
    try {
      parseToken(token);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  private Claims parseToken(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(signingKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private String encodeSecret(String secret) {
    return java.util.Base64.getEncoder().encodeToString(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
  }
}
