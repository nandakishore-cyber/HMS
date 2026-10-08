package com.example.hms.doctorManagement.controller;

import com.example.hms.doctorManagement.dto.doctor.*;
import com.example.hms.doctorManagement.dto.schedule.*;
import com.example.hms.doctorManagement.entity.DoctorStatus;
import com.example.hms.doctorManagement.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorScheduleService scheduleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorResponse create(@Valid @RequestBody DoctorCreateRequest request) {
        return doctorService.create(request);
    }

    @GetMapping
    public Page<DoctorResponse> list(@RequestParam(required = false) String name,
                                     @RequestParam(required = false) String specialization,
                                     @RequestParam(required = false) UUID departmentId,
                                     @RequestParam(required = false) DoctorStatus status,
                                     Pageable pageable) {
        return doctorService.list(name, specialization, departmentId, status, pageable);
    }

    @GetMapping("/{doctorId}")
    public DoctorResponse get(@PathVariable UUID doctorId) {
        return doctorService.get(doctorId);
    }

    @PutMapping("/{doctorId}")
    public DoctorResponse update(@PathVariable UUID doctorId,
                                 @Valid @RequestBody DoctorUpdateRequest request) {
        return doctorService.update(doctorId, request);
    }

    @PatchMapping("/{doctorId}/status")
    public DoctorResponse status(@PathVariable UUID doctorId,
                                 @Valid @RequestBody DoctorStatusRequest request) {
        return doctorService.setStatus(doctorId, request.status());
    }

    @DeleteMapping("/{doctorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID doctorId) {
        doctorService.setStatus(doctorId, DoctorStatus.INACTIVE);
    }

    @PostMapping("/{doctorId}/schedules")
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleResponse createSchedule(@PathVariable UUID doctorId,
                                            @Valid @RequestBody ScheduleRequest request) {
        return scheduleService.create(doctorId, request);
    }

    @GetMapping("/{doctorId}/schedules")
    public List<ScheduleResponse> schedules(@PathVariable UUID doctorId) {
        return scheduleService.list(doctorId);
    }

    @GetMapping("/{doctorId}/schedules/{scheduleId}")
    public ScheduleResponse schedule(@PathVariable UUID doctorId, @PathVariable UUID scheduleId) {
        return scheduleService.get(doctorId, scheduleId);
    }

    @PutMapping("/{doctorId}/schedules/{scheduleId}")
    public ScheduleResponse updateSchedule(@PathVariable UUID doctorId, @PathVariable UUID scheduleId,
                                           @Valid @RequestBody ScheduleRequest request) {
        return scheduleService.update(doctorId, scheduleId, request);
    }

    @PatchMapping("/{doctorId}/schedules/{scheduleId}/availability")
    public ScheduleResponse availability(@PathVariable UUID doctorId, @PathVariable UUID scheduleId,
                                         @Valid @RequestBody ScheduleAvailabilityRequest request) {
        return scheduleService.setAvailability(doctorId, scheduleId, request.available());
    }

    @DeleteMapping("/{doctorId}/schedules/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSchedule(@PathVariable UUID doctorId, @PathVariable UUID scheduleId) {
        scheduleService.delete(doctorId, scheduleId);
    }

    @GetMapping("/{doctorId}/availability")
    public List<ScheduleResponse> availability(@PathVariable UUID doctorId) {
        return scheduleService.list(doctorId).stream()
                .filter(ScheduleResponse::available).toList();
    }
}
