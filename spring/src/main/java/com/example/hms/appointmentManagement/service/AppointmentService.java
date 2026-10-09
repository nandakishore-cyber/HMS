package com.example.hms.appointmentManagement.service;

import com.example.hms.appointmentManagement.dto.*;
import com.example.hms.appointmentManagement.entity.*;
import com.example.hms.appointmentManagement.exception.*;
import com.example.hms.appointmentManagement.repository.*;
import com.example.hms.patientManagement.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorScheduleRepository scheduleRepository;
    private final PatientRepository patientRepository;

    public AppointmentResponse create(AppointmentCreateRequest request) {
        ensureValidWindow(request.startTime(), request.endTime());
        ensurePatient(request.patientId());
        ensureAvailable(request.doctorId(), request.startTime(), request.endTime(), null);
        Appointment appointment = new Appointment();
        appointment.setPatientId(request.patientId());
        appointment.setDepartmentId(request.departmentId());
        appointment.setDoctorId(request.doctorId());
        apply(appointment, request.startTime(), request.endTime(), request.reason(), request.notes());
        return AppointmentResponse.from(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> search(UUID patientId, UUID doctorId, AppointmentStatus status,
                                             LocalDateTime fromTime, LocalDateTime toTime,
                                             Pageable pageable) {
        return appointmentRepository.search(patientId, doctorId, status, fromTime, toTime, pageable)
                .map(AppointmentResponse::from);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse get(UUID id) {
        return AppointmentResponse.from(find(id));
    }

    public AppointmentResponse reschedule(UUID id, AppointmentUpdateRequest request) {
        ensureValidWindow(request.startTime(), request.endTime());
        Appointment appointment = find(id);
        ensureMutable(appointment);
        ensureAvailable(appointment.getDoctorId(), request.startTime(), request.endTime(), id);
        apply(appointment, request.startTime(), request.endTime(), request.reason(), request.notes());
        return AppointmentResponse.from(appointment);
    }

    public AppointmentResponse setStatus(UUID id, AppointmentStatus status) {
        Appointment appointment = find(id);
        if (!isAllowedTransition(appointment.getStatus(), status)) {
            throw new ConflictException("Invalid appointment status transition from "
                    + appointment.getStatus() + " to " + status);
        }
        appointment.setStatus(status);
        return AppointmentResponse.from(appointment);
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> availability(UUID doctorId, LocalDate date,
                                                    Integer slotMinutes) {
        int duration = slotMinutes == null ? 30 : slotMinutes;
        if (duration < 5 || duration > 240) {
            throw new ConflictException("slotMinutes must be between 5 and 240");
        }
        DoctorSchedule schedule = scheduleRepository.findByDoctorIdAndDayOfWeek(
                        doctorId, date.getDayOfWeek())
                .filter(DoctorSchedule::isAvailable)
                .orElseThrow(() -> new ResourceNotFoundException("No available schedule for doctor on " + date));
        List<AvailabilityResponse> slots = new ArrayList<>();
        LocalDateTime cursor = LocalDateTime.of(date, schedule.getStartTime());
        LocalDateTime end = LocalDateTime.of(date, schedule.getEndTime());
        while (!cursor.plusMinutes(duration).isAfter(end)) {
            LocalDateTime slotEnd = cursor.plusMinutes(duration);
            boolean available = !appointmentRepository.hasDoctorOverlap(
                    doctorId, cursor, slotEnd, null);
            slots.add(new AvailabilityResponse(doctorId, cursor, slotEnd, available));
            cursor = slotEnd;
        }
        return slots;
    }

    public DoctorScheduleResponse saveSchedule(DoctorScheduleRequest request) {
        if (!request.startTime().isBefore(request.endTime())) {
            throw new ConflictException("Schedule startTime must be before endTime");
        }
        DoctorSchedule schedule = scheduleRepository
                .findByDoctorIdAndDayOfWeek(request.doctorId(), request.dayOfWeek())
                .orElseGet(DoctorSchedule::new);
        schedule.setDoctorId(request.doctorId());
        schedule.setDepartmentId(request.departmentId());
        schedule.setDayOfWeek(request.dayOfWeek());
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());
        schedule.setAvailable(request.available());
        return DoctorScheduleResponse.from(scheduleRepository.save(schedule));
    }

    @Transactional(readOnly = true)
    public List<DoctorScheduleResponse> schedules(UUID doctorId) {
        return scheduleRepository.findByDoctorIdAndAvailableTrue(doctorId).stream()
                .map(DoctorScheduleResponse::from).toList();
    }

    private Appointment find(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + id));
    }

    private void ensurePatient(UUID patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found: " + patientId);
        }
    }

    private void ensureAvailable(UUID doctorId, LocalDateTime start, LocalDateTime end, UUID appointmentId) {
        DoctorSchedule schedule = scheduleRepository.findByDoctorIdAndDayOfWeek(
                        doctorId, start.getDayOfWeek())
                .filter(DoctorSchedule::isAvailable)
                .orElseThrow(() -> new ConflictException("Doctor is not available at the requested time"));
        if (start.toLocalTime().isBefore(schedule.getStartTime())
                || end.toLocalTime().isAfter(schedule.getEndTime())) {
            throw new ConflictException("Appointment is outside the doctor's schedule");
        }
        if (appointmentRepository.hasDoctorOverlap(doctorId, start, end, appointmentId)) {
            throw new ConflictException("Doctor already has an appointment at the requested time");
        }
    }

    private void ensureValidWindow(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new ConflictException("Appointment startTime must be before endTime");
        }
    }

    private void ensureMutable(Appointment appointment) {
        if (appointment.getStatus() == AppointmentStatus.CANCELLED
                || appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new ConflictException("This appointment cannot be rescheduled");
        }
    }

    private boolean isAllowedTransition(AppointmentStatus current, AppointmentStatus next) {
        if (current == next) return true;
        return switch (current) {
            case SCHEDULED -> next == AppointmentStatus.CONFIRMED || next == AppointmentStatus.CANCELLED;
            case CONFIRMED -> next == AppointmentStatus.IN_PROGRESS || next == AppointmentStatus.CANCELLED;
            case IN_PROGRESS -> next == AppointmentStatus.COMPLETED;
            case COMPLETED, CANCELLED -> false;
        };
    }

    private void apply(Appointment appointment, LocalDateTime start, LocalDateTime end,
                       String reason, String notes) {
        appointment.setStartTime(start);
        appointment.setEndTime(end);
        appointment.setReason(reason.trim());
        appointment.setNotes(notes == null ? null : notes.trim());
    }
}
