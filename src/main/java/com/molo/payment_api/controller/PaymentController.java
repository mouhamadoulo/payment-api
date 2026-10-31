package com.molo.payment_api.controller;

import com.molo.payment_api.dto.request.CreatePaymentRequest;
import com.molo.payment_api.dto.request.UpdatePaymentRequest;
import com.molo.payment_api.dto.request.UpdatePaymentStatusRequest;
import com.molo.payment_api.dto.response.PaymentResponse;
import com.molo.payment_api.service.PaymentService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

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

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}

