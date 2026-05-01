package com.fabulous.orchestrator.controller;

import com.fabulous.orchestrator.dto.AuthResponse;
import com.fabulous.orchestrator.dto.LoginRequest;
import com.fabulous.orchestrator.dto.RegisterRequest;
import com.fabulous.orchestrator.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** POST /api/auth/register — creates account, returns JWT */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        logger.info("Inside register endpoint of AuthController with email: {}", req.email());

        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(req));
    }

    /** POST /api/auth/login — validates credentials, returns JWT */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        logger.info("Inside login endpoint of AuthController with email: {}", req.email());

        return ResponseEntity.ok(authService.login(req));
    }
}
