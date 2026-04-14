package com.rodrigues.heric.incidentmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rodrigues.heric.incidentmanager.domain.OnCallScheduleEntity;
import com.rodrigues.heric.incidentmanager.domain.UsersEntity;
import com.rodrigues.heric.incidentmanager.dto.OnCallEngineerDTO;
import com.rodrigues.heric.incidentmanager.exception.ResourceNotFoundException;
import com.rodrigues.heric.incidentmanager.mapper.OnCallScheduleMapper;
import com.rodrigues.heric.incidentmanager.repository.OnCallScheduleRepository;

@ExtendWith(MockitoExtension.class)
class OnCallScheduleServiceTest {

    @Mock
    private OnCallScheduleRepository onCallScheduleRepository;

    @Mock
    private OnCallScheduleMapper onCallScheduleMapper;

    @InjectMocks
    private OnCallScheduleService onCallScheduleService;

    @Test
    @DisplayName("Should return on call engineer")
    void getCurrentOnCallEngineer_Success() {
        UUID serviceId = UUID.randomUUID();
        UsersEntity engineer = new UsersEntity();
        engineer.setName("Heric Rodrigues");

        OnCallScheduleEntity schedule = new OnCallScheduleEntity();
        schedule.setEngineer(engineer);

        OnCallEngineerDTO expectedDto = new OnCallEngineerDTO(
                UUID.randomUUID(), "Heric Rodrigues", "heric@example.com", "123456789");

        when(onCallScheduleRepository.findFirstByServiceIdAndStartTimeBeforeAndEndTimeAfter(
                eq(serviceId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(schedule));

        when(onCallScheduleMapper.toDTO(engineer)).thenReturn(expectedDto);

        OnCallEngineerDTO result = onCallScheduleService.getCurrentOnCallEngineer(serviceId);

        assertNotNull(result);
        assertEquals(expectedDto.name(), result.name());
        verify(onCallScheduleRepository, times(1))
                .findFirstByServiceIdAndStartTimeBeforeAndEndTimeAfter(eq(serviceId), any(LocalDateTime.class),
                        any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when no engineer on call")
    void getCurrentOnCallEngineer_NotFound() {
        UUID serviceId = UUID.randomUUID();

        when(onCallScheduleRepository.findFirstByServiceIdAndStartTimeBeforeAndEndTimeAfter(
                eq(serviceId), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            onCallScheduleService.getCurrentOnCallEngineer(serviceId);
        });

        verify(onCallScheduleMapper, never()).toDTO(any());
    }
}