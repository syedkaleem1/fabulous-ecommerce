//package com.fabulous.util;
//
//import io.jsonwebtoken.Jwts;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//
//@Component
//public class JwtUtil {
//
//    private static final Key SECRET_KEY = "ecommerce-secret-key";
//
//    public void validateToken(String token) {
//        Jwts.parser()
//                .verifyWith(SECRET_KEY)
//                .parseS(token);
//    }
//}
//
//
