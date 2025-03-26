package com.example.demo.util;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;
    private final long expirationTime = 86400000L; // 1 day in milliseconds

    // JWT 생성
    public String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // JWT에서 userId 추출
    public Long extractUserId(String token) {
        return Long.parseLong(
                Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .getSubject()
        );
    }

    // JWT가 만료되었는지 확인
    public boolean isTokenExpired(String token) {
        Date expirationDate = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        return expirationDate.before(new Date());
    }

    // HTTP 요청에서 JWT 추출 후 userId 반환
    public Long extractUserIdFromRequest(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null) {
            throw new JwtException("JWT가 필요합니다.");
        }
        return extractUserId(token);
    }

    // HTTP 요청에서 JWT 추출
    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        return token.substring(7); // "Bearer " 제거
    }

    // HTTP 요청에서 JWT 유효성 검증
    public void validateTokenFromRequest(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null || isTokenExpired(token)) {
            throw new JwtException("유효하지 않거나 만료된 토큰입니다.");
        }
    }
    // JWT가 유효한지 검사
    public boolean validateToken(String token, Long userId) {
        return (userId.toString().equals(extractUserId(token).toString()) && !isTokenExpired(token));
    }
}
