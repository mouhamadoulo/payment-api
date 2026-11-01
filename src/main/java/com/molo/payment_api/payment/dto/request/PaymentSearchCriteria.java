package com.molo.payment_api.payment.dto.request;

import com.molo.payment_api.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Nous allons commencer par définir tous les critères de recherche possibles.
 */
public record PaymentSearchCriteria(

        String reference,

        PaymentStatus status,

        String currency,

        String debtorIban,

        String creditorIban,

        BigDecimal minAmount,

        BigDecimal maxAmount,

        LocalDateTime startDate,

        LocalDateTime endDate

) {}

