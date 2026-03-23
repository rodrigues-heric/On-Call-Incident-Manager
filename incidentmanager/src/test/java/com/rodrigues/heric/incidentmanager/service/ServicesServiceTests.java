package com.rodrigues.heric.incidentmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

}
