package com.example.hms.billingPaymentManagement.repository;

import com.example.hms.billingPaymentManagement.entity.Payment;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    boolean existsByPaymentReference(String reference);
    java.util.List<Payment> findByBillIdOrderByPaymentDateDesc(UUID billId);
    Page<Payment> findAllByOrderByPaymentDateDesc(Pageable pageable);
}
