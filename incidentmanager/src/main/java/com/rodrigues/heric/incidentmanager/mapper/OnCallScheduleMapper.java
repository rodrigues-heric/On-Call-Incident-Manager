package com.rodrigues.heric.incidentmanager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.rodrigues.heric.incidentmanager.domain.UsersEntity;
import com.rodrigues.heric.incidentmanager.dto.OnCallEngineerDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OnCallScheduleMapper {

    OnCallEngineerDTO toDTO(UsersEntity engineer);

}
