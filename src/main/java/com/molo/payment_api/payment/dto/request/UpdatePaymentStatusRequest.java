package com.molo.payment_api.payment.dto.request;

import com.molo.payment_api.payment.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;

//DTO utilisé lors du changement de statut d'un paiement
public record UpdatePaymentStatusRequest(

        @NotNull
        PaymentStatus status

) {}
