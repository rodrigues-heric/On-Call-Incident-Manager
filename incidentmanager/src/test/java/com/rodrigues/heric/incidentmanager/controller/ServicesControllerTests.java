package com.rodrigues.heric.incidentmanager.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.rodrigues.heric.incidentmanager.domain.enums.CriticalityEnum;
import com.rodrigues.heric.incidentmanager.dto.CreateServicesRequest;
import com.rodrigues.heric.incidentmanager.dto.OnCallEngineerDTO;
import com.rodrigues.heric.incidentmanager.dto.ServicesDTO;
import com.rodrigues.heric.incidentmanager.exception.BusinessException;
import com.rodrigues.heric.incidentmanager.exception.ResourceNotFoundException;
import com.rodrigues.heric.incidentmanager.service.OnCallScheduleService;
import com.rodrigues.heric.incidentmanager.service.ServicesService;

@WebMvcTest(ServicesController.class)
@ActiveProfiles("test")
public class ServicesControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServicesService servicesService;
    @MockitoBean
    private OnCallScheduleService onCallScheduleService;

    @Test
    @DisplayName("Should get all services successfully")
    public void shouldGetAllServicesSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();
        String name = "Database";
        String team = "Backend";
        CriticalityEnum criticality = CriticalityEnum.HIGH;
        ServicesDTO serviceDTO = new ServicesDTO(id, name, team, criticality);

        when(this.servicesService.listAllServices()).thenReturn(List.of(serviceDTO));

        this.mockMvc.perform(get("/services/all").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].name").value(serviceDTO.name()))
                .andExpect(jsonPath("$[0].team").value(serviceDTO.team()))
                .andExpect(jsonPath("$[0].criticality").value(serviceDTO.criticality().toString()));
    }

    @Test
    @DisplayName("Should get empty list of all services successfully")
    public void shouldReturnEmptyListOfServicesSuccessfully() throws Exception {
        when(this.servicesService.listAllServices()).thenReturn(List.of());

        this.mockMvc.perform(get("/services/all").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    @DisplayName("Should create a service successfully")
    public void shouldCreateServiceSucessfully() throws Exception {
        String name = "Docker";
        String team = "DevOps";
        CriticalityEnum criticality = CriticalityEnum.MEDIUM;
        String jsonBody = """
                {
                    "name": "%s",
                    "team": "%s",
                    "criticality": "%s"
                }
                """.formatted(name, team, criticality.toString());
        UUID id = UUID.randomUUID();
        ServicesDTO response = new ServicesDTO(id, name, team, criticality);

        when(this.servicesService.createService(any(CreateServicesRequest.class))).thenReturn(response);

        this.mockMvc.perform(post("/services").contentType(MediaType.APPLICATION_JSON).content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id().toString()))
                .andExpect(jsonPath("$.name").value(response.name()))
                .andExpect(jsonPath("$.team").value(response.team()))
                .andExpect(jsonPath("$.criticality").value(response.criticality().toString()));
    }

    @Test
    @DisplayName("Should throw Business Rule Exception when creating repeated service")
    public void shouldThrowBusinessException_whenCreatingRepeatedService() throws Exception {
        String name = "Docker";
        String team = "DevOps";
        CriticalityEnum criticality = CriticalityEnum.MEDIUM;
        String jsonBody = """
                {
                    "name": "%s",
                    "team": "%s",
                    "criticality": "%s"
                }
                """.formatted(name, team, criticality.toString());

        when(this.servicesService.createService(any(CreateServicesRequest.class)))
                .thenThrow(new BusinessException("Service " + name + " already in use"));

        this.mockMvc.perform(post("/services").contentType(MediaType.APPLICATION_JSON).content(jsonBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Business Rule Violation"))
                .andExpect(jsonPath("$.message").value("Service " + name + " already in use"))
                .andExpect(jsonPath("$.path").value("/services"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should return 200 OK and the engineer data")
    void getCurrentOnCall_Success() throws Exception {
        UUID serviceId = UUID.randomUUID();
        OnCallEngineerDTO expectedResponse = new OnCallEngineerDTO(
                UUID.randomUUID(),
                "Heric Rodrigues",
                "heric@example.com",
                "5511999999999");

        when(onCallScheduleService.getCurrentOnCallEngineer(serviceId))
                .thenReturn(expectedResponse);

        mockMvc.perform(get("/services/{id}/oncall", serviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Heric Rodrigues"))
                .andExpect(jsonPath("$.email").value("heric@example.com"))
                .andExpect(jsonPath("$.phone").value("5511999999999"));
    }

    @Test
    @DisplayName("Should return 404 Not Found when service launches ResourceNotFoundException")
    void getCurrentOnCall_NotFound() throws Exception {
        UUID serviceId = UUID.randomUUID();

        when(onCallScheduleService.getCurrentOnCallEngineer(serviceId))
                .thenThrow(new ResourceNotFoundException("No engineer on call for this service at the moment"));

        mockMvc.perform(get("/services/{id}/oncall", serviceId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

}
