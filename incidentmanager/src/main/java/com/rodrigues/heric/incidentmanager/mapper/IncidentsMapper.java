package com.rodrigues.heric.incidentmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.rodrigues.heric.incidentmanager.domain.IncidentsEntity;
import com.rodrigues.heric.incidentmanager.dto.CreateIncidentsRequest;
import com.rodrigues.heric.incidentmanager.dto.IncidentsDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IncidentsMapper {

    @Mapping(target = "serviceId", source = "service.id")
    @Mapping(target = "assigneeId", source = "assignee.id")
    IncidentsDTO toDTO(IncidentsEntity incidentsEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    IncidentsEntity toEntity(CreateIncidentsRequest request);

}
