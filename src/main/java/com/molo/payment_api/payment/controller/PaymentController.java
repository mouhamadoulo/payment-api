package com.molo.payment_api.payment.controller;

import com.molo.payment_api.payment.dto.request.CreatePaymentRequest;
import com.molo.payment_api.payment.dto.request.PaymentSearchCriteria;
import com.molo.payment_api.payment.dto.request.UpdatePaymentRequest;
import com.molo.payment_api.payment.dto.request.UpdatePaymentStatusRequest;
import com.molo.payment_api.payment.dto.response.PaymentResponse;
import com.molo.payment_api.payment.enums.PaymentStatus;
import com.molo.payment_api.payment.service.PaymentService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Le Controller constitue la porte d'entrée HTTP de notre application.
 */
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {

        PaymentResponse response = service.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<Page<PaymentResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters"
            );
        }

        Sort.Direction sortDirection =
                Sort.Direction.fromString(direction);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        return ResponseEntity.ok(service.getAll(pageable));
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.getById(id));
    }

    // READ BY REFERENCE
    @GetMapping("/reference/{reference}")
    public ResponseEntity<PaymentResponse> getByReference(
            @PathVariable String reference) {

        return ResponseEntity.ok(
                service.getByReference(reference)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePaymentRequest request) {

        return ResponseEntity.ok(
                service.update(id, request)
        );
    }

    // PARTIAL UPDATE
    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {

        return ResponseEntity.ok(
                service.updateStatus(id, request)
        );
    }

    // DELETE , Seuls les administrateurs peuvent supprimer
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PaymentResponse>> search(

            @RequestParam(required = false)
            String reference,

            @RequestParam(required = false)
            PaymentStatus status,

            @RequestParam(required = false)
            String currency,

            @RequestParam(required = false)
            String debtorIban,

            @RequestParam(required = false)
            String creditorIban,

            @RequestParam(required = false)
            BigDecimal minAmount,

            @RequestParam(required = false)
            BigDecimal maxAmount,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "createdAt")
            String sortBy,

            @RequestParam(defaultValue = "DESC")
            String direction
    ) {

        // Validation pagination
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters"
            );
        }

        // Liste blanche des champs autorisés pour le tri
        Set<String> allowedSortFields = Set.of(
                "id",
                "reference",
                "amount",
                "currency",
                "status",
                "createdAt",
                "updatedAt"
        );

        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sort field: " + sortBy
            );
        }

        // Validation direction
        Sort.Direction sortDirection;

        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Direction must be ASC or DESC"
            );
        }

        // Construction du Criteria
        PaymentSearchCriteria criteria =
                new PaymentSearchCriteria(
                        reference,
                        status,
                        currency,
                        debtorIban,
                        creditorIban,
                        minAmount,
                        maxAmount,
                        startDate,
                        endDate
                );

        // Construction de la pagination
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        return ResponseEntity.ok(
                service.search(criteria, pageable)
        );
    }

}

