package com.rodrigues.heric.incidentmanager.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.rodrigues.heric.incidentmanager.domain.IncidentsEntity;
import com.rodrigues.heric.incidentmanager.domain.ServicesEntity;
import com.rodrigues.heric.incidentmanager.domain.UsersEntity;
import com.rodrigues.heric.incidentmanager.dto.CreateIncidentsRequest;
import com.rodrigues.heric.incidentmanager.dto.IncidentsDTO;
import com.rodrigues.heric.incidentmanager.exception.ResourceNotFoundException;
import com.rodrigues.heric.incidentmanager.mapper.IncidentsMapper;
import com.rodrigues.heric.incidentmanager.repository.IncidentsRepository;
import com.rodrigues.heric.incidentmanager.repository.ServicesRepository;
import com.rodrigues.heric.incidentmanager.repository.UsersRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncidentsService {

    private final IncidentsRepository incidentsRepository;
    private final ServicesRepository servicesRepository;
    private final UsersRepository usersRepository;

    private final IncidentsMapper incidentsMapper;

    @Transactional
    public IncidentsDTO createIncident(CreateIncidentsRequest request) {
        ServicesEntity service = this.findServiceById(request.serviceId());
        UsersEntity assignee = this.findAssigneeById(request.assigneeId());

        IncidentsEntity incident = IncidentsEntity.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status())
                .criticality(request.criticality())
                .service(service)
                .assignee(assignee)
                .build();
        IncidentsEntity savedIncident = this.incidentsRepository.save(incident);
        return this.incidentsMapper.toDTO(savedIncident);
    }

    private ServicesEntity findServiceById(UUID id) {
        return this.servicesRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Service with id " + id.toString() + " not found"));
    }

    private UsersEntity findAssigneeById(UUID id) {
        if (id == null)
            return null;

        return this.usersRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User with id " + id.toString() + " not found"));
    }

}
