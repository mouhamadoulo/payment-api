package com.molo.payment_api.payment.mapper;

import com.molo.payment_api.payment.dto.request.CreatePaymentRequest;
import com.molo.payment_api.payment.dto.response.PaymentResponse;
import com.molo.payment_api.payment.entity.Payment;
import com.molo.payment_api.payment.enums.PaymentStatus;
import org.springframework.stereotype.Component;

//Classe de transformation d'un DTO request en Entité Payment
//Mais aussi de transformation d'une entité Payment en DTO Response
//À retenir : le Mapper évite de mélanger les responsabilités de transformation avec les règles métier du Service.
@Component
public class PaymentMapper {

    public Payment toEntity(CreatePaymentRequest request) {

        Payment payment = new Payment();

        payment.setReference(request.reference());
        payment.setDebtorIban(request.debtorIban());
        payment.setCreditorIban(request.creditorIban());
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency());
        payment.setDescription(request.description());

        payment.setStatus(PaymentStatus.PENDING);

        return payment;
    }

    public PaymentResponse toResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getVersion(),
                payment.getReference(),
                payment.getDebtorIban(),
                payment.getCreditorIban(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getDescription(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}

