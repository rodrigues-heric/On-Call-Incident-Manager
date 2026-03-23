package com.rodrigues.heric.incidentmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rodrigues.heric.incidentmanager.domain.ServicesEntity;
import com.rodrigues.heric.incidentmanager.domain.enums.CriticalityEnum;
import com.rodrigues.heric.incidentmanager.dto.CreateServicesRequest;
import com.rodrigues.heric.incidentmanager.dto.ServicesDTO;
import com.rodrigues.heric.incidentmanager.exception.BusinessException;
import com.rodrigues.heric.incidentmanager.mapper.ServicesMapper;
import com.rodrigues.heric.incidentmanager.repository.ServicesRepository;

@ExtendWith(MockitoExtension.class)
public class ServicesServiceTests {

    @Mock
    ServicesRepository servicesRepository;
    @Mock
    ServicesMapper servicesMapper;

    @InjectMocks
    ServicesService servicesService;

    @Test
    @DisplayName("Should create Service successfully")
    public void shouldCreateServiceSuccessfully() {
        String name = "Foo Service";
        String team = "Backend";
        CriticalityEnum criticality = CriticalityEnum.MEDIUM;

        CreateServicesRequest request = new CreateServicesRequest(name, team, criticality);
        ServicesEntity serviceEntity = new ServicesEntity();
        ServicesEntity savedEntity = new ServicesEntity();
        ServicesDTO expectedDTO = new ServicesDTO(UUID.randomUUID(), name, team, criticality);

        when(this.servicesRepository.findByName(request.name())).thenReturn(Optional.empty());
        when(this.servicesMapper.toEntity(request)).thenReturn(serviceEntity);
        when(this.servicesRepository.save(serviceEntity)).thenReturn(savedEntity);
        when(this.servicesMapper.toDTO(savedEntity)).thenReturn(expectedDTO);

        ServicesDTO result = this.servicesService.createService(request);

        assertNotNull(result);
        assertEquals(expectedDTO.name(), result.name());
        assertEquals(expectedDTO.team(), result.team());
        assertEquals(expectedDTO.criticality(), result.criticality());

        verify(this.servicesRepository, times(1)).save(serviceEntity);
    }

    @Test
    @DisplayName("Should throw Business Logic Exception when name already in use")
    public void shouldThrowBusinessLogicExceptionWhenNameAlreadyInUse() {
        String name = "Foo Service";
        String team = "Backend";
        CriticalityEnum criticality = CriticalityEnum.MEDIUM;
        CreateServicesRequest request = new CreateServicesRequest(name, team, criticality);

        when(this.servicesRepository.findByName(name)).thenReturn(Optional.of(new ServicesEntity()));
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            this.servicesService.createService(request);
        });

        assertEquals("Service " + name + " already in use", exception.getMessage());

        verify(this.servicesRepository).findByName(name);
        verify(this.servicesMapper, never()).toEntity(any());
        verify(this.servicesRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get all services successfully")
    public void shouldGetAllServicesSuccessfully() {
        UUID id = UUID.randomUUID();
        String name = "Database";
        String team = "Infra";
        CriticalityEnum criticality = CriticalityEnum.CRITICAL;

        ServicesEntity serviceEntity = ServicesEntity.builder()
                .id(id)
                .name(name)
                .team(team)
                .criticality(criticality)
                .build();
        ServicesDTO serviceDTO = new ServicesDTO(id, name, team, criticality);

        when(this.servicesRepository.findAll()).thenReturn(List.of(serviceEntity));
        when(this.servicesMapper.toDTO(serviceEntity)).thenReturn(serviceDTO);

        List<ServicesDTO> result = this.servicesService.listAllServices();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(name, result.get(0).name());
        assertEquals(team, result.get(0).team());
        assertEquals(criticality, result.get(0).criticality());

        verify(this.servicesRepository, times(1)).findAll();
        verify(this.servicesMapper, times(1)).toDTO(serviceEntity);
    }

    @Test
    @DisplayName("Should return empty list of services")
    public void shouldReturnEmptyListOfServices() {
        when(this.servicesRepository.findAll()).thenReturn(List.of());

        List<ServicesDTO> result = this.servicesService.listAllServices();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(this.servicesRepository, times(1)).findAll();
        verifyNoInteractions(this.servicesMapper);
    }

}
