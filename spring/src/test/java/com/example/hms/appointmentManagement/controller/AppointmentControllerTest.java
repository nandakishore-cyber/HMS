package com.example.hms.appointmentManagement.controller;

import com.example.hms.appointmentManagement.dto.*;
import com.example.hms.appointmentManagement.entity.AppointmentStatus;
import com.example.hms.appointmentManagement.service.AppointmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.*;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean AppointmentService appointmentService;

    private static final UUID APPOINTMENT_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID PATIENT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID DEPARTMENT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID DOCTOR_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    @Test
    void createsAppointment() throws Exception {
        AppointmentCreateRequest request = request();
        when(appointmentService.create(request)).thenReturn(response());

        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(APPOINTMENT_ID.toString()))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void validatesAppointmentWindowFields() throws Exception {
        mockMvc.perform(post("/api/v1/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"patientId":"11111111-1111-1111-1111-111111111111",
                                 "doctorId":"44444444-4444-4444-4444-444444444444",
                                 "startTime":"2030-01-01T10:00:00"}
                                """))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(appointmentService);
    }

    @Test
    void searchesAndChangesStatus() throws Exception {
        when(appointmentService.search(eq(PATIENT_ID), eq(DOCTOR_ID), eq(AppointmentStatus.CONFIRMED),
                isNull(), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response())));
        when(appointmentService.setStatus(APPOINTMENT_ID, AppointmentStatus.CONFIRMED))
                .thenReturn(response());

        mockMvc.perform(get("/api/v1/appointments")
                        .param("patientId", PATIENT_ID.toString())
                        .param("doctorId", DOCTOR_ID.toString())
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(APPOINTMENT_ID.toString()));

        mockMvc.perform(patch("/api/v1/appointments/{id}/status", APPOINTMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    void exposesAvailabilityAndCancel() throws Exception {
        when(appointmentService.availability(DOCTOR_ID, LocalDate.of(2030, 1, 2), 30))
                .thenReturn(List.of(new AvailabilityResponse(DOCTOR_ID,
                        LocalDateTime.of(2030, 1, 2, 9, 0),
                        LocalDateTime.of(2030, 1, 2, 9, 30), true)));
        when(appointmentService.setStatus(APPOINTMENT_ID, AppointmentStatus.CANCELLED))
                .thenReturn(response());

        mockMvc.perform(get("/api/v1/appointments/availability")
                        .param("doctorId", DOCTOR_ID.toString())
                        .param("date", "2030-01-02")
                        .param("slotMinutes", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].available").value(true));

        mockMvc.perform(delete("/api/v1/appointments/{id}", APPOINTMENT_ID))
                .andExpect(status().isNoContent());
    }

    private AppointmentCreateRequest request() {
        return new AppointmentCreateRequest(PATIENT_ID, DEPARTMENT_ID, DOCTOR_ID,
                LocalDateTime.of(2030, 1, 2, 9, 0),
                LocalDateTime.of(2030, 1, 2, 9, 30), "Routine checkup", null);
    }

    private AppointmentResponse response() {
        return new AppointmentResponse(APPOINTMENT_ID, PATIENT_ID, DEPARTMENT_ID, DOCTOR_ID,
                LocalDateTime.of(2030, 1, 2, 9, 0), LocalDateTime.of(2030, 1, 2, 9, 30),
                "Routine checkup", null, AppointmentStatus.SCHEDULED);
    }
}
