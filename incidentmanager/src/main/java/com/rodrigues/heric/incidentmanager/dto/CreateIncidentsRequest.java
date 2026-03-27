package com.rodrigues.heric.incidentmanager.dto;

import java.util.UUID;

import com.rodrigues.heric.incidentmanager.domain.enums.CriticalityEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateIncidentsRequest(
        @NotBlank(message = "Field TITLE is required") String title,

        String description,

        @NotNull(message = "Field CRITICALITY is requires") CriticalityEnum criticality,

        @NotNull(message = "Field SERVICEID is required") UUID serviceId) {

}
