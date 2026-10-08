package com.example.hms.billingPaymentManagement.repository;

import com.example.hms.billingPaymentManagement.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BillItemRepository extends JpaRepository<BillItem, UUID> {
    List<BillItem> findByBillIdOrderByCreatedAtAsc(UUID billId);
    java.util.Optional<BillItem> findByIdAndBillId(UUID id, UUID billId);
}
