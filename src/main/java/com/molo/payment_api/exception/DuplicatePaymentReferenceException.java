package com.molo.payment_api.exception;

public class DuplicatePaymentReferenceException extends RuntimeException {

    public DuplicatePaymentReferenceException(String reference) {
        super("Payment reference already exists: " + reference);
    }
}

