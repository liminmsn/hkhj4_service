package com.hkhj4;

import com.hkhj4.entity.TbUser;
import com.hkhj4.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest
class Hkhj4ServiceApplicationTests {
    @Resource(name = "userMapper")
    UserMapper userMapper;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Test
    void contextLoads() {
        Map<String, Object> map = new HashMap<>();
        map.put("email", "hkhj4");
        map.put("password", "hkhj4");

        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        String jwt = Jwts.builder()
                .claims(map)
                .expiration(new Date(System.currentTimeMillis()))
                .signWith(secretKey)
                .compact();
        System.out.println(jwt);
    }

    @Test
    void generateToken() {
        SecretKey secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims("eyJhbGciOiJIUzI1NiJ9.eyJwYXNzd29yZCI6ImhraGo0IiwiZW1haWwiOiJoa2hqNCIsImV4cCI6MTc3MTYwNjQyNX0.I-v8TwiUwau4Zs6MeoKvY5WJtXIMB1iDZOimcad_Ojs")
                .getPayload();
        System.out.println(claims);
    }

    @Test
    void getUserInfo() {
        TbUser user = userMapper.getUserInfo("1772081518@qq.com2", "123456");
        log.info("user={}", user);
    }
}
