package com.rodrigues.heric.incidentmanager.dto;

import com.rodrigues.heric.incidentmanager.domain.enums.IncidentStatusEnum;

import jakarta.validation.constraints.NotNull;

public record PatchIncidentRequest(
        @NotNull(message = "Status is required") IncidentStatusEnum status) {

}
