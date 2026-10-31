package com.molo.payment_api.repository;

import com.molo.payment_api.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

/**
 * Classe d'accès aux données de ma base de données
 * Spring génère automatiquement les opérations : save(), findById(), findAll(), ...
 * JpaSpecificationExecutor nous apporte : findAll(spec) recherche avec critères dynamiques, findAll(spec, pageable), ...
 * Une Specification permet de construire dynamiquement une requête SQL en fonction des critères fournis.
 * C'est cette interface qui nous permet d'exécuter nos Specifications sans écrire de SQL manuellement.
 */
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    Optional<Payment> findByReference(String reference);

    boolean existsByReference(String reference);
}

