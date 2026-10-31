package com.molo.payment_api.dto.response;

import com.molo.payment_api.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

//DTO pour les données retournées au Client
public record PaymentResponse(

        Long id,
        Long version,
        String reference,
        String debtorIban,
        String creditorIban,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {}

