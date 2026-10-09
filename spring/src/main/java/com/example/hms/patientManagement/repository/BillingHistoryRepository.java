package com.example.hms.patientManagement.repository;

import com.example.hms.patientManagement.entity.BillingHistory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BillingHistoryRepository extends JpaRepository<BillingHistory, UUID> {
    Page<BillingHistory> findByPatientId(UUID patientId, Pageable pageable);
}
