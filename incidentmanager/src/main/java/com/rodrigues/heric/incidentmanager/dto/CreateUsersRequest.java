package com.rodrigues.heric.incidentmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUsersRequest(
        @NotBlank(message = "Field NAME is required") String name,

        @NotBlank(message = "Field EMAIL is required") @Email(message = "EMAIL must be valid") String email,

        String phone) {

}
