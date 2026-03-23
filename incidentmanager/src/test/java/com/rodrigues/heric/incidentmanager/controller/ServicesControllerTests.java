package com.rodrigues.heric.incidentmanager.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.rodrigues.heric.incidentmanager.dto.ServicesDTO;
import com.rodrigues.heric.incidentmanager.service.ServicesService;

@WebMvcTest(ServicesController.class)
@ActiveProfiles("test")
public class ServicesControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ServicesService servicesService;

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

}
