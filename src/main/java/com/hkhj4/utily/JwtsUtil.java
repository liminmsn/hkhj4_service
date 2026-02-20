package com.hkhj4.utily;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

@Component
public class JwtsUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expire}")
    private Long jwtExpire;

    /**
     * 解析token
     */
    public Claims parseJwt(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 生成token
     */
    public String generateJwt(HashMap<String, ?> map) {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claims(map)
                .expiration(new Date(System.currentTimeMillis() + jwtExpire))
                .signWith(secretKey)
                .compact();
    }
}
