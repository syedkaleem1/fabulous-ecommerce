//package com.fabulous.filter;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//
//@Component
//public class JwtAuthFilter
//        extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {
//
//    @Value("${jwt.secret}")
//    private String jwtSecret;
//
//    public JwtAuthFilter() {
//        super(Config.class);
//    }
//
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            ServerHttpRequest request = exchange.getRequest();
//
//            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                return unauthorized(exchange, "Missing or malformed Authorization header");
//            }
//
//            String token = authHeader.substring(7);
//            try {
//                Claims claims = validateToken(token);
//
//                // Propagate user identity to downstream services as trusted headers
//                ServerHttpRequest mutatedRequest = request.mutate()
//                        .header("X-User-Email", claims.getSubject())
//                        .header("X-User-Role",  claims.get("role", String.class))
//                        .header("X-User-Id",    claims.get("userId", String.class))
//                        .build();
//
//                return chain.filter(exchange.mutate().request(mutatedRequest).build());
//
//            } catch (JwtException ex) {
//                return unauthorized(exchange, "Invalid or expired token");
//            }
//        };
//    }
//
//    private Claims validateToken(String token) {
//        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
//        return Jwts.parser()
//                .verifyWith(key)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//
//    private Mono<Void> unauthorized(ServerWebExchange exchange, String reason) {
//        ServerHttpResponse response = exchange.getResponse();
//        response.setStatusCode(HttpStatus.UNAUTHORIZED);
//        response.getHeaders().add("X-Auth-Error", reason);
//        return response.setComplete();
//    }
//
//    public static class Config {}
//}
//
