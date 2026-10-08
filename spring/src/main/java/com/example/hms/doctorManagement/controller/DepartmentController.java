package com.example.hms.doctorManagement.controller;

import com.example.hms.doctorManagement.dto.department.*;
import com.example.hms.doctorManagement.dto.doctor.DoctorResponse;
import com.example.hms.doctorManagement.entity.DoctorStatus;
import com.example.hms.doctorManagement.repository.DoctorRepository;
import com.example.hms.doctorManagement.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;
    private final DoctorRepository doctorRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse create(@Valid @RequestBody DepartmentRequest request) {
        return departmentService.create(request);
    }

    @GetMapping
    public List<DepartmentResponse> list() {
        return departmentService.list();
    }

    @GetMapping("/{departmentId}")
    public DepartmentResponse get(@PathVariable UUID departmentId) {
        return departmentService.get(departmentId);
    }

    @PutMapping("/{departmentId}")
    public DepartmentResponse update(@PathVariable UUID departmentId,
                                     @Valid @RequestBody DepartmentRequest request) {
        return departmentService.update(departmentId, request);
    }

    @PatchMapping("/{departmentId}/status")
    public DepartmentResponse status(@PathVariable UUID departmentId,
                                     @Valid @RequestBody DepartmentStatusRequest request) {
        return departmentService.setActive(departmentId, request.active());
    }

    @DeleteMapping("/{departmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID departmentId) {
        departmentService.delete(departmentId);
    }

    @GetMapping("/{departmentId}/doctors")
    public List<DoctorResponse> doctors(@PathVariable UUID departmentId) {
        departmentService.getEntity(departmentId);
        return doctorRepository.findAllByDepartmentIdAndStatus(departmentId, DoctorStatus.ACTIVE)
                .stream().map(DoctorResponse::from).toList();
    }
}
