package com.rodrigues.heric.incidentmanager.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.rodrigues.heric.incidentmanager.domain.enums.CriticalityEnum;
import com.rodrigues.heric.incidentmanager.domain.enums.IncidentStatusEnum;

public record IncidentsDTO(
                UUID id,
                String title,
                String description,
                IncidentStatusEnum status,
                CriticalityEnum criticality,
                UUID serviceId,
                UUID assigneeId,
                LocalDateTime resolverAt) {

}
