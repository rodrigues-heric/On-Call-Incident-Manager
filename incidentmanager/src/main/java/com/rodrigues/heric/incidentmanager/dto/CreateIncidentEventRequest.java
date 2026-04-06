package com.rodrigues.heric.incidentmanager.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateIncidentEventRequest(
        @NotBlank(message = "Field ACTOR is required") String actor,

        @NotBlank(message = "Field DESCRIPTION is required") String description) {

}
