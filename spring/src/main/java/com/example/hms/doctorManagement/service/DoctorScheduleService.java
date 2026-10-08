package com.example.hms.doctorManagement.service;

import com.example.hms.doctorManagement.dto.schedule.*;
import com.example.hms.doctorManagement.entity.*;
import com.example.hms.doctorManagement.exception.*;
import com.example.hms.doctorManagement.repository.DoctorScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoctorScheduleService {
    private final DoctorScheduleRepository scheduleRepository;
    private final DoctorService doctorService;

    @Transactional
    public ScheduleResponse create(UUID doctorId, ScheduleRequest request) {
        Doctor doctor = activeDoctor(doctorId);
        validateTime(request.startTime(), request.endTime());
        if (scheduleRepository.existsByDoctorIdAndDayOfWeek(doctorId, request.dayOfWeek())) {
            throw new ConflictException("A schedule already exists for this doctor and day");
        }
        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setDoctor(doctor);
        apply(schedule, request);
        return ScheduleResponse.from(scheduleRepository.save(schedule));
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponse> list(UUID doctorId) {
        doctorService.getEntity(doctorId);
        return scheduleRepository.findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(doctorId)
                .stream().map(ScheduleResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ScheduleResponse get(UUID doctorId, UUID scheduleId) {
        return ScheduleResponse.from(find(doctorId, scheduleId));
    }

    @Transactional
    public ScheduleResponse update(UUID doctorId, UUID scheduleId, ScheduleRequest request) {
        DoctorSchedule schedule = find(doctorId, scheduleId);
        validateTime(request.startTime(), request.endTime());
        boolean duplicate = scheduleRepository.findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(doctorId).stream()
                .anyMatch(other -> !other.getId().equals(scheduleId)
                        && other.getDayOfWeek() == request.dayOfWeek());
        if (duplicate) {
            throw new ConflictException("A schedule already exists for this doctor and day");
        }
        apply(schedule, request);
        return ScheduleResponse.from(scheduleRepository.save(schedule));
    }

    @Transactional
    public ScheduleResponse setAvailability(UUID doctorId, UUID scheduleId, boolean available) {
        DoctorSchedule schedule = find(doctorId, scheduleId);
        if (available && schedule.getDoctor().getStatus() != DoctorStatus.ACTIVE) {
            throw new ConflictException("Inactive doctors cannot have available schedules");
        }
        schedule.setAvailable(available);
        return ScheduleResponse.from(scheduleRepository.save(schedule));
    }

    @Transactional
    public void delete(UUID doctorId, UUID scheduleId) {
        scheduleRepository.delete(find(doctorId, scheduleId));
    }

    private Doctor activeDoctor(UUID id) {
        Doctor doctor = doctorService.getEntity(id);
        if (doctor.getStatus() != DoctorStatus.ACTIVE) {
            throw new ConflictException("Inactive doctors cannot receive schedules");
        }
        return doctor;
    }

    private DoctorSchedule find(UUID doctorId, UUID scheduleId) {
        return scheduleRepository.findByIdAndDoctorId(scheduleId, doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found: " + scheduleId));
    }

    private void validateTime(LocalTime start, LocalTime end) {
        if (!start.isBefore(end)) {
            throw new ConflictException("Schedule start time must be before end time");
        }
    }

    private void apply(DoctorSchedule schedule, ScheduleRequest request) {
        schedule.setDayOfWeek(request.dayOfWeek());
        schedule.setStartTime(request.startTime());
        schedule.setEndTime(request.endTime());
    }
}
