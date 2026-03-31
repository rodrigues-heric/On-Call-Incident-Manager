package com.rodrigues.heric.incidentmanager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.rodrigues.heric.incidentmanager.domain.enums.CriticalityEnum;
import com.rodrigues.heric.incidentmanager.domain.enums.IncidentStatusEnum;
import com.rodrigues.heric.incidentmanager.dto.CreateIncidentsRequest;
import com.rodrigues.heric.incidentmanager.dto.IncidentsDTO;
import com.rodrigues.heric.incidentmanager.exception.ResourceNotFoundException;
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

    @Test
    @DisplayName("Should throw Resource Not Found exception when getting incident")
    public void shouldThrowResourceNotFound_whenIncidentDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();

        when(this.incidentsService.getIncidentById(id))
                .thenThrow(new ResourceNotFoundException("Incident with id " + id + " not found"));

        this.mockMvc.perform(get("/incidents/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("Resource Not Found"))
                .andExpect(jsonPath("$.message").value("Incident with id " + id + " not found"))
                .andExpect(jsonPath("$.path").exists());

    }

    // ========== CREATE INCIDENT ==========
    @Test
    @DisplayName("Should create incident successfully")
    public void shouldCreateIncidentSuccessfully() throws Exception {
        String title = "Incident database";
        String description = "Incident description";
        CriticalityEnum criticality = CriticalityEnum.CRITICAL;
        UUID id = UUID.randomUUID();

        String jsonBody = """
                {
                    "title": "%s",
                    "description": "%s",
                    "criticality": "%s",
                    "serviceId": "%s"
                }
                """.formatted(title, description, criticality.toString(), id.toString());

        UUID createdId = UUID.randomUUID();
        IncidentStatusEnum status = IncidentStatusEnum.OPEN;
        IncidentsDTO expectedDTO = new IncidentsDTO(
                createdId,
                title,
                description,
                status,
                criticality,
                id,
                null,
                null);

        when(this.incidentsService.createIncident(any(CreateIncidentsRequest.class))).thenReturn(expectedDTO);

        this.mockMvc.perform(post("/incidents").contentType(MediaType.APPLICATION_JSON).content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedDTO.id().toString()))
                .andExpect(jsonPath("$.title").value(expectedDTO.title()))
                .andExpect(jsonPath("$.description").value(expectedDTO.description()))
                .andExpect(jsonPath("$.status").value(expectedDTO.status().toString()))
                .andExpect(jsonPath("$.criticality").value(expectedDTO.criticality().toString()))
                .andExpect(jsonPath("$.serviceId").value(expectedDTO.serviceId().toString()))
                .andExpect(jsonPath("$.assigneeId").value(nullValue()))
                .andExpect(jsonPath("$.resolvedAt").value(nullValue()));
    }

    @Test
    @DisplayName("Should throw Resource Not Found creating incident with non existing service")
    public void shouldThrowResourceNotFound_whenCreatingIncidentWithNonExistingService() throws Exception {
        String title = "Incident database";
        String description = "Incident description";
        CriticalityEnum criticality = CriticalityEnum.CRITICAL;
        UUID id = UUID.randomUUID();

        String jsonBody = """
                {
                    "title": "%s",
                    "description": "%s",
                    "criticality": "%s",
                    "serviceId": "%s"
                }
                """.formatted(title, description, criticality.toString(), id.toString());

        String serviceId = UUID.randomUUID().toString();
        when(this.incidentsService.createIncident(any(CreateIncidentsRequest.class))).thenThrow(
                new ResourceNotFoundException("Incident with id " + serviceId + " not found"));

        this.mockMvc.perform(post("/incidents").contentType(MediaType.APPLICATION_JSON).content(jsonBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.error").value("Resource Not Found"))
                .andExpect(jsonPath("$.message").value("Incident with id " + serviceId + " not found"))
                .andExpect(jsonPath("$.path").exists());
    }

    // ========== SEARCH INCIDENT ==========
    @Test
    @DisplayName("Should search incidents successfully with all parameters")
    public void shouldSearchIncidentsSuccessfully() throws Exception {
        UUID serviceId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();
        String title = "Database Incident";
        IncidentStatusEnum status = IncidentStatusEnum.OPEN;
        CriticalityEnum criticality = CriticalityEnum.HIGH;

        IncidentsDTO incident1 = new IncidentsDTO(
                UUID.randomUUID(), title, "Description 1", status, criticality, serviceId, assigneeId, null);
        IncidentsDTO incident2 = new IncidentsDTO(
                UUID.randomUUID(), title, "Description 2", status, criticality, serviceId, assigneeId, null);

        when(this.incidentsService.findAllWithFilters(status, serviceId, criticality, assigneeId, title))
                .thenReturn(List.of(incident1, incident2));

        this.mockMvc.perform(get("/incidents/search")
                .param("title", title)
                .param("status", status.toString())
                .param("criticality", criticality.toString())
                .param("serviceId", serviceId.toString())
                .param("assigneeId", assigneeId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value(title))
                .andExpect(jsonPath("$[1].title").value(title));
    }

    @Test
    @DisplayName("Should search incidents by status only")
    public void shouldSearchIncidentsByStatusOnly() throws Exception {
        IncidentStatusEnum status = IncidentStatusEnum.OPEN;
        IncidentsDTO incident = new IncidentsDTO(
                UUID.randomUUID(), "Test", "Desc", status, CriticalityEnum.LOW, UUID.randomUUID(), null, null);

        when(this.incidentsService.findAllWithFilters(status, null, null, null, null))
                .thenReturn(List.of(incident));

        this.mockMvc.perform(get("/incidents/search")
                .param("status", status.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value(status.toString()));
    }

    @Test
    @DisplayName("Should search incidents by criticality only")
    public void shouldSearchIncidentsByCriticalityOnly() throws Exception {
        CriticalityEnum criticality = CriticalityEnum.CRITICAL;
        IncidentsDTO incident = new IncidentsDTO(
                UUID.randomUUID(), "Critical", "Desc", IncidentStatusEnum.OPEN, criticality, UUID.randomUUID(), null,
                null);

        when(this.incidentsService.findAllWithFilters(null, null, criticality, null, null))
                .thenReturn(List.of(incident));

        this.mockMvc.perform(get("/incidents/search")
                .param("criticality", criticality.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].criticality").value(criticality.toString()));
    }

    @Test
    @DisplayName("Should search incidents by serviceId only")
    public void shouldSearchIncidentsByServiceIdOnly() throws Exception {
        UUID serviceId = UUID.randomUUID();
        IncidentsDTO incident = new IncidentsDTO(
                UUID.randomUUID(), "Test", "Desc", IncidentStatusEnum.OPEN, CriticalityEnum.MEDIUM, serviceId, null,
                null);

        when(this.incidentsService.findAllWithFilters(null, serviceId, null, null, null))
                .thenReturn(List.of(incident));

        this.mockMvc.perform(get("/incidents/search")
                .param("serviceId", serviceId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceId").value(serviceId.toString()));
    }

    @Test
    @DisplayName("Should search incidents by assigneeId only")
    public void shouldSearchIncidentsByAssigneeIdOnly() throws Exception {
        UUID assigneeId = UUID.randomUUID();
        IncidentsDTO incident = new IncidentsDTO(
                UUID.randomUUID(), "Test", "Desc", IncidentStatusEnum.OPEN, CriticalityEnum.MEDIUM, UUID.randomUUID(),
                assigneeId, null);

        when(this.incidentsService.findAllWithFilters(null, null, null, assigneeId, null))
                .thenReturn(List.of(incident));

        this.mockMvc.perform(get("/incidents/search")
                .param("assigneeId", assigneeId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assigneeId").value(assigneeId.toString()));
    }

    @Test
    @DisplayName("Should search incidents by title only")
    public void shouldSearchIncidentsByTitleOnly() throws Exception {
        String title = "Server Down";
        IncidentsDTO incident = new IncidentsDTO(
                UUID.randomUUID(), title, "Desc", IncidentStatusEnum.OPEN, CriticalityEnum.CRITICAL, UUID.randomUUID(),
                null, null);

        when(this.incidentsService.findAllWithFilters(null, null, null, null, title))
                .thenReturn(List.of(incident));

        this.mockMvc.perform(get("/incidents/search")
                .param("title", title))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(title));
    }

    @Test
    @DisplayName("Should return empty list when no incidents match search criteria")
    public void shouldReturnEmptyListWhenNoIncidentsMatch() throws Exception {
        when(this.incidentsService.findAllWithFilters(null, null, null, null, null))
                .thenReturn(List.of());

        this.mockMvc.perform(get("/incidents/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
