package com.molo.payment_api.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleNotFound(PaymentNotFoundException ex) {

        return problem(
                HttpStatus.NOT_FOUND,
                "Payment not found",
                ex.getMessage()
        );
    }

    @ExceptionHandler(DuplicatePaymentReferenceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleDuplicate(DuplicatePaymentReferenceException ex) {

        return problem(
                HttpStatus.CONFLICT,
                "Duplicate payment",
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {

        var errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage(),
                        (first, second) -> first
                ));

        ProblemDetail detail = problem(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "One or more fields are invalid"
        );

        detail.setProperty("errors", errors);

        return detail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleIntegrity(DataIntegrityViolationException ex) {

        return problem(
                HttpStatus.CONFLICT,
                "Data integrity violation",
                "The requested operation violates a database constraint"
        );
    }

    private ProblemDetail problem(
            HttpStatus status,
            String title,
            String message) {

        ProblemDetail detail = ProblemDetail.forStatus(status);

        detail.setTitle(title);
        detail.setDetail(message);
        detail.setType(URI.create(
                "https://api.payment.local/errors/" + status.value()
        ));

        return detail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleBadRequest(
            IllegalArgumentException ex) {

        return problem(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                ex.getMessage()
        );
    }

}
