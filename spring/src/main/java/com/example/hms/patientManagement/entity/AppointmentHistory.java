package com.example.hms.patientManagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "appointment_history", indexes = {
        @Index(name = "idx_appointment_history_patient", columnList = "patient_id")
})
@Getter
@Setter
@NoArgsConstructor
public class AppointmentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(nullable = false)
    private LocalDateTime appointmentDate;

    @Column(nullable = false, length = 160)
    private String doctorName;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
