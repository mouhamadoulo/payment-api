package com.molo.payment_api.exception;

/**
 * Une bonne API REST doit retourner des erreurs cohérentes, compréhensibles et normalisées.
 */
public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(Long id) {
        super("Payment not found with id: " + id);
    }
}
