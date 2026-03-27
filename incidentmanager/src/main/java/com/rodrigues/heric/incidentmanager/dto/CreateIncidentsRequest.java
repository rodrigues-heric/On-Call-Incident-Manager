package com.rodrigues.heric.incidentmanager.dto;

import com.rodrigues.heric.incidentmanager.domain.ServicesEntity;
import com.rodrigues.heric.incidentmanager.domain.UsersEntity;
import com.rodrigues.heric.incidentmanager.domain.enums.CriticalityEnum;
import com.rodrigues.heric.incidentmanager.domain.enums.IncidentStatusEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateIncidentsRequest(
        @NotBlank(message = "Field TITLE is required") String title,

        String description,

        @NotNull(message = "Field STATUS is required") IncidentStatusEnum status,

        @NotNull(message = "Field CRITICALITY is requires") CriticalityEnum criticality,

        @NotNull(message = "Field SERFVICE is required") ServicesEntity service,

        UsersEntity assignee) {

}
