package com.example.demo.configJWT;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

import io.jsonwebtoken.security.Keys;
import java.security.Key;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")//luu key o application.properties
    private String SECRET_KEY ; // secret key 32bit

//    private final long EXPIRATION_TIME = 86400000; // 1 ngày
    private final long EXPIRATION_TIME = 1800000; //30 phut, 1000=1s
//    Chuyển chuỗi SECRET_KEY thành một Key object để ký JWT bằng thuật toán HMAC SHA-256
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)); // Giải mã Base64 trước khi tạo key
    }
    // Tạo token gồm username và role
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role) // Thêm role vào JWT
                .setIssuedAt(new Date()) // Ngày cấp
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //Ngày hết han
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) //ký với khao bi mat
                .compact();// Chuyển JWT thành String Base64Url
    }

    // Lấy username từ token
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token) //Giải mã và kiểm tra chữ ký của token.
                .getBody()
                .getSubject(); //Trả về username (chủ thể)
    }

    // Lấy role từ token
    public String getRoleFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    // Kiểm tra token hợp lệ nếu token hợp lệ (chữ ký đúng, chưa hết hạn) → trả về true.
    //Nếu token bị chỉnh sửa hoặc hết hạn → bắt lỗi JwtException → trả về false.
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}

