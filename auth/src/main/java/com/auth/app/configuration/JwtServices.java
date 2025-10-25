package com.auth.app.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtServices {
    @Value("${jwt.secret_key}")
    private String secretKey;
    @Value("${jwt.refresh_expiry}")
    private long refreshExpiry;
    @Value("${jwt.access_expiry}")
    private long accessExpiry;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.audience}")
    private String audience;


    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        var username = extractUserName(token);
        return username.equals(userDetails.getUsername()) && isTokenExpired(token);

    }

    private boolean isTokenExpired(String token) {
        var expired = extractClaim(token, Claims::getExpiration);
        return expired.after(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsFunction) {
        Claims claims = extractClaims(token);
        return claimsFunction.apply(claims);
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSecretKey())
                .build().parseClaimsJws(token)
                .getBody();
    }

    public String generateRefreshToken(UserDetails userDetails, Map<String, Object> claims) {
        return buildToken(userDetails, claims, refreshExpiry);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, new HashMap<>(), refreshExpiry);
    }

    public String generateAccessToken(UserDetails userDetails, Map<String, Object> claims) {
        return buildToken(userDetails, claims, accessExpiry);
    }

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(userDetails, new HashMap<>(), accessExpiry);
    }


    private String buildToken(UserDetails userDetails, Map<String, Object> claims, long expiry) {

        Date expired = new Date(System.currentTimeMillis() + expiry);
        var jti = UUID.randomUUID().toString();
        claims.put("jti", jti);
        return Jwts.builder().signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .setClaims(claims)
                .setAudience(audience)
                .setIssuer(issuer)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expired)
                .compact();
    }

    private Key getSecretKey() {
        byte[] key = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(key);
    }
}
