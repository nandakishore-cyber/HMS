package com.example.hms.doctorManagement.repository;

import com.example.hms.doctorManagement.entity.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, UUID> {
    List<DoctorSchedule> findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(UUID doctorId);
    Optional<DoctorSchedule> findByIdAndDoctorId(UUID id, UUID doctorId);
    boolean existsByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek);
    boolean existsByDoctorIdAndDayOfWeekAndStartTimeLessThanAndEndTimeGreaterThan(
            UUID doctorId, DayOfWeek dayOfWeek, LocalTime endTime, LocalTime startTime);
}
