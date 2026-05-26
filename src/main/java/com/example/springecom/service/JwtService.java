package com.example.springecom.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    private static String SECRET = "";

    //generate a new key for every server restart
    public JwtService() {
        try {
            javax.crypto.KeyGenerator keyGen = javax.crypto.KeyGenerator.getInstance("HmacSHA256");
            javax.crypto.SecretKey sk = keyGen.generateKey();
            SECRET = java.util.Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private Key getkey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 3)) // Token valid for 3 minuties
                .signWith(getkey())
                .compact();

    }


    public boolean validateToken(String token) {
        // Implement JWT token validation logic here
        return token.startsWith("dummy-jwt-token-for-");
    }

    public String extractUsername(String token) {
        // Implement logic to extract username from the token
        if (validateToken(token)) {
            return token.substring("dummy-jwt-token-for-".length());
        }
        return null;
    }
}
