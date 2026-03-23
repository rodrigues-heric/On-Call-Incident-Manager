package com.rodrigues.heric.incidentmanager.dto;

import com.rodrigues.heric.incidentmanager.domain.enums.CriticalityEnum;

import jakarta.validation.constraints.NotBlank;

public record CreateServicesRequest(
        @NotBlank(message = "Field NAME is required") String name,

        @NotBlank(message = "Field TEAM is required") String team,

        @NotBlank(message = "Field CRITICALITY is required") CriticalityEnum criticality) {

}