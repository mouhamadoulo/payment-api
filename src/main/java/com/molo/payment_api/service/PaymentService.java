package com.molo.payment_api.service;

import com.molo.payment_api.dto.request.CreatePaymentRequest;
import com.molo.payment_api.dto.request.UpdatePaymentRequest;
import com.molo.payment_api.dto.request.UpdatePaymentStatusRequest;
import com.molo.payment_api.dto.response.PaymentResponse;
import com.molo.payment_api.entity.Payment;
import com.molo.payment_api.exception.DuplicatePaymentReferenceException;
import com.molo.payment_api.exception.PaymentNotFoundException;
import com.molo.payment_api.mapper.PaymentMapper;
import com.molo.payment_api.repository.PaymentRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Le coeur de notre application - La logique métier
 */
@Service
@Transactional
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;

    public PaymentService(
            PaymentRepository repository,
            PaymentMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    // CREATE
    public PaymentResponse create(CreatePaymentRequest request) {

        if (repository.existsByReference(request.reference())) {
            throw new DuplicatePaymentReferenceException(
                    request.reference()
            );
        }

        Payment payment = mapper.toEntity(request);

        return mapper.toResponse(repository.save(payment));
    }

    // READ BY ID
    @Transactional(readOnly = true)
    public PaymentResponse getById(Long id) {

        return mapper.toResponse(findPayment(id));
    }

    // READ ALL WITH PAGINATION
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getAll(Pageable pageable) {

        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    // READ BY REFERENCE
    @Transactional(readOnly = true)
    public PaymentResponse getByReference(String reference) {

        Payment payment = repository.findByReference(reference)
                .orElseThrow(() ->
                        new PaymentNotFoundException(-1L)
                );

        return mapper.toResponse(payment);
    }

    // UPDATE
    public PaymentResponse update(
            Long id,
            UpdatePaymentRequest request) {

        Payment payment = findPayment(id);

        payment.setDebtorIban(request.debtorIban());
        payment.setCreditorIban(request.creditorIban());
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency());
        payment.setDescription(request.description());

        return mapper.toResponse(repository.save(payment));
    }

    // PATCH STATUS
    public PaymentResponse updateStatus(
            Long id,
            UpdatePaymentStatusRequest request) {

        Payment payment = findPayment(id);

        payment.setStatus(request.status());

        return mapper.toResponse(repository.save(payment));
    }

    // DELETE
    public void delete(Long id) {

        Payment payment = findPayment(id);

        repository.delete(payment);
    }

    private Payment findPayment(Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }
}
