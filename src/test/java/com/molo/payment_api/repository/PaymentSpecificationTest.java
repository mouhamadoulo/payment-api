package com.molo.payment_api.repository;

import com.molo.payment_api.payment.dto.request.PaymentSearchCriteria;
import com.molo.payment_api.payment.entity.Payment;
import com.molo.payment_api.payment.enums.PaymentStatus;
import com.molo.payment_api.payment.repository.PaymentRepository;
import com.molo.payment_api.payment.specification.PaymentSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @DataJpaTest remplace la DataSource par une base H2 en mémoire (dépendance test).
 * Le dialecte PostgreSQL défini dans application.yaml est surchargé pour H2.
 */
@DataJpaTest(properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
class PaymentSpecificationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        // @DataJpaTest est transactionnel : rollback automatique après chaque test
        Payment payment1 = new Payment();
        payment1.setReference("REF-12345");
        payment1.setStatus(PaymentStatus.PENDING);
        payment1.setCurrency("EUR");
        payment1.setDebtorIban("FR761234567890");
        payment1.setCreditorIban("FR769876543210");
        payment1.setAmount(new BigDecimal("150.00"));
        paymentRepository.save(payment1);

        Payment payment2 = new Payment();
        payment2.setReference("REF-67890");
        payment2.setStatus(PaymentStatus.COMPLETED);
        payment2.setCurrency("USD");
        payment2.setDebtorIban("US1234567890");
        payment2.setCreditorIban("US9876543210");
        payment2.setAmount(new BigDecimal("1500.00"));
        paymentRepository.save(payment2);
    }

    @Test
    @DisplayName("Devrait filtrer par référence (insensible à la casse et recherche partielle)")
    void shouldFilterByReference() {
        PaymentSearchCriteria criteria = new PaymentSearchCriteria(
                "ref-123", null, null, null, null, null, null, null, null
        );

        Specification<Payment> spec = PaymentSpecification.withFilters(criteria);
        List<Payment> results = paymentRepository.findAll(spec);

        assertThat(results)
                .hasSize(1)
                .extracting(Payment::getReference)
                .containsExactly("REF-12345");
    }

    @Test
    @DisplayName("Devrait filtrer par statut et devise")
    void shouldFilterByStatusAndCurrency() {
        PaymentSearchCriteria criteria = new PaymentSearchCriteria(
                null, PaymentStatus.COMPLETED, "usd", null, null, null, null, null, null
        );

        Specification<Payment> spec = PaymentSpecification.withFilters(criteria);
        List<Payment> results = paymentRepository.findAll(spec);

        assertThat(results)
                .hasSize(1)
                .extracting(Payment::getCurrency)
                .containsExactly("USD");
    }

    @Test
    @DisplayName("Devrait filtrer par fourchette de montants (min et max)")
    void shouldFilterByAmountRange() {
        PaymentSearchCriteria criteria = new PaymentSearchCriteria(
                null, null, null, null, null,
                new BigDecimal("100.00"), new BigDecimal("500.00"),
                null, null
        );

        Specification<Payment> spec = PaymentSpecification.withFilters(criteria);
        List<Payment> results = paymentRepository.findAll(spec);

        assertThat(results)
                .hasSize(1)
                .extracting(Payment::getAmount)
                .usingElementComparator(BigDecimal::compareTo)
                .containsExactly(new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Devrait filtrer par liste de statuts avec withStatuses")
    void shouldFilterByMultipleStatuses() {
        Specification<Payment> spec = PaymentSpecification.withStatuses(
                PaymentStatus.PENDING, PaymentStatus.COMPLETED
        );

        List<Payment> results = paymentRepository.findAll(spec);

        assertThat(results)
                .extracting(Payment::getReference)
                .containsExactlyInAnyOrder("REF-12345", "REF-67890");
    }

    @Test
    @DisplayName("Ne devrait retourner aucun résultat si aucun critère ne correspond")
    void shouldReturnEmptyWhenNoMatch() {
        PaymentSearchCriteria criteria = new PaymentSearchCriteria(
                "NON-EXISTENT", null, null, null, null, null, null, null, null
        );

        Specification<Payment> spec = PaymentSpecification.withFilters(criteria);
        List<Payment> results = paymentRepository.findAll(spec);

        assertThat(results).isEmpty();
    }
}
