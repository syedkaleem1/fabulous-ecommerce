package com.fabulous.orchestrator.dto;

public record AuthResponse(
    String token,
    String firstName,
    String lastName,
    String mobileNumber,
    String email,
    String role,
    long expiresIn   // ms
) {}
