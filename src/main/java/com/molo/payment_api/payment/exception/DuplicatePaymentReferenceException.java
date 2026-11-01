package com.molo.payment_api.payment.exception;

public class DuplicatePaymentReferenceException extends RuntimeException {

    public DuplicatePaymentReferenceException(String reference) {
        super("Payment reference already exists: " + reference);
    }
}

