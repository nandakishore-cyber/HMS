package com.example.hms.patientManagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "medical_history", indexes = {
        @Index(name = "idx_medical_history_patient", columnList = "patient_id")
})
@Getter
@Setter
@NoArgsConstructor
public class MedicalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private LocalDate recordedDate;

    @Column(nullable = false, length = 255)
    private String diagnosis;

    @Column(nullable = false, length = 500)
    private String treatment;

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
