package com.rodrigues.heric.incidentmanager.dto;

import java.util.UUID;

public record UsersDTO(
        UUID id,
        String name,
        String email,
        String phone) {

}
