package com.example.hms.doctorManagement.service;

import com.example.hms.doctorManagement.dto.department.*;
import com.example.hms.doctorManagement.entity.Department;
import com.example.hms.doctorManagement.exception.*;
import com.example.hms.doctorManagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.existsByNameIgnoreCase(request.name().trim())) {
            throw new ConflictException("Department already exists");
        }
        Department department = new Department();
        department.setName(request.name().trim());
        department.setDescription(request.description().trim());
        return DepartmentResponse.from(departmentRepository.save(department));
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> list() {
        return departmentRepository.findAll().stream().map(DepartmentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Department getEntity(UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }

    @Transactional(readOnly = true)
    public DepartmentResponse get(UUID id) {
        return DepartmentResponse.from(getEntity(id));
    }

    @Transactional
    public DepartmentResponse update(UUID id, DepartmentRequest request) {
        Department department = getEntity(id);
        if (!department.getName().equalsIgnoreCase(request.name())
                && departmentRepository.existsByNameIgnoreCase(request.name().trim())) {
            throw new ConflictException("Department already exists");
        }
        department.setName(request.name().trim());
        department.setDescription(request.description().trim());
        return DepartmentResponse.from(departmentRepository.save(department));
    }

    @Transactional
    public DepartmentResponse setActive(UUID id, boolean active) {
        Department department = getEntity(id);
        department.setActive(active);
        return DepartmentResponse.from(departmentRepository.save(department));
    }

    @Transactional
    public void delete(UUID id) {
        Department department = getEntity(id);
        if (doctorRepository.existsByDepartmentId(id)) {
            throw new ConflictException("Cannot delete a department assigned to doctors");
        }
        departmentRepository.delete(department);
    }
}
