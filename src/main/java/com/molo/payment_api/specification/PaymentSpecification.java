package com.molo.payment_api.specification;

import com.molo.payment_api.dto.request.PaymentSearchCriteria;
import com.molo.payment_api.entity.Payment;
import com.molo.payment_api.enums.PaymentStatus;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * L'idée est de construire une liste de prédicats, puis de les combiner avec AND.
 */
public final class PaymentSpecification {

    private PaymentSpecification() {
    }

    public static Specification<Payment> withFilters(
            PaymentSearchCriteria criteria) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 1. Recherche par référence
            if (criteria.reference() != null
                    && !criteria.reference().isBlank()) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("reference")),
                                "%" + criteria.reference()
                                        .trim().toLowerCase() + "%"
                        )
                );
            }

            // 2. Recherche par statut
            if (criteria.status() != null) {

                predicates.add(
                        cb.equal(
                                root.get("status"),
                                criteria.status()
                        )
                );
            }

            // 3. Recherche par devise
            if (criteria.currency() != null
                    && !criteria.currency().isBlank()) {

                predicates.add(
                        cb.equal(
                                root.get("currency"),
                                criteria.currency().trim().toUpperCase()
                        )
                );
            }

            // 4. Recherche par IBAN débiteur
            if (criteria.debtorIban() != null
                    && !criteria.debtorIban().isBlank()) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("debtorIban")),
                                "%" + criteria.debtorIban()
                                        .trim().toLowerCase() + "%"
                        )
                );
            }

            // 5. Recherche par IBAN créancier
            if (criteria.creditorIban() != null
                    && !criteria.creditorIban().isBlank()) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("creditorIban")),
                                "%" + criteria.creditorIban()
                                        .trim().toLowerCase() + "%"
                        )
                );
            }

            // 6. Montant minimum
            if (criteria.minAmount() != null) {

                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("amount"),
                                criteria.minAmount()
                        )
                );
            }

            // 7. Montant maximum
            if (criteria.maxAmount() != null) {

                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("amount"),
                                criteria.maxAmount()
                        )
                );
            }

            // 8. Date de début
            if (criteria.startDate() != null) {

                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                criteria.startDate()
                        )
                );
            }

            // 9. Date de fin
            if (criteria.endDate() != null) {

                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("createdAt"),
                                criteria.endDate()
                        )
                );
            }

            // Combinaison de toutes les conditions avec AND
            return cb.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }


    public static Specification<Payment> withStatuses(
            PaymentStatus... statuses) {

        return (root, query, cb) ->
                root.get("status").in((Object[]) statuses);
    }

}

