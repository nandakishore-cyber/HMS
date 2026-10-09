package com.example.hms.appointmentManagement.repository;

import com.example.hms.appointmentManagement.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.*;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    Page<Appointment> findByPatientId(UUID patientId, Pageable pageable);
    Page<Appointment> findByDoctorId(UUID doctorId, Pageable pageable);
    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);
    Page<Appointment> findByPatientIdAndStatus(UUID patientId, AppointmentStatus status, Pageable pageable);

    @Query("""
            select a from Appointment a
            where (:patientId is null or a.patientId = :patientId)
              and (:doctorId is null or a.doctorId = :doctorId)
              and (:status is null or a.status = :status)
              and (:fromTime is null or a.startTime >= :fromTime)
              and (:toTime is null or a.startTime < :toTime)
            """)
    Page<Appointment> search(@Param("patientId") UUID patientId, @Param("doctorId") UUID doctorId,
                             @Param("status") AppointmentStatus status,
                             @Param("fromTime") LocalDateTime fromTime,
                             @Param("toTime") LocalDateTime toTime, Pageable pageable);

    @Query("""
            select count(a) > 0 from Appointment a
            where a.doctorId = :doctorId
              and a.status <> com.example.hms.appointmentManagement.entity.AppointmentStatus.CANCELLED
              and a.startTime < :endTime and a.endTime > :startTime
              and (:appointmentId is null or a.id <> :appointmentId)
            """)
    boolean hasDoctorOverlap(@Param("doctorId") UUID doctorId,
                             @Param("startTime") LocalDateTime startTime,
                             @Param("endTime") LocalDateTime endTime,
                             @Param("appointmentId") UUID appointmentId);

    List<Appointment> findByPatientIdOrderByStartTimeDesc(UUID patientId);
}
