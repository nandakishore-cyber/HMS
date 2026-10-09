package com.example.hms.patientManagement.controller;

import com.example.hms.patientManagement.dto.*;
import com.example.hms.patientManagement.entity.PatientStatus;
import com.example.hms.patientManagement.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
class PatientControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientService patientService;

    private static final UUID PATIENT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Test
    void registersPatient() throws Exception {
        PatientCreateRequest request = request();
        when(patientService.create(request)).thenReturn(response());

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patientId").value(PATIENT_ID.toString()))
                .andExpect(jsonPath("$.fullName").value("Alex Patient"));
    }

    @Test
    void validatesPatientRegistration() throws Exception {
        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"","dateOfBirth":"2030-01-01","email":"not-an-email"}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(patientService);
    }

    @Test
    void searchesPatients() throws Exception {
        when(patientService.list(eq("Alex"), eq(PatientStatus.ACTIVE), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response())));

        mockMvc.perform(get("/api/v1/patients")
                        .param("query", "Alex")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].patientId").value(PATIENT_ID.toString()));
    }

    @Test
    void updatesAndArchivesPatient() throws Exception {
        PatientUpdateRequest update = new PatientUpdateRequest("Alex Updated",
                LocalDate.of(1990, 1, 1), "OTHER", "O+", "555-0000",
                "alex.updated@example.com", "2 Main Street", "555-9999");
        when(patientService.update(PATIENT_ID, update)).thenReturn(response());
        when(patientService.setStatus(PATIENT_ID, PatientStatus.ARCHIVED)).thenReturn(response());

        mockMvc.perform(put("/api/v1/patients/{patientId}", PATIENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/patients/{patientId}", PATIENT_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void returnsPatientHistories() throws Exception {
        when(patientService.appointmentHistory(PATIENT_ID)).thenReturn(List.of());
        when(patientService.medicalHistory(PATIENT_ID)).thenReturn(List.of());
        when(patientService.billingHistory(eq(PATIENT_ID), any(Pageable.class)))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/patients/{patientId}/appointment-history", PATIENT_ID))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
        mockMvc.perform(get("/api/v1/patients/{patientId}/medical-history", PATIENT_ID))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
        mockMvc.perform(get("/api/v1/patients/{patientId}/billing-history", PATIENT_ID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray());
    }

    private PatientCreateRequest request() {
        return new PatientCreateRequest("Alex Patient", LocalDate.of(1990, 1, 1),
                "MALE", "O+", "555-0100", "alex@example.com",
                "1 Main Street", "555-0199");
    }

    private PatientResponse response() {
        return new PatientResponse(PATIENT_ID, "Alex Patient", LocalDate.of(1990, 1, 1),
                36, "MALE", "O+", "555-0100", "alex@example.com",
                "1 Main Street", "555-0199", LocalDate.of(2026, 1, 1), PatientStatus.ACTIVE);
    }
}
