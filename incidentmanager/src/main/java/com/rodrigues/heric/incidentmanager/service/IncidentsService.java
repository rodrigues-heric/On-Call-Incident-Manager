package com.rodrigues.heric.incidentmanager.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.rodrigues.heric.incidentmanager.domain.IncidentsEntity;
import com.rodrigues.heric.incidentmanager.domain.ServicesEntity;
import com.rodrigues.heric.incidentmanager.domain.enums.CriticalityEnum;
import com.rodrigues.heric.incidentmanager.domain.enums.IncidentStatusEnum;
import com.rodrigues.heric.incidentmanager.dto.CreateIncidentsRequest;
import com.rodrigues.heric.incidentmanager.dto.IncidentsDTO;
import com.rodrigues.heric.incidentmanager.exception.ResourceNotFoundException;
import com.rodrigues.heric.incidentmanager.mapper.IncidentsMapper;
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

    private final IncidentsMapper incidentsMapper;

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

    private ServicesEntity findServiceById(UUID id) {
        return this.servicesRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Service with id " + id.toString() + " not found"));
    }

}
