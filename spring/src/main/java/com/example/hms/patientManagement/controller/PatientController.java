package com.example.hms.patientManagement.controller;

import com.example.hms.patientManagement.dto.*;
import com.example.hms.patientManagement.entity.PatientStatus;
import com.example.hms.patientManagement.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientResponse create(@Valid @RequestBody PatientCreateRequest request) {
        return patientService.create(request);
    }

    @GetMapping
    public Page<PatientResponse> list(@RequestParam(required = false) String query,
                                      @RequestParam(required = false) PatientStatus status,
                                      Pageable pageable) {
        return patientService.list(query, status, pageable);
    }

    @GetMapping("/{patientId}")
    public PatientResponse get(@PathVariable UUID patientId) {
        return patientService.get(patientId);
    }

    @PutMapping("/{patientId}")
    public PatientResponse update(@PathVariable UUID patientId,
                                  @Valid @RequestBody PatientUpdateRequest request) {
        return patientService.update(patientId, request);
    }

    @PatchMapping("/{patientId}/status")
    public PatientResponse status(@PathVariable UUID patientId,
                                  @Valid @RequestBody PatientStatusRequest request) {
        return patientService.setStatus(patientId, request.status());
    }

    @DeleteMapping("/{patientId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void archive(@PathVariable UUID patientId) {
        patientService.setStatus(patientId, PatientStatus.ARCHIVED);
    }

    @GetMapping("/{patientId}/appointment-history")
    public List<AppointmentHistoryResponse> appointmentHistory(@PathVariable UUID patientId) {
        return patientService.appointmentHistory(patientId);
    }

    @GetMapping("/{patientId}/medical-history")
    public List<MedicalHistoryResponse> medicalHistory(@PathVariable UUID patientId) {
        return patientService.medicalHistory(patientId);
    }

    @GetMapping("/{patientId}/billing-history")
    public Page<BillingHistoryResponse> billingHistory(@PathVariable UUID patientId, Pageable pageable) {
        return patientService.billingHistory(patientId, pageable);
    }
}
