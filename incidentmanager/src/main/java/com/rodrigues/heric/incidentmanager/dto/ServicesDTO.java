package com.rodrigues.heric.incidentmanager.dto;

import java.util.UUID;

import com.rodrigues.heric.incidentmanager.domain.enums.CriticalityEnum;

public record ServicesDTO(
        UUID id,
        String name,
        String team,
        CriticalityEnum criticality) {

}
