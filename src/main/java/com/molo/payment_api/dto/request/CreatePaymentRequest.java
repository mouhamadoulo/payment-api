package com.molo.payment_api.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

//DTO utilisé lors de la création d'un paiement
//Les annotations de validation permettent de contrôler les données entrantes
public record CreatePaymentRequest(

        @NotBlank
        @Size(max = 50)
        String reference,

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

