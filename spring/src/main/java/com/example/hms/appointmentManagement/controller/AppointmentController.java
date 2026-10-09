package com.example.hms.appointmentManagement.controller;

import com.example.hms.appointmentManagement.dto.*;
import com.example.hms.appointmentManagement.entity.AppointmentStatus;
import com.example.hms.appointmentManagement.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create(@Valid @RequestBody AppointmentCreateRequest request) {
        return appointmentService.create(request);
    }

    @GetMapping
    public Page<AppointmentResponse> search(@RequestParam(required = false) UUID patientId,
                                            @RequestParam(required = false) UUID doctorId,
                                            @RequestParam(required = false) AppointmentStatus status,
                                            @RequestParam(required = false) LocalDateTime fromTime,
                                            @RequestParam(required = false) LocalDateTime toTime,
                                            Pageable pageable) {
        return appointmentService.search(patientId, doctorId, status, fromTime, toTime, pageable);
    }

    @GetMapping("/{appointmentId}")
    public AppointmentResponse get(@PathVariable UUID appointmentId) {
        return appointmentService.get(appointmentId);
    }

    @PutMapping("/{appointmentId}")
    public AppointmentResponse reschedule(@PathVariable UUID appointmentId,
                                          @Valid @RequestBody AppointmentUpdateRequest request) {
        return appointmentService.reschedule(appointmentId, request);
    }

    @PatchMapping("/{appointmentId}/status")
    public AppointmentResponse status(@PathVariable UUID appointmentId,
                                      @Valid @RequestBody AppointmentStatusRequest request) {
        return appointmentService.setStatus(appointmentId, request.status());
    }

    @DeleteMapping("/{appointmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable UUID appointmentId) {
        appointmentService.setStatus(appointmentId, AppointmentStatus.CANCELLED);
    }

    @GetMapping("/availability")
    public List<AvailabilityResponse> availability(@RequestParam UUID doctorId,
                                                   @RequestParam LocalDate date,
                                                   @RequestParam(required = false) Integer slotMinutes) {
        return appointmentService.availability(doctorId, date, slotMinutes);
    }

    @PostMapping("/schedules")
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorScheduleResponse saveSchedule(@Valid @RequestBody DoctorScheduleRequest request) {
        return appointmentService.saveSchedule(request);
    }

    @GetMapping("/schedules/{doctorId}")
    public List<DoctorScheduleResponse> schedules(@PathVariable UUID doctorId) {
        return appointmentService.schedules(doctorId);
    }
}
