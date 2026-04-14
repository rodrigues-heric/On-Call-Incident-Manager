package com.rodrigues.heric.incidentmanager.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.rodrigues.heric.incidentmanager.domain.OnCallScheduleEntity;
import com.rodrigues.heric.incidentmanager.domain.UsersEntity;
import com.rodrigues.heric.incidentmanager.dto.OnCallEngineerDTO;
import com.rodrigues.heric.incidentmanager.exception.ResourceNotFoundException;
import com.rodrigues.heric.incidentmanager.mapper.OnCallScheduleMapper;
import com.rodrigues.heric.incidentmanager.repository.OnCallScheduleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OnCallScheduleService {

    private final OnCallScheduleRepository onCallScheduelRepository;
    private final OnCallScheduleMapper onCallScheduleMapper;

    public OnCallEngineerDTO getCurrentOnCallEngineer(UUID serviceId) {
        LocalDateTime now = LocalDateTime.now();
        UsersEntity engineer = onCallScheduelRepository
                .findFirstByServiceIdAndStartTimeBeforeAndEndTimeAfter(serviceId, now, now)
                .map(OnCallScheduleEntity::getEngineer)
                .orElseThrow(
                        () -> new ResourceNotFoundException("No engineer on call for this service at the moment"));
        return this.onCallScheduleMapper.toDTO(engineer);
    }

}
