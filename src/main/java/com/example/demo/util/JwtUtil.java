package com.example.demo.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;
    private long expirationTime = 86400000L; // 1 day in milliseconds

    // JWT 생성
    public String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())  // userId를 문자열로 변환하여 설정
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // JWT에서 userId 추출
    public Long extractUserId(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();

        // JWT에서 userId 추출 (subject를 Long으로 변환)
        String userIdStr = jwtParser.parseClaimsJws(token)
                .getBody()
                .getSubject();

        return Long.parseLong(userIdStr);
    }

    // 토큰 유효성 검사
    public boolean isTokenExpired(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();

        Date expirationDate = jwtParser.parseClaimsJws(token)
                .getBody()
                .getExpiration();

        return expirationDate.before(new Date());
    }

    // JWT가 유효한지 검사
    public boolean validateToken(String token, Long userId) {
        return (userId.toString().equals(extractUserId(token).toString()) && !isTokenExpired(token));
    }
}
