package com.example.hms.doctorManagement.repository;

import com.example.hms.doctorManagement.entity.Doctor;
import com.example.hms.doctorManagement.entity.DoctorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByDepartmentId(UUID departmentId);
    List<Doctor> findAllByDepartmentIdAndStatus(UUID departmentId, DoctorStatus status);
    Page<Doctor> findByFullNameContainingIgnoreCaseAndStatus(String name, DoctorStatus status, Pageable pageable);
    Page<Doctor> findBySpecializationContainingIgnoreCaseAndStatus(String specialization, DoctorStatus status, Pageable pageable);
    Page<Doctor> findByDepartmentIdAndStatus(UUID departmentId, DoctorStatus status, Pageable pageable);
    Page<Doctor> findByStatus(DoctorStatus status, Pageable pageable);
}
