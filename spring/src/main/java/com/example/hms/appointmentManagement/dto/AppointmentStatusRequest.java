package com.example.hms.appointmentManagement.dto;

import com.example.hms.appointmentManagement.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record AppointmentStatusRequest(@NotNull AppointmentStatus status) {}
