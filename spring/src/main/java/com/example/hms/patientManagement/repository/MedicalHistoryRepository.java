package com.example.hms.patientManagement.repository;

import com.example.hms.patientManagement.entity.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, UUID> {
    List<MedicalHistory> findByPatientIdOrderByRecordedDateDesc(UUID patientId);
}
