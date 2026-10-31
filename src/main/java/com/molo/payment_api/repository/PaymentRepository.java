package com.molo.payment_api.repository;

import com.molo.payment_api.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//Classe d'accès aux données de ma base de données
//Spring génère automatiquement les opérations : save(), findById(), findAll(), ...
public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Optional<Payment> findByReference(String reference);

    boolean existsByReference(String reference);
}
