package com.example.hms.patientManagement.service;

import com.example.hms.billingPaymentManagement.repository.BillRepository;
import com.example.hms.patientManagement.dto.*;
import com.example.hms.patientManagement.entity.*;
import com.example.hms.patientManagement.exception.*;
import com.example.hms.patientManagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {
    private final PatientRepository patientRepository;
    private final AppointmentHistoryRepository appointmentHistoryRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final BillRepository billRepository;

    public PatientResponse create(PatientCreateRequest request) {
        if (patientRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("A patient with this email already exists");
        }
        Patient patient = new Patient();
        copy(request, patient);
        return PatientResponse.from(patientRepository.save(patient));
    }

    @Transactional(readOnly = true)
    public Page<PatientResponse> list(String query, PatientStatus status, Pageable pageable) {
        Page<Patient> patients;
        if (query != null && !query.isBlank() && status != null) {
            patients = patientRepository.findByFullNameContainingIgnoreCaseAndStatus(query.trim(), status, pageable);
        } else if (query != null && !query.isBlank()) {
            patients = patientRepository.findByFullNameContainingIgnoreCase(query.trim(), pageable);
        } else if (status != null) {
            patients = patientRepository.findByStatus(status, pageable);
        } else {
            patients = patientRepository.findAll(pageable);
        }
        return patients.map(PatientResponse::from);
    }

    @Transactional(readOnly = true)
    public PatientResponse get(UUID patientId) {
        return PatientResponse.from(findPatient(patientId));
    }

    public PatientResponse update(UUID patientId, PatientUpdateRequest request) {
        Patient patient = findPatient(patientId);
        if (patientRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), patientId)) {
            throw new ConflictException("A patient with this email already exists");
        }
        copy(request, patient);
        return PatientResponse.from(patient);
    }

    public PatientResponse setStatus(UUID patientId, PatientStatus status) {
        Patient patient = findPatient(patientId);
        patient.setStatus(status);
        return PatientResponse.from(patient);
    }

    @Transactional(readOnly = true)
    public List<AppointmentHistoryResponse> appointmentHistory(UUID patientId) {
        ensurePatientExists(patientId);
        return appointmentHistoryRepository.findByPatientIdOrderByAppointmentDateDesc(patientId)
                .stream().map(AppointmentHistoryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MedicalHistoryResponse> medicalHistory(UUID patientId) {
        ensurePatientExists(patientId);
        return medicalHistoryRepository.findByPatientIdOrderByRecordedDateDesc(patientId)
                .stream().map(MedicalHistoryResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Page<BillingHistoryResponse> billingHistory(UUID patientId, Pageable pageable) {
        ensurePatientExists(patientId);
        return billRepository.findByPatientId(patientId, pageable).map(BillingHistoryResponse::from);
    }

    private Patient findPatient(UUID patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + patientId));
    }

    private void ensurePatientExists(UUID patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found: " + patientId);
        }
    }

    private void copy(PatientCreateRequest request, Patient patient) {
        patient.setFullName(request.fullName());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setBloodGroup(request.bloodGroup());
        patient.setPhone(request.phone());
        patient.setEmail(request.email());
        patient.setAddress(request.address());
        patient.setEmergencyContact(request.emergencyContact());
    }

    private void copy(PatientUpdateRequest request, Patient patient) {
        patient.setFullName(request.fullName());
        patient.setDateOfBirth(request.dateOfBirth());
        patient.setGender(request.gender());
        patient.setBloodGroup(request.bloodGroup());
        patient.setPhone(request.phone());
        patient.setEmail(request.email());
        patient.setAddress(request.address());
        patient.setEmergencyContact(request.emergencyContact());
    }
}
