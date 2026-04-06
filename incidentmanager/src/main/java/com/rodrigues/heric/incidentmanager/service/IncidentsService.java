package com.rodrigues.heric.incidentmanager.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.rodrigues.heric.incidentmanager.domain.IncidentEventsEntity;
import com.rodrigues.heric.incidentmanager.domain.IncidentsEntity;
import com.rodrigues.heric.incidentmanager.domain.ServicesEntity;
import com.rodrigues.heric.incidentmanager.domain.enums.CriticalityEnum;
import com.rodrigues.heric.incidentmanager.domain.enums.IncidentEventsEnum;
import com.rodrigues.heric.incidentmanager.domain.enums.IncidentStatusEnum;
import com.rodrigues.heric.incidentmanager.domain.state.IncidentStateMachine;
import com.rodrigues.heric.incidentmanager.dto.CreateIncidentEventRequest;
import com.rodrigues.heric.incidentmanager.dto.CreateIncidentsRequest;
import com.rodrigues.heric.incidentmanager.dto.IncidentsDTO;
import com.rodrigues.heric.incidentmanager.exception.ResourceNotFoundException;
import com.rodrigues.heric.incidentmanager.mapper.IncidentsMapper;
import com.rodrigues.heric.incidentmanager.repository.IncidentEventsRepository;
import com.rodrigues.heric.incidentmanager.repository.IncidentsRepository;
import com.rodrigues.heric.incidentmanager.repository.ServicesRepository;
import com.rodrigues.heric.incidentmanager.specification.IncidentsSpecification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncidentsService {

    private final IncidentsRepository incidentsRepository;
    private final ServicesRepository servicesRepository;
    private final IncidentEventsRepository incidentEventsRepository;

    private final IncidentsMapper incidentsMapper;

    private final IncidentStateMachine stateMachine;

    @Transactional
    public IncidentsDTO createIncident(CreateIncidentsRequest request) {
        ServicesEntity service = this.findServiceById(request.serviceId());

        IncidentsEntity incident = this.incidentsMapper.toEntity(request);
        incident.setService(service);
        incident.setStatus(IncidentStatusEnum.OPEN);

        IncidentsEntity savedIncident = this.incidentsRepository.save(incident);
        IncidentsDTO incidentDTO = this.incidentsMapper.toDTO(savedIncident);
        return incidentDTO;
    }

    @Transactional
    public IncidentsDTO getIncidentById(UUID id) {
        return this.incidentsRepository.findById(id)
                .map(this.incidentsMapper::toDTO)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Incident with id " + id + " not found"));
    }

    @Transactional
    public List<IncidentsDTO> findAllWithFilters(
            IncidentStatusEnum status,
            UUID serviceId,
            CriticalityEnum criticality,
            UUID assigneeId,
            String title) {

        Specification<IncidentsEntity> spec = IncidentsSpecification.withFilters(
                status, serviceId, criticality, assigneeId, title);

        List<IncidentsEntity> incidents = this.incidentsRepository.findAll(spec);

        return incidents.stream()
                .map(incidentsMapper::toDTO)
                .toList();
    }

    @Transactional
    public IncidentsDTO updateIncidentStatus(UUID id, IncidentStatusEnum status) {
        IncidentsEntity incidentEntity = this.getIncidentsEntity(id);

        this.stateMachine.validateTransition(incidentEntity.getStatus(), status);
        incidentEntity.setStatus(status);
        this.setResolvedAt(incidentEntity, status);

        IncidentsEntity savedEntity = this.incidentsRepository.save(incidentEntity);
        IncidentsDTO incidentDTO = this.incidentsMapper.toDTO(savedEntity);

        IncidentStatusEnum oldStatus = incidentEntity.getStatus();
        this.createEvent(savedEntity, IncidentEventsEnum.STATE_CHANGE, "SYSTEM",
                "Status changed from " + oldStatus + " to " + status);

        return incidentDTO;
    }

    @Transactional
    public void addManualNote(UUID id, CreateIncidentEventRequest request) {
        IncidentsEntity incident = this.getIncidentsEntity(id);
        this.createEvent(incident, IncidentEventsEnum.NOTE, request.actor(), request.description());
    }

    private void setResolvedAt(IncidentsEntity entity, IncidentStatusEnum status) {
        if (status == IncidentStatusEnum.RESOLVED)
            entity.setResolvedAt(LocalDateTime.now());
    }

    private IncidentsEntity getIncidentsEntity(UUID id) {
        return this.incidentsRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Incident with id " + id + " not found"));
    }

    private ServicesEntity findServiceById(UUID id) {
        return this.servicesRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Service with id " + id.toString() + " not found"));
    }

    private void createEvent(IncidentsEntity incidentEntity, IncidentEventsEnum incidentType, String actor,
            String description) {
        IncidentEventsEntity event = IncidentEventsEntity.builder()
                .incident(incidentEntity)
                .type(incidentType)
                .actor(actor)
                .description(description)
                .build();
        this.incidentEventsRepository.save(event);
    }

}
