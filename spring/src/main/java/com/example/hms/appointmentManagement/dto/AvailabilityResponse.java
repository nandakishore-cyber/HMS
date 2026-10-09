package com.example.hms.appointmentManagement.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AvailabilityResponse(UUID doctorId, LocalDateTime startTime,
                                   LocalDateTime endTime, boolean available) {}
