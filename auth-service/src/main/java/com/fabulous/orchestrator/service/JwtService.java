package com.fabulous.orchestrator.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Issues JWTs signed with the same secret the API Gateway uses to verify them.
 * Claims forwarded downstream as headers:
 * sub        → X-User-Email
 * role       → X-User-Role
 * userId     → X-User-Id
 */
@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiry-ms}")
    private long expiryMs;

    /**
     * Generates a JWT token for the given user name by creating an empty claims map and calling the createToken method with the claims and user name.
     * The createToken method builds the JWT token by setting the claims, subject (user name), issued date, expiration date, and signing it with the secret key.
     * The token is valid for 30 minutes as specified by the expiryMs property.
     * The generated token can then be used for authentication and authorization in the application.
     *
     * @param userName - as Email
     * @return
     */
    public String generateToken(String userName) {
        logger.info("Generating JWT token for user: {}", userName);
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    /**
     * Creates a JWT token with the specified claims and subject (user name) by building the token using the Jwts.builder() method.
     * The token is signed with the secret key and has an expiration time set to 30
     * minutes from the current time. The claims can include additional information about the user, such as roles or permissions, which can be used for authorization purposes in the application.
     * The generated token can then be returned to the client and included in subsequent requests for authentication and authorization.
     *
     * @param claims
     * @param userName
     * @return
     */
    private String createToken(Map<String, Object> claims, String userName) {
        logger.info("Creating JWT token for user: {}", userName);
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .claims(claims)
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date(now + expiryMs)) // Token valid for 30 minutes
                .signWith(key)
                .compact();
    }

    /**
     * Extracts the username (subject) from the JWT token by parsing it with the signing key and retrieving the "sub" claim.
     *
     * @param token
     * @return
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the JWT token by parsing it with the signing key and retrieving the "exp" claim.
     *
     * @param token
     * @return
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the JWT token by parsing it with the signing key and applying the provided claims resolver function to the claims.
     * The claims resolver is a function that takes the Claims object and returns the desired claim value (e.g., subject, expiration).
     *
     * @param token
     * @param claimsResolver
     * @param <T>
     * @return
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token by parsing it with the signing key.
     *
     * @param token
     * @return
     */
    private Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Checks if the token is expired by comparing the expiration date with the current date.
     *
     * @param token
     * @return
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validates the token by checking if the username matches and if the token is not expired.
     *
     * @param token
     * @param userDetails
     * @return
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
