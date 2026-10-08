package com.example.hms.doctorManagement.service;

import com.example.hms.doctorManagement.dto.doctor.*;
import com.example.hms.doctorManagement.entity.*;
import com.example.hms.doctorManagement.exception.*;
import com.example.hms.doctorManagement.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DepartmentService departmentService;

    @Transactional
    public DoctorResponse create(DoctorCreateRequest request) {
        if (doctorRepository.existsByEmailIgnoreCase(request.email().trim())) {
            throw new ConflictException("A doctor with this email already exists");
        }
        Department department = activeDepartment(request.departmentId());
        Doctor doctor = new Doctor();
        apply(doctor, request.fullName(), request.specialization(), request.qualification(),
                department, request.phone(), request.email(), request.experienceYears());
        return DoctorResponse.from(doctorRepository.save(doctor));
    }

    @Transactional(readOnly = true)
    public Page<DoctorResponse> list(String name, String specialization, UUID departmentId,
                                     DoctorStatus status, Pageable pageable) {
        Page<Doctor> doctors;
        if (departmentId != null) {
            doctors = doctorRepository.findByDepartmentIdAndStatus(departmentId,
                    status == null ? DoctorStatus.ACTIVE : status, pageable);
        } else if (specialization != null && !specialization.isBlank()) {
            doctors = doctorRepository.findBySpecializationContainingIgnoreCaseAndStatus(
                    specialization, status == null ? DoctorStatus.ACTIVE : status, pageable);
        } else if (name != null && !name.isBlank()) {
            doctors = doctorRepository.findByFullNameContainingIgnoreCaseAndStatus(
                    name, status == null ? DoctorStatus.ACTIVE : status, pageable);
        } else {
            doctors = doctorRepository.findByStatus(status == null ? DoctorStatus.ACTIVE : status, pageable);
        }
        return doctors.map(DoctorResponse::from);
    }

    @Transactional(readOnly = true)
    public Doctor getEntity(UUID id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id));
    }

    @Transactional(readOnly = true)
    public DoctorResponse get(UUID id) {
        return DoctorResponse.from(getEntity(id));
    }

    @Transactional
    public DoctorResponse update(UUID id, DoctorUpdateRequest request) {
        Doctor doctor = getEntity(id);
        if (!doctor.getEmail().equalsIgnoreCase(request.email())
                && doctorRepository.existsByEmailIgnoreCase(request.email().trim())) {
            throw new ConflictException("A doctor with this email already exists");
        }
        apply(doctor, request.fullName(), request.specialization(), request.qualification(),
                activeDepartment(request.departmentId()), request.phone(), request.email(), request.experienceYears());
        return DoctorResponse.from(doctorRepository.save(doctor));
    }

    @Transactional
    public DoctorResponse setStatus(UUID id, DoctorStatus status) {
        Doctor doctor = getEntity(id);
        doctor.setStatus(status);
        return DoctorResponse.from(doctorRepository.save(doctor));
    }

    private Department activeDepartment(UUID id) {
        Department department = departmentService.getEntity(id);
        if (!department.isActive()) {
            throw new ConflictException("Department is inactive");
        }
        return department;
    }

    private void apply(Doctor doctor, String name, String specialization, String qualification,
                       Department department, String phone, String email, Integer experienceYears) {
        doctor.setFullName(name.trim());
        doctor.setSpecialization(specialization.trim());
        doctor.setQualification(qualification.trim());
        doctor.setDepartment(department);
        doctor.setPhone(phone.trim());
        doctor.setEmail(email.trim().toLowerCase());
        doctor.setExperienceYears(experienceYears);
    }
}
