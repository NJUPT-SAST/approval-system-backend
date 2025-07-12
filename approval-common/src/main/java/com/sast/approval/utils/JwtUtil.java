package com.sast.approval.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private static String secret;
    @Value("${jwt.expiration}")
    private static long expiration;
    public static String generateToken(String code) {
        Date now = new Date();
        Map<String, Object> claims = new HashMap<>();
        claims.put("code", code);
        return  Jwts.builder()
                .setIssuedAt(now)
                .setClaims(claims)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

    }
}
