package com.example.hms.billingPaymentManagement.repository;

import com.example.hms.billingPaymentManagement.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;
import java.util.Collection;

public interface BillRepository extends JpaRepository<Bill, UUID> {
    boolean existsByBillNumber(String billNumber);
    Page<Bill> findByPatientId(UUID patientId, Pageable pageable);
    Page<Bill> findByStatus(PaymentStatus status, Pageable pageable);
    Page<Bill> findByStatusIn(Collection<PaymentStatus> statuses, Pageable pageable);
    Page<Bill> findByPatientIdAndStatus(UUID patientId, PaymentStatus status, Pageable pageable);
    Page<Bill> findByBillDateBetween(LocalDate from, LocalDate to, Pageable pageable);
}
