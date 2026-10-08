package com.example.hms.patientManagement.repository;

import com.example.hms.patientManagement.entity.AppointmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AppointmentHistoryRepository extends JpaRepository<AppointmentHistory, UUID> {
    List<AppointmentHistory> findByPatientIdOrderByAppointmentDateDesc(UUID patientId);
}
