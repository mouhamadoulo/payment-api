package com.molo.payment_api.auth.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {}
