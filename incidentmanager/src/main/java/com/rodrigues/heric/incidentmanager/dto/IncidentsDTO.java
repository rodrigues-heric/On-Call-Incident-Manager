package com.rodrigues.heric.incidentmanager.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.rodrigues.heric.incidentmanager.domain.ServicesEntity;
import com.rodrigues.heric.incidentmanager.domain.UsersEntity;
import com.rodrigues.heric.incidentmanager.domain.enums.CriticalityEnum;
import com.rodrigues.heric.incidentmanager.domain.enums.IncidentStatusEnum;

public record IncidentsDTO(
        UUID id,
        String title,
        String description,
        IncidentStatusEnum status,
        CriticalityEnum criticality,
        ServicesEntity service,
        UsersEntity assignee,
        LocalDateTime resolverAt) {

}
