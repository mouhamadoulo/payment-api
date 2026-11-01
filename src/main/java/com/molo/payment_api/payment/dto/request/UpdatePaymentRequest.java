package com.molo.payment_api.payment.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

//DTO utilisé lors de la modification d'un paiement
public record UpdatePaymentRequest(

        @NotBlank
        @Pattern(regexp = "[A-Z]{2}[A-Z0-9]{13,32}")
        String debtorIban,

        @NotBlank
        @Pattern(regexp = "[A-Z]{2}[A-Z0-9]{13,32}")
        String creditorIban,

        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 17, fraction = 2)
        BigDecimal amount,

        @NotBlank
        @Pattern(regexp = "[A-Z]{3}")
        String currency,

        @Size(max = 255)
        String description

) {}
