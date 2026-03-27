package com.rodrigues.heric.incidentmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rodrigues.heric.incidentmanager.domain.IncidentsEntity;
import com.rodrigues.heric.incidentmanager.domain.ServicesEntity;
import com.rodrigues.heric.incidentmanager.domain.enums.CriticalityEnum;
import com.rodrigues.heric.incidentmanager.domain.enums.IncidentStatusEnum;
import com.rodrigues.heric.incidentmanager.dto.CreateIncidentsRequest;
import com.rodrigues.heric.incidentmanager.dto.IncidentsDTO;
import com.rodrigues.heric.incidentmanager.mapper.IncidentsMapper;
import com.rodrigues.heric.incidentmanager.repository.IncidentsRepository;
import com.rodrigues.heric.incidentmanager.repository.ServicesRepository;
import com.rodrigues.heric.incidentmanager.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
public class IncidentsServiceTests {

    @Mock
    IncidentsRepository incidentsRepository;
    @Mock
    ServicesRepository servicesRepository;
    @Mock
    UsersRepository usersRepository;
    @Mock
    IncidentsMapper incidentsMapper;

    @InjectMocks
    IncidentsService incidentsService;

    @Test
    @DisplayName("Should create incident successfully")
    public void shouldCreateIncidentSuccessfully() {
        UUID incidentId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        String title = "Incident title";
        String description = "Incident description";
        CriticalityEnum criticality = CriticalityEnum.CRITICAL;

        ServicesEntity service = ServicesEntity.builder()
                .id(serviceId)
                .name("Service Test")
                .build();

        CreateIncidentsRequest request = new CreateIncidentsRequest(
                title,
                description,
                criticality,
                serviceId);

        when(servicesRepository.findById(serviceId)).thenReturn(Optional.of(service));

        IncidentsEntity incidentsEntity = IncidentsEntity.builder()
                .title(title)
                .description(description)
                .criticality(criticality)
                .service(service)
                .status(IncidentStatusEnum.OPEN)
                .build();

        IncidentsEntity savedIncident = IncidentsEntity.builder()
                .id(incidentId)
                .title(title)
                .description(description)
                .criticality(criticality)
                .service(service)
                .status(IncidentStatusEnum.OPEN)
                .build();

        IncidentsDTO expectedDTO = new IncidentsDTO(
                incidentId,
                title,
                description,
                IncidentStatusEnum.OPEN,
                criticality,
                serviceId,
                null,
                null);

        when(incidentsMapper.toEntity(request)).thenReturn(incidentsEntity);
        when(incidentsRepository.save(incidentsEntity)).thenReturn(savedIncident);
        when(incidentsMapper.toDTO(savedIncident)).thenReturn(expectedDTO);

        IncidentsDTO result = incidentsService.createIncident(request);

        assertNotNull(result);
        assertEquals(expectedDTO.id(), result.id());
        assertEquals(expectedDTO.title(), result.title());
        assertEquals(expectedDTO.description(), result.description());
        assertEquals(expectedDTO.status(), result.status());
        assertEquals(expectedDTO.criticality(), result.criticality());
        assertEquals(expectedDTO.serviceId(), result.serviceId());
        assertEquals(expectedDTO.assigneeId(), result.assigneeId());
        assertEquals(expectedDTO.resolverAt(), result.resolverAt());

        verify(servicesRepository, times(1)).findById(serviceId);
        verify(incidentsMapper, times(1)).toEntity(request);
        verify(incidentsRepository, times(1)).save(incidentsEntity);
        verify(incidentsMapper, times(1)).toDTO(savedIncident);
    }

}
