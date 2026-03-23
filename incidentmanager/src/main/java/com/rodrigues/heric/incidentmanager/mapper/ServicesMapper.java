package com.rodrigues.heric.incidentmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.rodrigues.heric.incidentmanager.domain.ServicesEntity;
import com.rodrigues.heric.incidentmanager.dto.CreateServicesRequest;
import com.rodrigues.heric.incidentmanager.dto.ServicesDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ServicesMapper {

    ServicesDTO toDTO(ServicesEntity service);

    @Mapping(target = "id", ignore = true)
    ServicesEntity toEntity(CreateServicesRequest request);

}
