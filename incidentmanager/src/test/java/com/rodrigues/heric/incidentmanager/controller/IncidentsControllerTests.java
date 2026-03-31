package com.rodrigues.heric.incidentmanager.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.nullValue;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.rodrigues.heric.incidentmanager.domain.enums.CriticalityEnum;
import com.rodrigues.heric.incidentmanager.domain.enums.IncidentStatusEnum;
import com.rodrigues.heric.incidentmanager.dto.IncidentsDTO;
import com.rodrigues.heric.incidentmanager.service.IncidentsService;

@WebMvcTest(IncidentsController.class)
@ActiveProfiles("test")
public class IncidentsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IncidentsService incidentsService;

    // ========== GET INCIDENT ==========
    @Test
    @DisplayName("Should get by id incident successfully")
    public void shouldGetIncidentSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        String title = "Incident title";
        String description = "Incident description";
        IncidentStatusEnum status = IncidentStatusEnum.OPEN;
        CriticalityEnum criticality = CriticalityEnum.MEDIUM;

        IncidentsDTO result = new IncidentsDTO(
                id, title, description, status, criticality, serviceId, null, null);

        when(this.incidentsService.getIncidentById(id)).thenReturn(result);

        this.mockMvc.perform(get("/incidents/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(result.id().toString()))
                .andExpect(jsonPath("$.title").value(result.title()))
                .andExpect(jsonPath("$.description").value(result.description()))
                .andExpect(jsonPath("$.status").value(result.status().toString()))
                .andExpect(jsonPath("$.criticality").value(result.criticality().toString()))
                .andExpect(jsonPath("$.serviceId").value(result.serviceId().toString()))
                .andExpect(jsonPath("$.assigneeId").value(nullValue()))
                .andExpect(jsonPath("$.resolvedAt").value(nullValue()));
    }

}
