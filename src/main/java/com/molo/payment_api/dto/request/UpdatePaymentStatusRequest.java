package com.molo.payment_api.dto.request;

import com.molo.payment_api.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;

//DTO utilisé lors du changement de statut d'un paiement
public record UpdatePaymentStatusRequest(

        @NotNull
        PaymentStatus status

) {}
