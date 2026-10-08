package com.example.hms.patientManagement.repository;

import com.example.hms.patientManagement.entity.Patient;
import com.example.hms.patientManagement.entity.PatientStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, UUID id);
    Page<Patient> findByStatus(PatientStatus status, Pageable pageable);
    Page<Patient> findByFullNameContainingIgnoreCaseAndStatus(String name, PatientStatus status, Pageable pageable);
    Page<Patient> findByFullNameContainingIgnoreCase(String name, Pageable pageable);
}
