package com.supplyhouse.account_management.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTHelper {

    //Token valid for 1 hour
    private static final long JWT_TOKEN_VALIDITY_IN_MILLI_SECS = 3600000;

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    /**
     * Retrieve username from jwt token
     * @param token jwt token
     * @return userName of account
     */
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    /**
     * Extracts a specific claim from the JWT token.
     * @param token jwt token
     * @param claimResolver A function to extract the claim.
     * @return The value of the specified claim.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        // Extract the specified claim using the provided function
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // Parse and return all claims from the token
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build().parseClaimsJws(token).getBody();
    }

    /**
     * Creates a signing key from the base64 encoded secret.
     * @return a Key object for signing the JWT.
     */
    private Key getSigningKey() {
        // Decode the base64 encoded secret key and return a Key object
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generate JWT Token for the given user name
     * @param authentication Authentication object after being authenticated
     * @return JWT Token
     */
    /**
     * Generate JWT Token for the given user name
     * @param userName userName of Account
     * @return JWT Token
     */
    public String generateToken(String userName) {
        // Prepare claims for the token
        Map<String, Object> claims = new HashMap<>();
       /* Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        authorities*/
        // Build JWT token with claims, subject, issued time, expiration time, and signing algorithm
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY_IN_MILLI_SECS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    /**
     * Extracts the expiration date from the JWT token.
     * @param token jwtToken
     * @return The expiration date of the token.
     */
    public Date getExpirationDateFromToken(String token) {
        // Extract and return the expiration claim from the token
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Checks if the JWT token is expired.
     * @param token jwt token
     * @return true if the token is expired, false otherwise.
     */
    public boolean isTokenExpired(String token) {
        // Check if the token's expiration time is before the current time
        return getExpirationDateFromToken(token).before(new Date());
    }

    /**
     * Validates the JWT token against the UserDetails.
     * @param token jwt token
     * @param userDetails userDetails from the database
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        // Extract username from token and check if it matches UserDetails' username
        final String userName = getUsernameFromToken(token);
        // Also check if the token is expired
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
